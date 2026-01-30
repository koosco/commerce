# 인프라 관리

커머스 플랫폼을 위한 Infrastructure as Code (IaC). 개발 및 운영 환경을 위한 Kubernetes 배포 설정을 관리합니다.

## 📋 목차

- [개요](#개요)
- [아키텍처](#아키텍처)
- [디렉토리 구조](#디렉토리-구조)
- [사전 요구사항](#사전-요구사항)
- [빠른 시작](#빠른-시작)
- [설정](#설정)
- [Kubernetes 리소스](#kubernetes-리소스)
- [Makefile 명령어](#makefile-명령어)
- [배포 워크플로우](#배포-워크플로우)
- [문제 해결](#문제-해결)

## 🎯 개요

이 인프라 디렉토리는 6개 마이크로서비스의 Kubernetes 배포를 관리합니다:

- **auth-service** (8089) - JWT 토큰 발급
- **user-service** (8081) - 사용자 관리
- **catalog-service** (8084) - 상품 및 카테고리
- **inventory-service** (8083) - 재고 관리
- **order-service** (8085) - 주문 처리
- **payment-service** (8087) - 결제 처리

### 핵심 원칙

1. **외부 인프라**: Database, Redis, Kafka, Prometheus, Grafana는 노드 레벨에서 관리
2. **Kubernetes 전용**: 애플리케이션 배포에만 집중
3. **환경별 설정**: dev와 prod를 위한 별도 설정
4. **기본 HA**: 서비스당 2개 복제본으로 고가용성 보장

## 🏗️ 아키텍처

```
┌─────────────────────────────────────────────────────────┐
│                  Traefik Ingress                        │
│  /api/auth → auth-service                               │
│  /api/users → user-service                              │
│  /api/categories → catalog-service                      │
│  /api/products → catalog-service                        │
│  /api/inventories → inventory-service                   │
│  /api/orders → order-service                            │
│  /api/payments → payment-service                        │
└─────────────────────────────────────────────────────────┘
                           │
                           ↓
┌─────────────────────────────────────────────────────────┐
│              Kubernetes 클러스터 (k3s/k3d)              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │ auth-service │  │ user-service │  │catalog-svc   │  │
│  │  (x2 pods)   │  │  (x2 pods)   │  │  (x2 pods)   │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │inventory-svc │  │ order-service│  │payment-svc   │  │
│  │  (x2 pods)   │  │  (x2 pods)   │  │  (x2 pods)   │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
                           │
                           ↓ (host.k3d.internal 경유)
┌─────────────────────────────────────────────────────────┐
│          외부 인프라 (노드 레벨)                         │
│  MariaDB (3306) | Redis (6379) | Kafka (9092)           │
│  Prometheus (9090) | Grafana (3000)                     │
└─────────────────────────────────────────────────────────┘
```

## 📁 디렉토리 구조

```
infra/
├── README.md                 # 이 문서
├── CLAUDE.md                 # Claude Code를 위한 개발자 가이드
├── Makefile                  # 중앙 명령 인터페이스
├── .env                      # 기본 환경 변수
├── .env.local                # 로컬 개발 설정
├── .env.dev                  # k3d 개발 설정
├── .env.prod                 # 운영 설정
│
├── k8s/                      # Kubernetes 매니페스트
│   ├── namespace.yaml        # 네임스페이스 정의
│   ├── ingress-dev.yaml      # 개발 Ingress (HTTP, localhost)
│   ├── ingress.yaml          # 운영 Ingress (HTTPS, 도메인)
│   ├── common/               # 공유 리소스
│   │   ├── configmap.yaml    # 환경 변수
│   │   └── secret.yaml       # 민감 데이터 (base64)
│   └── services/             # 서비스 배포
│       ├── auth-service.yaml
│       ├── user-service.yaml
│       ├── catalog-service.yaml
│       ├── inventory-service.yaml
│       ├── order-service.yaml
│       └── payment-service.yaml
│
└── makefiles/                # 모듈화된 Makefile
    ├── k8s-namespace.mk      # 네임스페이스 관리
    ├── k8s-ingress.mk        # Ingress 설정
    ├── k8s-deploy.mk         # 배포 작업
    └── k8s-local.mk          # 로컬 개발 유틸리티
```

## 🔧 사전 요구사항

### 소프트웨어 요구사항

- **Kubernetes 클러스터**
  - k3d (macOS 개발): `brew install k3d`
  - k3s (Linux 운영): `curl -sfL https://get.k3s.io | sh -`
- **kubectl**: Kubernetes CLI
- **Docker**: 이미지 빌드 및 로컬 레지스트리
- **Make**: 명령 실행

### 인프라 요구사항

다음 서비스가 노드 레벨에서 실행 중이어야 합니다:

| 서비스 | 포트 | 용도 | 연결 방법 |
|--------|------|------|-----------|
| MariaDB | 3306 | 데이터베이스 | `host.k3d.internal:3306` |
| Redis | 6379 | 캐시 | `host.k3d.internal:6379` |
| Kafka | 9092 | 메시징 | `host.k3d.internal:9092` |
| Prometheus | 9090 | 메트릭 | `host.k3d.internal:9090` |
| Grafana | 3000 | 대시보드 | `host.k3d.internal:3000` |

### 데이터베이스 스키마

각 서비스는 독립된 데이터베이스 스키마를 사용합니다:

| 서비스 | 스키마 이름 |
|--------|-------------|
| auth-service | `commerce-auth` |
| user-service | `commerce-user` |
| catalog-service | `commerce-catalog` |
| inventory-service | `commerce-inventory` |
| order-service | `commerce-order` |
| payment-service | `commerce-payment` |

## 🚀 빠른 시작

### 개발 환경 (k3d)

```bash
# 1. infra 디렉토리로 이동
cd infra/

# 2. Kubernetes 네임스페이스 생성
make k8s-ns-create

# 3. 모든 리소스 배포
make k8s-apply-all ENV=dev

# 4. 서비스 시작
make k8s-start

# 5. 상태 확인
make k8s-status

# 6. 서비스 접근
# Traefik LoadBalancer IP를 통한 접근
make k8s-traefik-ip

# 포트 포워딩을 통한 접근
make k8s-port-forward PORT=8080
```

### 운영 환경 (k3s)

```bash
# 1. 운영 환경에 배포
make k8s-apply-all ENV=prod

# 2. 배포 확인
make k8s-status

# 3. 필요시 스케일링
make k8s-scale REPLICAS=5
```

## ⚙️ 설정

### 환경 변수

모든 `.env.*` 파일은 Kubernetes 전용 설정을 포함합니다:

**.env (기본)**
```bash
NAMESPACE=commerce
ENV=dev
```

**.env.local (로컬 개발)**
```bash
NAMESPACE=commerce
ENV=local
```

**.env.dev (k3d 개발)**
```bash
NAMESPACE=commerce
ENV=dev
```

**.env.prod (운영)**
```bash
NAMESPACE=commerce
ENV=prod
```

### ConfigMap (k8s/common/configmap.yaml)

모든 서비스가 공유하는 공통 설정:

```yaml
data:
  # 외부 인프라 엔드포인트
  DB_HOST: "host.k3d.internal"
  DB_PORT: "3306"
  SPRING_KAFKA_BOOTSTRAP_SERVERS: "host.k3d.internal:9092"
  REDIS_HOST: "host.k3d.internal"
  REDIS_PORT: "6379"

  # JWT 설정
  JWT_EXPIRATION: "86400"
  JWT_REFRESH_EXPIRATION: "604800"

  # Spring 프로파일
  SPRING_PROFILES_ACTIVE: "dev"

  # 로깅
  LOGGING_LEVEL_ROOT: "INFO"
  LOGGING_LEVEL_COM_KOOSCO: "DEBUG"
```

### Secret (k8s/common/secret.yaml)

민감한 데이터 (base64 인코딩):

```yaml
data:
  DB_USERNAME: YWRtaW4=        # admin
  DB_PASSWORD: YWRtaW4xMjM0    # admin1234
  JWT_SECRET: a29vc2NvLWp3dC1zZWNyZXQta2V5...
```

## 📦 Kubernetes 리소스

### 서비스 배포 사양

각 서비스는 다음 패턴을 따릅니다:

**Deployment**
- **복제본**: 2개 (고가용성)
- **이미지 풀 정책**: Never (k3d 이미지 임포트)
- **컨테이너 포트**: 8080
- **환경 변수**: ConfigMap + Secret + 서비스별 DB_NAME

**헬스 프로브**
- **Liveness Probe**: `/actuator/health/liveness`
  - 초기 대기: 60초
  - 주기: 30초
  - 타임아웃: 5초
  - 실패 임계값: 3
- **Readiness Probe**: `/actuator/health/readiness`
  - 초기 대기: 30초
  - 주기: 10초
  - 타임아웃: 5초
  - 실패 임계값: 3

**리소스**
```yaml
requests:
  memory: "512Mi"
  cpu: "250m"
limits:
  memory: "1Gi"
  cpu: "1000m"
```

**Service**
- **타입**: ClusterIP (기본)
- **포트**: 80 → 8080 (targetPort)
- **프로토콜**: TCP

### Ingress 설정

**개발 환경 (ingress-dev.yaml)**
- **진입점**: HTTP (web)
- **호스트**: 모두 허용 (localhost 지원)
- **미들웨어**: CORS (허용적), Rate Limit (1000/s 평균, 2000 버스트)
- **경로**: `/api/{service}` → `{service}-service:80`

**운영 환경 (ingress.yaml)**
- **진입점**: HTTPS (websecure)
- **호스트**: 특정 도메인
- **TLS**: 인증서 활성화
- **미들웨어**: CORS (제한적), Rate Limit (엄격)

### API 라우팅

| 경로 | 서비스 | 컨테이너 포트 |
|------|---------|---------------|
| `/api/auth` | auth-service | 8080 |
| `/api/users` | user-service | 8080 |
| `/api/categories` | catalog-service | 8080 |
| `/api/products` | catalog-service | 8080 |
| `/api/inventories` | inventory-service | 8080 |
| `/api/orders` | order-service | 8080 |
| `/api/payments` | payment-service | 8080 |

## 🔨 Makefile 명령어

### 네임스페이스 관리

```bash
make k8s-ns-create       # commerce 네임스페이스 생성
make k8s-ns-delete       # commerce 네임스페이스 삭제
```

### 리소스 관리

```bash
make k8s-apply-all       # 모든 리소스 적용 (namespace, common, services, ingress)
make k8s-apply-all ENV=dev   # 개발 환경
make k8s-apply-all ENV=prod  # 운영 환경

make k8s-services-apply  # 서비스만 적용 (common + services)
make k8s-services-delete # 서비스 매니페스트 삭제
```

### 배포 작업

```bash
make k8s-start           # 모든 서비스 시작 (2개로 스케일)
make k8s-stop            # 모든 서비스 중지 (0개로 스케일)
make k8s-restart         # 모든 서비스 롤링 재시작
make k8s-scale REPLICAS=3  # 모든 서비스를 3개 복제본으로 스케일
make k8s-deployments     # 배포 상태 확인
```

### Ingress 관리

```bash
make k8s-ingress-apply ENV=dev   # 개발 ingress 적용
make k8s-ingress-apply ENV=prod  # 운영 ingress 적용
make k8s-ingress-list    # 모든 ingress 목록
```

### 상태 및 모니터링

```bash
make k8s-status          # 모든 리소스 상태 확인
make k8s-traefik-ip      # Traefik LoadBalancer IP 확인
make k8s-port-forward PORT=8080  # 로컬로 포트 포워딩
```

### 일반적인 워크플로우

```bash
# 처음부터 전체 배포
make k8s-ns-create && make k8s-apply-all ENV=dev && make k8s-start

# 서비스 업데이트 및 재시작
make k8s-services-apply && make k8s-restart

# 부하 테스트를 위한 스케일 업
make k8s-scale REPLICAS=10

# 정상 종료
make k8s-stop
```

## 🔄 배포 워크플로우

### 초기 배포

```bash
# 1단계: 인프라 확인
# MariaDB, Redis, Kafka가 노드 레벨에서 실행 중인지 확인

# 2단계: 네임스페이스 생성
cd infra/
make k8s-ns-create

# 3단계: 이미지 빌드 및 임포트 (프로젝트 루트에서)
cd ../
./gradlew :services:auth-service:build
docker build -t auth-service:latest services/auth-service/
k3d image import auth-service:latest -c <클러스터-이름>
# 모든 서비스에 대해 반복

# 4단계: 리소스 배포
cd infra/
make k8s-apply-all ENV=dev

# 5단계: 서비스 시작
make k8s-start

# 6단계: 확인
make k8s-status
kubectl get pods -n commerce -w
```

### 배포 업데이트

```bash
# 코드 수정, 이미지 재빌드, 재임포트
./gradlew :services:order-service:build
docker build -t order-service:latest services/order-service/
k3d image import order-service:latest -c <클러스터-이름>

# 특정 배포 재시작
kubectl rollout restart deployment/order-service -n commerce

# 또는 전체 재시작
make k8s-restart
```

### 설정 변경

```bash
# ConfigMap 또는 Secret 편집
vim k8s/common/configmap.yaml

# 변경사항 적용
make k8s-services-apply

# 변경사항 반영을 위해 재시작
make k8s-restart
```

### 스케일링

```bash
# 모든 서비스 스케일
make k8s-scale REPLICAS=5

# 특정 서비스 스케일
kubectl scale deployment/order-service --replicas=3 -n commerce

# 확인
make k8s-deployments
```

## 🐛 문제 해결

### Pod가 시작되지 않음

```bash
# Pod 상태 확인
kubectl get pods -n commerce

# Pod 이벤트 확인
kubectl describe pod <pod-이름> -n commerce

# 로그 확인
kubectl logs <pod-이름> -n commerce

# 일반적인 원인:
# - 이미지를 찾을 수 없음 → k3d image import로 재임포트
# - CrashLoopBackOff → 애플리케이션 로그 확인
# - ImagePullBackOff → imagePullPolicy: Never 확인
```

### 서비스가 데이터베이스에 연결할 수 없음

```bash
# 클러스터에서 DB 접근 확인
kubectl run -it --rm debug --image=mysql:8 --restart=Never -n commerce \
  -- mysql -h host.k3d.internal -u admin -padmin1234

# ConfigMap 확인
kubectl get configmap commerce-common-config -n commerce -o yaml

# 데이터베이스 존재 확인
mysql -h localhost -u admin -padmin1234 -e "SHOW DATABASES;"

# 연결 오류에 대한 서비스 로그 확인
kubectl logs deployment/user-service -n commerce
```

### 서비스가 Kafka에 연결할 수 없음

```bash
# Kafka 연결 테스트
kubectl run -it --rm kafka-test --image=confluentinc/cp-kafka:latest \
  --restart=Never -n commerce \
  -- kafka-topics --list --bootstrap-server host.k3d.internal:9092

# 컨슈머 그룹 상태 확인
kubectl exec -it <order-service-pod> -n commerce -- \
  env | grep KAFKA

# Kafka 실행 확인
docker ps | grep kafka
```

### Ingress가 작동하지 않음

```bash
# Ingress 상태 확인
make k8s-ingress-list

# Traefik 확인
kubectl get pods -n kube-system | grep traefik

# LoadBalancer IP 확인
make k8s-traefik-ip

# 엔드포인트 테스트
curl http://<traefik-ip>/api/auth/health

# Ingress 로그 확인
kubectl logs -n kube-system deployment/traefik
```

### 헬스 체크 실패

```bash
# Actuator 엔드포인트 확인
kubectl exec -it <pod-이름> -n commerce -- \
  wget -O- http://localhost:8080/actuator/health

# 포트가 올바른지 확인
kubectl get svc -n commerce

# 시작이 느린 경우 초기 대기 시간 증가
# deployment yaml 편집, initialDelaySeconds 증가
```

### 리소스 문제 (OOMKilled, CPU 제한)

```bash
# 리소스 사용량 확인
kubectl top pods -n commerce

# 리소스 제한 확인
kubectl describe pod <pod-이름> -n commerce | grep -A 10 Limits

# 서비스 yaml에서 제한 증가
# resources.limits.memory: "2Gi"
# resources.limits.cpu: "2000m"

# 변경사항 적용
make k8s-services-apply && make k8s-restart
```

### 디버깅 팁

```bash
# Pod 내부 셸 접근
kubectl exec -it <pod-이름> -n commerce -- /bin/sh

# 로컬 머신으로 포트 포워딩
kubectl port-forward pod/<pod-이름> 8080:8080 -n commerce

# 이벤트 확인
kubectl get events -n commerce --sort-by='.lastTimestamp'

# Pod 상태 모니터링
kubectl get pods -n commerce -w

# 로그 실시간 확인
kubectl logs -f deployment/order-service -n commerce
```

## 📚 추가 리소스

- **프로젝트 루트**: `../` - Gradle 멀티모듈 프로젝트
- **서비스**: `../services/` - 개별 서비스 소스 코드
- **공통 모듈**: `../common/` - 공유 라이브러리
- **부하 테스트**: `../load-test/` - k6 테스트 스크립트

### 관련 문서

- [메인 프로젝트 README](../README.md)
- [CLAUDE.md](./CLAUDE.md) - Claude Code를 위한 개발자 가이드
- [서비스 문서](../services/) - 개별 서비스 README

### 외부 참조

- [k3d 문서](https://k3d.io/)
- [k3s 문서](https://docs.k3s.io/)
- [Traefik Ingress](https://doc.traefik.io/traefik/providers/kubernetes-ingress/)
- [Kubernetes 모범 사례](https://kubernetes.io/docs/concepts/configuration/overview/)

---

**최종 업데이트**: 2026-01-26
**관리**: 백엔드 팀
**환경**: k3d (개발), k3s (운영)
