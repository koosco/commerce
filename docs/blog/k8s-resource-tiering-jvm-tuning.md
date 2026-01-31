# k8s 서비스별 리소스 차등 할당과 JVM 튜닝

## 문제 정의

6개의 Spring Boot 서비스를 k3s 클러스터에서 운영하고 있습니다. 모든 서비스에 동일한 리소스 설정을 적용하고 있었습니다.

```yaml
resources:
  requests:
    memory: "512Mi"
    cpu: "250m"
  limits:
    memory: "1Gi"
    cpu: "1000m"
```

JVM 옵션도 Dockerfile에서 일괄 설정하고 있었습니다.

```dockerfile
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:InitialRAMPercentage=50.0 \
               -XX:+UseG1GC \
               -XX:+DisableExplicitGC \
               -Djava.security.egd=file:/dev/./urandom"
```

서비스를 처음 배포할 때는 이 방식이 편리했습니다. 실제 사용량을 모르니 넉넉하게 동일한 값을 주는 것이 안전한 선택이었습니다. 하지만 운영 데이터가 쌓이면서 문제가 보이기 시작했습니다.

`kubectl top pods`로 유휴 상태의 실사용량을 측정한 결과입니다.

| 서비스 | 유휴 메모리 | 유휴 CPU |
|--------|-----------|---------|
| order-service | ~520Mi | 13m |
| inventory-service | ~546Mi | 6m |
| catalog-service | ~495Mi | 3m |
| auth-service | ~484Mi | 1m |
| user-service | ~353Mi | 1m |
| payment-service | ~379Mi | 2m |

몇 가지 사실이 드러납니다.

**CPU가 과도하게 할당되어 있습니다.** 가장 바쁜 order-service조차 유휴 시 13m밖에 사용하지 않는데, 모든 서비스에 requests 250m, limits 1000m을 주고 있었습니다. 12개 Pod(서비스당 2 replicas) 기준으로 CPU requests만 3000m입니다. 실제로는 그 1%도 사용하지 않는 셈입니다.

**서비스 간 메모리 사용량 차이가 큽니다.** inventory-service(546Mi)와 user-service(353Mi) 사이에 약 200Mi 차이가 있습니다. 둘 다 동일한 1Gi limits를 받고 있으니, user-service에는 실제로 필요한 것보다 훨씬 많은 메모리가 할당된 셈입니다.

**JVM MaxRAMPercentage 75%가 non-heap 여유를 압박합니다.** 컨테이너 limits가 1Gi일 때 Max Heap은 768Mi입니다. Metaspace, Thread Stack, Code Cache 등 non-heap 영역에 256Mi만 남는데, Spring Boot 앱에서 이 영역이 200Mi 이상 사용되는 것은 흔한 일입니다. 부하가 걸리면 OOMKilled 위험이 있습니다.

노드는 12 CPU, 32GB RAM이고, 현재 메모리 limits 사용률이 39%입니다. 리소스가 부족한 상황은 아니지만, 200+ VU 부하 테스트를 안정적으로 수행하려면 각 서비스의 특성에 맞게 리소스를 조정할 필요가 있었습니다.

## 대안책

### 1. VPA(Vertical Pod Autoscaler) 도입

k8s VPA를 사용하면 실사용량 기반으로 requests/limits를 자동 조정할 수 있습니다. 운영 환경에서는 이상적인 방법이지만, VPA 컨트롤러를 별도로 설치해야 하고 Pod 재시작이 수반됩니다. 6개 서비스 규모에서 VPA를 도입하는 것은 과도하다고 판단했습니다.

### 2. 모든 서비스 일괄 축소

현재 설정에서 requests와 limits를 일률적으로 낮추는 방법입니다. 간단하지만, 서비스별 특성을 반영하지 못합니다. order-service는 7개의 Kafka consumer를 운영하며 Saga 오케스트레이션을 처리하는 반면, auth-service는 JWT 발급만 담당합니다. 동일한 리소스를 주는 것은 합리적이지 않습니다.

### 3. 서비스 특성별 Tier 분류 후 차등 할당 (선택)

서비스를 Kafka consumer 수, 비즈니스 로직 복잡도, 실사용량 기준으로 두 개 Tier로 분류하고 차등 할당하는 방법입니다.

이 방법을 선택한 이유는 다음과 같습니다.

- **추가 컴포넌트 불필요**: k8s manifest 수정만으로 적용할 수 있습니다
- **서비스 특성 반영**: 무거운 서비스와 가벼운 서비스를 구분하여 적절한 리소스를 배분합니다
- **JVM 튜닝 병행**: k8s env로 `JAVA_OPTS`를 오버라이드하여 Dockerfile 수정 없이 JVM 설정을 조정할 수 있습니다

