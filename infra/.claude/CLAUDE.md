# CLAUDE.md

이 파일은 인프라 디렉토리 작업 시 Claude Code(claude.ai/code)를 위한 가이드를 제공합니다.

## 디렉토리 목적

커머스 플랫폼을 위한 Infrastructure as Code (IaC). 개발 및 운영 환경을 위한 Kubernetes 배포 설정을 관리합니다.

**중요**: 인프라 컴포넌트(MariaDB, Redis, Kafka, Prometheus, Grafana)는 **노드 레벨에서 전역 관리**되며, 이 디렉토리의 Docker Compose로 관리되지 않습니다.

## 아키텍처 개요

### 배포 모델

```
애플리케이션 레이어 (Kubernetes)
├── auth-service (x2 복제본)
├── user-service (x2 복제본)
├── catalog-service (x2 복제본)
├── inventory-service (x2 복제본)
├── order-service (x2 복제본)
└── payment-service (x2 복제본)
         │
         ↓ (host.k3d.internal 경유)
인프라 레이어 (노드 레벨)
├── MariaDB (3306)
├── Redis (6379)
├── Kafka (9092)
├── Prometheus (9090)
└── Grafana (3000)
```

### 핵심 원칙

1. **관심사의 분리**: k8s의 애플리케이션, 노드 레벨의 인프라
2. **외부 의존성**: 서비스는 `host.k3d.internal`을 통해 인프라에 연결
3. **고가용성**: 서비스당 2개 복제본 (기본)
4. **환경 격리**: dev/prod를 위한 별도 설정

## 디렉토리 구조

```
infra/
├── README.md                 # 사용자용 문서
├── CLAUDE.md                 # 이 파일 (개발자 가이드)
├── Makefile                  # 중앙 명령 인터페이스
├── .env*                     # 환경별 변수
│
├── k8s/                      # Kubernetes 매니페스트
│   ├── namespace.yaml        # 네임스페이스: commerce
│   ├── ingress-dev.yaml      # 개발 ingress (HTTP, localhost)
│   ├── ingress.yaml          # 운영 ingress (HTTPS, 도메인)
│   ├── common/               # 공유 리소스
│   │   ├── configmap.yaml    # 환경 변수
│   │   └── secret.yaml       # 자격 증명 (base64)
│   └── services/             # 서비스 배포
│       ├── auth-service.yaml
│       ├── user-service.yaml
│       ├── catalog-service.yaml
│       ├── inventory-service.yaml
│       ├── order-service.yaml
│       └── payment-service.yaml
│
└── makefiles/                # 모듈화된 Makefile
    ├── k8s-namespace.mk      # 네임스페이스 작업
    ├── k8s-ingress.mk        # Ingress 관리
    ├── k8s-deploy.mk         # 배포 작업
    └── k8s-local.mk          # 로컬 개발 유틸리티
```

## 설정 파일

### 환경 변수 (.env.*)

모든 `.env` 파일은 **Kubernetes 전용 설정만** 포함합니다. 인프라 연결 세부 정보는 ConfigMap/Secret에 있습니다.

| 파일 | 용도 | 변수 |
|------|------|------|
| `.env` | 기본 | NAMESPACE=commerce, ENV=dev |
| `.env.local` | 로컬 개발 | NAMESPACE=commerce, ENV=local |
| `.env.dev` | k3d 개발 | NAMESPACE=commerce, ENV=dev |
| `.env.prod` | 운영 | NAMESPACE=commerce, ENV=prod |

### ConfigMap (k8s/common/configmap.yaml)

**목적**: 모든 서비스가 공유하는 비민감 환경 변수

**주요 설정**:
- **DB_HOST**: `host.k3d.internal` (외부 MariaDB)
- **DB_PORT**: `3306`
- **SPRING_KAFKA_BOOTSTRAP_SERVERS**: `host.k3d.internal:9092`
- **REDIS_HOST**: `host.k3d.internal`
- **REDIS_PORT**: `6379`
- **JWT_EXPIRATION**: `86400` (24시간)
- **JWT_REFRESH_EXPIRATION**: `604800` (7일)
- **SPRING_PROFILES_ACTIVE**: `dev`
- **LOGGING_LEVEL_ROOT**: `INFO`
- **LOGGING_LEVEL_COM_KOOSCO**: `DEBUG`

**수정 시기**:
- 인프라 엔드포인트 변경 시
- JWT 만료 시간 업데이트 시
- 로그 레벨 조정 시
- Spring 프로파일 수정 시

### Secret (k8s/common/secret.yaml)

**목적**: 민감한 데이터 (base64 인코딩)

**주요 시크릿**:
- **DB_USERNAME**: `YWRtaW4=` (admin)
- **DB_PASSWORD**: `YWRtaW4xMjM0` (admin1234)
- **JWT_SECRET**: JWT 서명 키

**수정 시기**:
- 데이터베이스 자격 증명 변경 시
- JWT 시크릿 교체 시
- 새로운 시크릿 추가 시

**인코딩/디코딩**:
```bash
# 인코딩
echo -n "new-password" | base64

# 디코딩
echo "YWRtaW4xMjM0" | base64 -d
```

## 무엇을 언제 수정할지

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

**비민감 (ConfigMap)**:
```bash
# ConfigMap 편집
vim k8s/common/configmap.yaml

# 변경사항 적용
kubectl apply -f k8s/common/configmap.yaml -n commerce

# 변경사항 반영을 위해 서비스 재시작
make k8s-restart
```

**민감 (Secret)**:
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

**Deployment 변경** (복제본, 리소스, 프로브):
```bash
# 매니페스트 편집
vim k8s/services/order-service.yaml

# 적용
kubectl apply -f k8s/services/order-service.yaml -n commerce

# 확인
kubectl get deployment order-service -n commerce
```

**롤링 업데이트** (코드 변경 후):
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

## 상세 문서

자세한 정보는 다음 문서를 참조하세요:

- **[K8s 배포 가이드](docs/k8s-deployment-guide.md)** - Deployment 패턴, Ingress 설정
- **[문제 해결 가이드](docs/troubleshooting-guide.md)** - 6가지 주요 문제 해결 시나리오
- **[Makefile 참조](docs/makefile-reference.md)** - Makefile 명령어 레퍼런스
- **[모범 사례](docs/best-practices.md)** - 리소스, 설정, 배포, 보안 모범 사례

## 관련 문서

- **프로젝트 루트**: `../../` - 메인 프로젝트 README
- **서비스**: `../../services/` - 개별 서비스 문서
- **공통 모듈**: `../../common/` - 공유 라이브러리 문서
- **부하 테스트**: `../../load-test/` - k6 성능 테스트

---

**최종 업데이트**: 2026-01-26
**대상**: Claude Code (AI 개발 어시스턴트)
**목적**: 인프라 운영을 위한 개발자 가이드
