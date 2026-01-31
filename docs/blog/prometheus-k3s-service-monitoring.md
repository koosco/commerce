# Docker Prometheus에서 k3s 서비스 메트릭 수집하기

## 문제 정의

분산 시스템을 운영하다 보면 각 서비스의 상태를 한눈에 파악하는 것이 중요합니다. 이 프로젝트는 6개의 Spring Boot 서비스(Auth, User, Catalog, Inventory, Order, Payment)를 k3s 클러스터에서 운영하고 있으며, 모니터링 스택(Prometheus, Grafana)은 Docker Compose로 별도 관리하고 있습니다.

문제는 Prometheus가 Docker 컨테이너로 실행되고, 수집 대상 서비스는 k3s 내부의 ClusterIP로만 접근 가능하다는 점입니다. 일반적으로 Docker 컨테이너에서 k8s ClusterIP에 직접 접근할 수 없다고 알려져 있지만, k3s는 호스트 네트워크 스택에 ClusterIP 라우팅을 직접 주입하는 특성이 있습니다. 즉, 같은 호스트에서 실행되는 Docker 컨테이너가 ClusterIP로 직접 접근할 수 있습니다.

이 특성을 확인한 뒤, 아래 요구사항을 정리했습니다.

- Docker Compose의 Prometheus에서 k3s 내 6개 서비스의 `/actuator/prometheus` 메트릭을 수집할 것
- 기존 Grafana 대시보드에서 바로 확인할 수 있을 것
- ClusterIP가 변경되더라도 Prometheus 재시작 없이 반영될 것
- 기존 모니터링 스택 설정을 최소한으로 변경할 것

## 대안책

### 1. ServiceMonitor + kube-prometheus-stack

k8s 네이티브 방식으로, Prometheus Operator와 ServiceMonitor CRD를 사용하여 자동으로 타겟을 발견하는 방법입니다. 가장 정석적인 접근이지만, 기존 Docker Compose 기반 모니터링 스택을 전면 교체해야 합니다. 이미 Node Exporter, Loki, Promtail 등이 Docker Compose로 운영되고 있어 마이그레이션 비용이 큽니다.

### 2. Prometheus 자체를 k3s로 이전

Prometheus를 k3s 내부에 배포하면 서비스 디스커버리를 네이티브로 사용할 수 있습니다. 하지만 Grafana, Loki, Promtail 등 나머지 모니터링 컴포넌트와의 네트워크 연결을 재구성해야 하고, 기존 대시보드 설정이 유실될 수 있습니다.

### 3. file_sd_configs로 타겟 파일 관리 (선택)

Prometheus의 `file_sd_configs`를 사용하여 JSON 파일로 scrape 타겟을 관리하는 방법입니다. 파일이 변경되면 Prometheus가 자동으로 감지하여 타겟을 갱신합니다.

이 방법을 선택한 이유는 다음과 같습니다.

- **기존 스택 변경 최소화**: `prometheus.yml`에 job 하나만 추가하면 됩니다
- **Prometheus 재시작 불필요**: 파일 변경 시 `refresh_interval` 내에 자동 반영됩니다
- **운영 편의성**: ClusterIP가 변경되면 JSON 파일만 수정하면 됩니다
- **관심사 분리**: 타겟 정보가 commerce 프로젝트 내에 위치하므로 서비스 코드와 함께 관리됩니다

## 해결 과정

### 전체 구조 설계

Prometheus가 k3s의 ClusterIP를 통해 각 서비스의 Actuator 엔드포인트에 접근하는 구조입니다.

```
Prometheus (Docker)
  → file_sd_configs (JSON 파일 읽기)
    → 10.43.x.x:80/actuator/prometheus (k3s ClusterIP)
      → 각 서비스 Pod (Spring Boot Actuator)
```

`file_sd_configs`는 Prometheus의 동적 타겟 발견 메커니즘 중 하나로, 지정한 디렉토리의 JSON 파일을 주기적으로 읽어 scrape 타겟을 갱신합니다. `static_configs`와 달리 설정 변경 시 Prometheus를 재시작할 필요가 없다는 장점이 있습니다.