## 해결 과정

### Tier 분류 기준 설정

서비스를 두 개 Tier로 나눕니다.

**Tier 1 (Heavy)**: Kafka consumer가 많고 비즈니스 로직이 복잡한 서비스입니다. 부하 테스트 시 HTTP 요청 처리와 Kafka 메시지 소비가 동시에 일어나면 메모리 사용량이 급증할 수 있습니다.

- **order-service**: 7개 Kafka consumer, Saga 오케스트레이션, Outbox 패턴
- **inventory-service**: Redis + MariaDB 하이브리드 재고 관리, 다수의 Kafka consumer
- **catalog-service**: 상품/카테고리/SKU 관리, QueryDSL 복합 쿼리

**Tier 2 (Light)**: 상대적으로 경량인 서비스입니다. 유휴 시 350~484Mi 수준이므로 limits를 줄여도 부하 시 충분한 여유가 있습니다.

- **auth-service**: JWT 발급/검증
- **user-service**: 회원 정보 CRUD
- **payment-service**: 결제 처리 (외부 PG 연동)

### 리소스 할당표

| 서비스 | Tier | Memory requests | Memory limits | CPU requests | CPU limits |
|--------|------|-----------------|---------------|-------------|-----------|
| order-service | 1 | 512Mi | 1Gi | 100m | 500m |
| inventory-service | 1 | 512Mi | 1Gi | 100m | 500m |
| catalog-service | 1 | 512Mi | 1Gi | 100m | 500m |
| auth-service | 2 | 384Mi | 768Mi | 50m | 250m |
| user-service | 2 | 384Mi | 768Mi | 50m | 250m |
| payment-service | 2 | 384Mi | 768Mi | 50m | 250m |

기존 대비 총 리소스 변화(12 Pods 기준)입니다.

| 항목 | 기존 | 신규 | 변화 |
|------|------|------|------|
| CPU requests | 3000m | 900m | -70% |
| CPU limits | 12000m | 4500m | -62.5% |
| Memory requests | 6144Mi | 5376Mi | -12.5% |
| Memory limits | 12Gi | 10.5Gi | -12.5% |

CPU는 실사용량 대비 과할당이 심했으므로 대폭 축소했습니다. 메모리는 Tier 2 서비스만 소폭 줄이고, Tier 1은 기존과 동일하게 유지하여 안전 마진을 확보했습니다.

### JVM MaxRAMPercentage 조정

`MaxRAMPercentage`를 75%에서 65%로 낮춥니다. 이렇게 하면 non-heap 영역에 더 많은 여유를 확보할 수 있습니다.

| Tier | limits | 기존 Max Heap (75%) | 신규 Max Heap (65%) | non-heap 여유 |
|------|--------|-------------------|-------------------|--------------|
| Tier 1 | 1Gi | 768Mi | 665Mi | 359Mi |
| Tier 2 | 768Mi | 576Mi | 499Mi | 269Mi |

Tier 2의 경우 기존에 limits 1Gi, Heap 768Mi로 non-heap에 256Mi만 남았지만, 변경 후에는 limits 768Mi, Heap 499Mi로 non-heap에 269Mi가 남습니다. limits 자체는 줄었지만 non-heap 여유는 오히려 늘어났습니다.

### k8s env로 JAVA_OPTS 오버라이드

Dockerfile을 수정하고 이미지를 재빌드하는 대신, k8s Deployment의 `env`로 `JAVA_OPTS`를 오버라이드합니다. Dockerfile의 `ENTRYPOINT`가 `java $JAVA_OPTS -jar app.jar` 형태로 환경 변수를 참조하고 있으므로, k8s에서 같은 이름의 환경 변수를 설정하면 Dockerfile의 기본값을 덮어씁니다.

```yaml
# Tier 1 서비스 (order, inventory, catalog)
env:
  - name: DB_NAME
    value: commerce-order
  - name: SPRING_KAFKA_CONSUMER_GROUP_ID
    value: order-service-group
  - name: JAVA_OPTS
    value: "-XX:+UseContainerSupport -XX:MaxRAMPercentage=65.0 -XX:InitialRAMPercentage=50.0 -XX:+UseG1GC -XX:+DisableExplicitGC -Djava.security.egd=file:/dev/./urandom"
resources:
  requests:
    memory: "512Mi"
    cpu: "100m"
  limits:
    memory: "1Gi"
    cpu: "500m"
```

```yaml
# Tier 2 서비스 (auth, user, payment)
env:
  - name: DB_NAME
    value: commerce-auth
  - name: JAVA_OPTS
    value: "-XX:+UseContainerSupport -XX:MaxRAMPercentage=65.0 -XX:InitialRAMPercentage=50.0 -XX:+UseG1GC -XX:+DisableExplicitGC -Djava.security.egd=file:/dev/./urandom"
resources:
  requests:
    memory: "384Mi"
    cpu: "50m"
  limits:
    memory: "768Mi"
    cpu: "250m"
```

