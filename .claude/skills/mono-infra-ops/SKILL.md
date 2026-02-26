---
name: mono-infra-ops
description: 인프라 운영 절차 가이드. 새 서비스 추가, 환경변수 변경, 스케일링, 롤링 업데이트가 필요할 때 사용합니다.
---

## 운영 절차

### 새 서비스 추가

**생성할 파일**:
1. `k8s/services/<new-service>.yaml` - Deployment + Service

**업데이트할 파일**:
2. `k8s/ingress-dev.yaml` - 경로 `/api/<new-service>` 추가
3. `k8s/ingress.yaml` - 경로 추가 (운영용)
4. `Makefile` - SERVICES 변수에 추가

**단계**:
```bash
# 1. 서비스 매니페스트 생성 (기존 것에서 복사)
cp k8s/services/auth-service.yaml k8s/services/new-service.yaml
# 편집: name, image, DB_NAME

# 2. Ingress 경로 추가
# k8s/ingress-dev.yaml 편집, 경로 추가

# 3. 배포
make k8s-services-apply
make k8s-ingress-apply ENV=dev
```

### 환경 변수 변경

#### 비민감 (ConfigMap)

```bash
# ConfigMap 편집
vim k8s/common/configmap.yaml

# 변경사항 적용
kubectl apply -f k8s/common/configmap.yaml -n commerce

# 변경사항 반영을 위해 서비스 재시작
make k8s-restart
```

#### 민감 (Secret)

```bash
# base64 생성
echo -n "new-password" | base64

# Secret 편집
vim k8s/common/secret.yaml

# 변경사항 적용
kubectl apply -f k8s/common/secret.yaml -n commerce

# 서비스 재시작
make k8s-restart
```

### 서비스 스케일링

**모든 서비스**:
```bash
make k8s-scale REPLICAS=5
```

**단일 서비스**:
```bash
kubectl scale deployment/order-service --replicas=3 -n commerce
```

**영구 스케일링** (권장):
```bash
# 서비스 매니페스트 편집
vim k8s/services/order-service.yaml
# spec.replicas: 3으로 변경

# 적용
make k8s-services-apply
```

### 서비스 설정 업데이트

#### Deployment 변경 (복제본, 리소스, 프로브)

```bash
# 매니페스트 편집
vim k8s/services/order-service.yaml

# 적용
kubectl apply -f k8s/services/order-service.yaml -n commerce

# 확인
kubectl get deployment order-service -n commerce
```

#### 롤링 업데이트 (코드 변경 후)

```bash
# 이미지 재빌드
./gradlew :services:order-service:build
docker build -t order-service:latest services/order-service/

# k3d로 임포트
k3d image import order-service:latest -c <클러스터>

# Deployment 재시작
kubectl rollout restart deployment/order-service -n commerce

# 롤아웃 모니터링
kubectl rollout status deployment/order-service -n commerce
```

### 주의사항

- ConfigMap/Secret 변경 후 반드시 `make k8s-restart`로 서비스 재시작
- base64 인코딩: `echo -n "value" | base64`
- base64 디코딩: `echo "encoded" | base64 -d`

---

## Makefile 명령어 레퍼런스

### 명령 카테고리

| 카테고리 | 명령어 | 목적 |
|---------|--------|------|
| **네임스페이스** | `k8s-ns-create`, `k8s-ns-delete` | 네임스페이스 라이프사이클 |
| **리소스** | `k8s-apply-all`, `k8s-services-apply` | 리소스 배포 |
| **배포** | `k8s-start`, `k8s-stop`, `k8s-restart`, `k8s-scale` | 서비스 작업 |
| **Ingress** | `k8s-ingress-apply`, `k8s-ingress-list` | Ingress 관리 |
| **모니터링** | `k8s-status`, `k8s-deployments` | 상태 확인 |
| **로컬 개발** | `k8s-traefik-ip`, `k8s-port-forward` | 로컬 접근 |

### 일반적인 워크플로우

```bash
# 초기 배포
make k8s-ns-create
make k8s-apply-all ENV=dev
make k8s-start
make k8s-status

# 서비스 업데이트
make k8s-services-apply
make k8s-restart

# 정상 종료
make k8s-stop

# 완전 삭제
make k8s-ns-delete
```

### 명령어 상세

#### 네임스페이스 명령어

| 명령어 | 설명 |
|--------|------|
| `k8s-ns-create` | commerce 네임스페이스 생성 |
| `k8s-ns-delete` | commerce 네임스페이스 삭제 (모든 리소스 포함) |

#### 리소스 명령어

| 명령어 | 설명 |
|--------|------|
| `k8s-apply-all ENV=<env>` | 모든 리소스 적용 (ConfigMap, Secret, Services, Ingress) |
| `k8s-services-apply` | 서비스 Deployment만 적용 |

#### 배포 명령어

| 명령어 | 설명 |
|--------|------|
| `k8s-start` | 모든 서비스 시작 (replicas > 0) |
| `k8s-stop` | 모든 서비스 중지 (replicas = 0) |
| `k8s-restart` | 모든 서비스 롤링 재시작 |
| `k8s-scale REPLICAS=<n>` | 모든 서비스를 n개 복제본으로 스케일링 |