### 단계 1: Prometheus scrape job 추가

기존 `prometheus.yml`에 `commerce_services` job을 추가합니다.

```yaml
scrape_configs:
  # ... 기존 job 유지

  # Commerce k3s services
  - job_name: "commerce_services"
    file_sd_configs:
      - files: ["/etc/prometheus/commerce-targets/*.json"]
        refresh_interval: 30s
    metrics_path: "/actuator/prometheus"
    scrape_interval: 10s
    scrape_timeout: 5s
```

기존에 블로그 서비스를 위한 `file_sd_targets` job이 있었는데, 이 job은 `metrics_path`가 `/api/actuator/prometheus`로 설정되어 있습니다. Spring Boot의 기본 Actuator 경로는 `/actuator/prometheus`이므로 별도 job으로 분리했습니다. 같은 job에 넣으면 `metrics_path`를 공유하게 되어, 경로가 다른 서비스는 수집에 실패합니다.

### 단계 2: docker-compose 볼륨 마운트 추가

Prometheus 컨테이너에서 타겟 파일을 읽을 수 있도록 볼륨을 마운트합니다.

```yaml
prometheus:
  image: prom/prometheus:latest
  volumes:
    - ./prometheus/prometheus.yml:/etc/prometheus/prometheus.yml:ro
    - /srv/docker/data/prometheus:/prometheus
    - /srv/docker/stacks/blog/prometheus/targets:/etc/prometheus/targets:ro
    # commerce 서비스 타겟 추가
    - /srv/docker/stacks/commerce/infra/prometheus/targets:/etc/prometheus/commerce-targets:ro
```

타겟 파일을 commerce 프로젝트 내(`infra/prometheus/targets/`)에 배치했습니다. 이렇게 하면 서비스 코드와 모니터링 설정을 같은 저장소에서 관리할 수 있습니다.

### 단계 3: file_sd 타겟 JSON 파일 생성

각 서비스별로 별도의 JSON 파일을 생성합니다. 하나의 파일에 6개 타겟을 모두 넣을 수도 있지만, 서비스별로 분리하면 개별 서비스의 타겟 변경이 다른 서비스에 영향을 주지 않습니다.

```json
[
  {
    "targets": ["10.43.168.196:80"],
    "labels": {
      "application": "auth-service",
      "environment": "production",
      "namespace": "commerce"
    }
  }
]
```

`labels`에 `application`, `environment`, `namespace`를 지정하여 Grafana에서 필터링과 그룹화에 활용할 수 있게 했습니다. 특히 `application` 레이블은 Spring Boot 대시보드에서 서비스를 구분하는 기본 키로 사용됩니다.

ClusterIP는 `kubectl get svc -n commerce`로 확인할 수 있습니다.

```bash
$ kubectl get svc -n commerce
NAME                TYPE        CLUSTER-IP      PORT(S)
auth-service        ClusterIP   10.43.168.196   80/TCP
user-service        ClusterIP   10.43.201.159   80/TCP
catalog-service     ClusterIP   10.43.67.150    80/TCP
inventory-service   ClusterIP   10.43.40.28     80/TCP
order-service       ClusterIP   10.43.131.153   80/TCP
payment-service     ClusterIP   10.43.236.134   80/TCP
```

ClusterIP가 변경될 때를 대비하여 갱신 스크립트도 함께 작성했습니다.

```bash
#!/bin/bash
# update-targets.sh - k3s ClusterIP로 타겟 파일 자동 갱신
SERVICES=("auth-service" "user-service" "catalog-service"
          "inventory-service" "order-service" "payment-service")

for SERVICE in "${SERVICES[@]}"; do
  CLUSTER_IP=$(kubectl get svc "$SERVICE" -n commerce -o jsonpath='{.spec.clusterIP}')
  cat > "targets/$SERVICE.json" <<EOF
[
  {
    "targets": ["${CLUSTER_IP}:80"],
    "labels": {
      "application": "${SERVICE}",
      "environment": "production",
      "namespace": "commerce"
    }
  }
]
EOF
  echo "  OK: $SERVICE -> $CLUSTER_IP:80"
done
```