이 방식의 장점은 Dockerfile과 Docker 이미지를 그대로 유지할 수 있다는 점입니다. 로컬 개발 시에는 Dockerfile의 기본값(75%)이 적용되고, k8s 배포 시에만 65%로 오버라이드됩니다. 환경별로 JVM 설정을 달리할 수 있는 유연성을 확보한 셈입니다.

### 주의사항: env 블록 중복

k8s Deployment manifest에서 `env:` 키가 하나의 컨테이너 스펙 내에 두 번 이상 나타나면, YAML 파서에 따라 마지막 블록만 인식되거나 예측 불가능한 동작이 발생할 수 있습니다. 기존에 `DB_NAME`이나 `SPRING_KAFKA_CONSUMER_GROUP_ID` 등이 이미 `env:` 블록에 정의되어 있으므로, `JAVA_OPTS`는 반드시 같은 `env:` 블록 안에 추가해야 합니다.

```yaml
# 잘못된 예 - env: 블록이 두 개
env:
  - name: DB_NAME
    value: commerce-order
livenessProbe: ...
env:                          # 두 번째 env: → 첫 번째를 덮어쓸 수 있음
  - name: JAVA_OPTS
    value: "..."

# 올바른 예 - 하나의 env: 블록에 모든 변수
env:
  - name: DB_NAME
    value: commerce-order
  - name: JAVA_OPTS
    value: "..."
livenessProbe: ...
```

### 배포

변경된 manifest를 적용하고 순차적으로 롤링 업데이트합니다.

```bash
kubectl apply -f infra/k8s/services/ -n commerce

for svc in auth user catalog inventory payment order; do
  kubectl rollout restart deployment/${svc}-service -n commerce
  kubectl rollout status deployment/${svc}-service -n commerce --timeout=300s
done
```

순차 배포하는 이유는 한 서비스에서 OOMKilled가 발생하면 즉시 발견하고 롤백할 수 있기 때문입니다. 모든 서비스를 동시에 재시작하면 문제 원인을 특정하기 어렵습니다.

## 결과

변경 사항을 정리하면 다음과 같습니다.

| 변경 | 파일 | 내용 |
|------|------|------|
| 수정 | `infra/k8s/services/order-service.yaml` | Tier 1 리소스 + JAVA_OPTS |
| 수정 | `infra/k8s/services/inventory-service.yaml` | Tier 1 리소스 + JAVA_OPTS |
| 수정 | `infra/k8s/services/catalog-service.yaml` | Tier 1 리소스 + JAVA_OPTS |
| 수정 | `infra/k8s/services/auth-service.yaml` | Tier 2 리소스 + JAVA_OPTS |
| 수정 | `infra/k8s/services/user-service.yaml` | Tier 2 리소스 + JAVA_OPTS |
| 수정 | `infra/k8s/services/payment-service.yaml` | Tier 2 리소스 + JAVA_OPTS |

이번 작업에서 얻은 인사이트는 다음과 같습니다.

- **"일단 넉넉하게"는 좋은 출발점이지만 끝이 아닙니다.** 초기에 모든 서비스에 동일한 리소스를 할당하는 것은 합리적입니다. 하지만 운영 데이터가 쌓이면 반드시 실사용량을 측정하고 조정해야 합니다. 특히 CPU는 유휴 시 사용량과 할당량 사이의 괴리가 매우 클 수 있습니다
- **MaxRAMPercentage는 Heap만의 이야기입니다.** JVM 메모리는 Heap + non-heap(Metaspace, Thread Stack, Code Cache, Direct Buffer 등)으로 구성됩니다. `MaxRAMPercentage=75`면 컨테이너 메모리의 75%를 Heap이 차지할 수 있으므로, non-heap에 25%만 남습니다. Spring Boot처럼 Metaspace를 많이 사용하는 프레임워크에서는 이 비율이 빠듯할 수 있습니다
- **k8s env는 Dockerfile ENV을 오버라이드합니다.** `ENTRYPOINT`에서 환경 변수를 참조하는 구조라면, Docker 이미지를 재빌드하지 않고도 k8s manifest만으로 JVM 설정을 변경할 수 있습니다. 환경별 설정 분리에 유용한 패턴입니다

추후 서비스가 더 추가되거나 부하 패턴이 변하면 Tier를 세분화하거나 HPA(Horizontal Pod Autoscaler)를 도입하여 트래픽 기반 자동 스케일링으로 발전시킬 수 있습니다. 현재 규모에서는 수동 Tier 분류만으로 충분합니다.