#### Ingress 명령어

| 명령어 | 설명 |
|--------|------|
| `k8s-ingress-apply ENV=<env>` | Ingress 리소스 적용 |
| `k8s-ingress-list` | Ingress 상태 조회 |

#### 모니터링 명령어

| 명령어 | 설명 |
|--------|------|
| `k8s-status` | 전체 상태 확인 (pods, services, ingress) |
| `k8s-deployments` | Deployment 상태 확인 |

#### 로컬 개발 명령어

| 명령어 | 설명 |
|--------|------|
| `k8s-traefik-ip` | Traefik Ingress IP 조회 |
| `k8s-port-forward` | 로컬 포트 포워딩 설정 |

### 환경 변수

| 변수 | 기본값 | 설명 |
|------|--------|------|
| `ENV` | dev | 환경 (dev, prod) |
| `NAMESPACE` | commerce | Kubernetes 네임스페이스 |
| `REPLICAS` | 2 | 스케일링 시 복제본 수 |

---

## 문제 해결 가이드

### 문제 1: Pod가 CrashLoopBackOff 상태

**증상**: Pod가 반복적으로 재시작

**디버깅**:
```bash
kubectl get pods -n commerce
kubectl logs <pod-이름> -n commerce
kubectl describe pod <pod-이름> -n commerce
kubectl top pods -n commerce
```

**해결책**: ConfigMap/Secret 확인, DB 연결 확인 (`DB_HOST: host.k3d.internal`), `initialDelaySeconds` 증가, 리소스 제한 증가

### 문제 2: 서비스가 데이터베이스에 연결할 수 없음

**디버깅**:
```bash
kubectl run -it --rm debug --image=mysql:8 --restart=Never -n commerce \
  -- mysql -h host.k3d.internal -u admin -padmin1234
kubectl get configmap commerce-common-config -n commerce -o yaml
```

**해결책**: MariaDB 실행 확인 (`docker ps | grep mariadb`), DB_HOST가 `host.k3d.internal`인지 확인, 누락된 스키마 생성

### 문제 3: 서비스가 Kafka에 연결할 수 없음

**디버깅**:
```bash
kubectl run -it --rm kafka-test --image=confluentinc/cp-kafka:latest \
  --restart=Never -n commerce \
  -- kafka-topics --list --bootstrap-server host.k3d.internal:9092
docker ps | grep kafka
```

**해결책**: Kafka 시작, `SPRING_KAFKA_BOOTSTRAP_SERVERS: host.k3d.internal:9092` 확인

### 문제 4: Ingress가 트래픽을 라우팅하지 않음

**디버깅**:
```bash
kubectl get ingress -n commerce
kubectl get pods -n kube-system | grep traefik
curl http://<traefik-ip>/api/auth/health
```

**해결책**: `make k8s-ingress-apply ENV=dev`, 서비스 이름/경로 일치 확인

### 문제 5: 이미지 풀 오류 (ImagePullBackOff)

**해결책**:
```bash
./gradlew :services:auth-service:build
docker build -t auth-service:latest services/auth-service/
k3d image import auth-service:latest -c <클러스터-이름>
# Deployment에서 imagePullPolicy: Never 확인
```

### 문제 6: 헬스 체크 실패

**디버깅**:
```bash
kubectl exec -it <pod-이름> -n commerce -- \
  wget -qO- http://localhost:8080/actuator/health
```

**해결책**: `initialDelaySeconds` 증가, actuator 활성화 확인, 포트 8080 확인

### 빠른 진단 체크리스트

**Pod 문제**: `kubectl get pods` → `kubectl logs` → `kubectl describe pod` → `kubectl top pods`

**연결 문제**: ConfigMap/Secret 확인 → `host.k3d.internal` 테스트 → `docker ps`

**Ingress 문제**: `kubectl get ingress` → Traefik 확인 → 서비스 이름/경로 확인

**이미지 문제**: `imagePullPolicy: Never` 확인 → k3d 이미지 임포트 확인

---

## 모범 사례

### 리소스 관리

- 항상 리소스 요청/제한 설정 (requests: 512Mi/250m, limits: 1Gi/1000m)
- 적절한 프로브 타이밍 사용 (liveness: 60s initial, readiness: 30s initial)
- `kubectl top pods -n commerce`로 리소스 사용량 모니터링

### 설정 관리

- 비민감 데이터는 ConfigMap, 민감 데이터는 Secret 사용
- 시크릿은 Base64 인코딩
- 설정 변경 후 `make k8s-restart`

### 배포 전략

- 항상 2개 이상의 복제본 사용 (HA)
- 롤링 업데이트 사용 (`kubectl rollout restart`)
- 운영 전에 개발에서 테스트 (`ENV=dev` 먼저)

### 보안

- 디코딩된 시크릿 커밋 금지
- 운영에서는 제한적 CORS 사용 (`*` 허용 금지)
- rate limiting 구현

### 배포 전/후 체크리스트

**배포 전**: ConfigMap/Secret 확인, 이미지 k3d 임포트, 리소스 설정, 프로브 설정, Ingress 경로 추가

**배포 후**: Pod Running 상태, 헬스 엔드포인트 응답, 로그 오류 없음, Ingress 라우팅 작동