### 단계 4: 배포 및 검증

볼륨 마운트가 변경되었으므로 Prometheus 컨테이너를 재생성합니다.

```bash
cd /srv/docker/stacks/monitoring
docker compose up -d prometheus
```

`docker compose up -d`는 설정 변경을 감지하면 컨테이너를 자동으로 재생성합니다. 이후 변경 사항은 JSON 파일만 수정하면 30초 내에 반영되므로, 이 단계는 최초 설정 시에만 필요합니다.

배포 후 Prometheus API로 타겟 상태를 확인합니다.

```bash
$ curl -s http://localhost:9090/api/v1/targets | python3 -c "
import json, sys
data = json.load(sys.stdin)
for t in data['data']['activeTargets']:
  labels = t.get('labels', {})
  if 'commerce' in labels.get('namespace', ''):
    print(f\"{labels['application']:25s} {t['health']}\")"

auth-service              up
user-service              up
catalog-service           up
inventory-service         up
payment-service           up
order-service             down
```

5개 서비스는 정상이었지만, order-service만 `down` 상태였습니다.

### 트러블슈팅: order-service 500 에러

order-service의 `/actuator/prometheus` 엔드포인트가 HTTP 500을 반환하고 있었습니다. Pod 로그를 확인하니 원인은 명확했습니다.

```
WARN  o.s.web.servlet.PageNotFound : No mapping for GET /actuator/prometheus
ERROR c.k.c.c.e.GlobalExceptionHandler : Unexpected error occurred
org.springframework.web.servlet.NoHandlerFoundException:
  No endpoint GET /actuator/prometheus.
```

`/actuator/prometheus` 엔드포인트 자체가 등록되지 않은 것입니다. 다른 5개 서비스와 비교해 보니 order-service에 두 가지가 누락되어 있었습니다.

**첫째, Micrometer Prometheus 의존성 누락**입니다.

```kotlin
// build.gradle.kts - 다른 5개 서비스에는 모두 있지만 order-service에만 없었음
runtimeOnly("io.micrometer:micrometer-registry-prometheus")
```

Spring Boot Actuator는 `spring-boot-starter-actuator`만으로 기본적인 health, info 엔드포인트를 제공합니다. 하지만 `/actuator/prometheus` 엔드포인트는 `micrometer-registry-prometheus`가 classpath에 있어야 자동 구성됩니다. 이 의존성이 없으면 Prometheus 형식으로 메트릭을 노출하는 엔드포인트가 아예 생성되지 않습니다.

**둘째, Actuator 설정 블록 누락**입니다.

```yaml
# application.yaml - 다른 서비스에는 있지만 order-service에만 없었음
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized
      probes:
        enabled: true
  health:
    livenessState:
      enabled: true
    readinessState:
      enabled: true
  prometheus:
    metrics:
      export:
        enabled: true
```

Spring Boot의 기본 설정에서는 `health`와 `info` 엔드포인트만 노출됩니다. `prometheus` 엔드포인트를 웹으로 노출하려면 `management.endpoints.web.exposure.include`에 명시적으로 포함시켜야 합니다.

두 가지를 모두 추가한 뒤 Docker 이미지를 재빌드하고 k3s에 배포했습니다.

```bash
./gradlew :services:order-service:build -x test
docker build -t order-service:latest services/order-service/
docker save order-service:latest | sudo k3s ctr images import -
kubectl rollout restart deployment/order-service -n commerce
```

### 트러블슈팅: Pod 반복 재시작

재배포 후 새로운 문제가 발생했습니다. order-service Pod이 `Running` 상태이지만 `Ready 0/1`을 반복하며, 계속 재시작되는 현상이었습니다.

이전 컨테이너 로그를 확인하니 앱 자체는 정상적으로 시작되었습니다.

```
Started OrderServiceApplicationKt in 161.359 seconds
```

문제는 **기동 시간(161초)이 liveness probe 한계를 초과**한다는 점이었습니다. 기존 probe 설정을 분석해 보면 다음과 같습니다.

```yaml
livenessProbe:
  initialDelaySeconds: 60   # 60초 후 첫 체크
  periodSeconds: 30          # 30초 간격
  failureThreshold: 3        # 3회 실패 시 재시작
```

최대 허용 시간은 `60 + (30 × 3) = 150초`입니다. 앱 기동에 161초가 걸리므로, liveness probe가 실패하고 kubelet이 컨테이너를 종료합니다. 그런데 재시작된 컨테이너도 같은 시간이 걸리므로 무한 재시작 루프에 빠집니다.

해결책으로 `startupProbe`를 추가했습니다.

```yaml
startupProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 10
  failureThreshold: 20
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  periodSeconds: 30
  failureThreshold: 3
```

`startupProbe`는 k8s 1.18에서 도입된 기능으로, 앱이 기동 완료될 때까지 liveness/readiness probe를 비활성화합니다. `startupProbe`가 성공하면 그때부터 liveness probe가 동작을 시작합니다. 위 설정은 `30 + (10 × 20) = 230초`까지 기동을 허용합니다.

기존 `livenessProbe`에서 `initialDelaySeconds`를 제거한 것도 중요합니다. `startupProbe`가 기동 대기를 담당하므로, liveness probe에 별도의 초기 지연이 필요 없습니다.

## 결과

최종적으로 6개 서비스 모두 Prometheus에서 정상적으로 메트릭을 수집하게 되었습니다.

```bash
$ curl -s "http://localhost:9090/api/v1/targets" | ...
auth-service              up
user-service              up
catalog-service           up
inventory-service         up
order-service             up
payment-service           up
```

변경 사항을 정리하면 다음과 같습니다.

| 변경 | 파일 | 내용 |
|------|------|------|
| 수정 | `monitoring/prometheus/prometheus.yml` | `commerce_services` scrape job 추가 |
| 수정 | `monitoring/docker-compose.yaml` | 타겟 디렉토리 볼륨 마운트 추가 |
| 생성 | `commerce/infra/prometheus/targets/*.json` (6개) | file_sd 타겟 파일 |
| 생성 | `commerce/infra/prometheus/update-targets.sh` | ClusterIP 갱신 스크립트 |
| 수정 | `order-service/build.gradle.kts` | `micrometer-registry-prometheus` 의존성 추가 |
| 수정 | `order-service/application.yaml` | `management:` 설정 블록 추가 |
| 수정 | `infra/k8s/services/order-service.yaml` | `startupProbe` 추가 |

이번 작업에서 얻은 인사이트는 다음과 같습니다.

- `file_sd_configs`는 기존 Prometheus 설정을 크게 변경하지 않으면서 동적 타겟을 관리할 수 있는 실용적인 방법입니다. k8s 네이티브 서비스 디스커버리를 사용할 수 없는 환경에서 좋은 대안이 됩니다
- 여러 서비스를 운영할 때 의존성이나 설정이 한 서비스에만 누락되는 경우가 종종 발생합니다. 새 서비스를 추가할 때 체크리스트를 만들어 두면 이런 문제를 예방할 수 있습니다
- Spring Boot 앱의 기동 시간이 liveness probe 한계를 초과하면 Pod이 무한 재시작됩니다. `startupProbe`를 사용하면 기동 대기와 상태 확인의 관심사를 깔끔하게 분리할 수 있습니다

추후 개선할 수 있는 부분으로는 ClusterIP 변경 시 수동으로 `update-targets.sh`를 실행해야 한다는 점이 있습니다. CronJob이나 k8s Operator를 활용하여 자동 갱신하는 방식으로 발전시킬 수 있겠지만, ClusterIP는 Service를 삭제하지 않는 한 변경되지 않으므로 현재 수준으로 충분합니다.
