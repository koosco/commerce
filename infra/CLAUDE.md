# CLAUDE.md

이 파일은 인프라 디렉토리 작업 시 Claude Code(claude.ai/code)를 위한 가이드를 제공합니다.

## 📋 디렉토리 목적

커머스 플랫폼을 위한 Infrastructure as Code (IaC). 개발 및 운영 환경을 위한 Kubernetes 배포 설정을 관리합니다.

**중요**: 인프라 컴포넌트(MariaDB, Redis, Kafka, Prometheus, Grafana)는 **노드 레벨에서 전역 관리**되며, 이 디렉토리의 Docker Compose로 관리되지 않습니다.

## 🏗️ 아키텍처 개요

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

## 📁 디렉토리 구조

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

## 🔧 설정 파일

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

## 🚀 서비스 배포 패턴

각 서비스는 표준화된 패턴을 따릅니다:

### Deployment 사양

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: <서비스-이름>
  namespace: commerce
  labels:
    app: <서비스-이름>
    app.kubernetes.io/part-of: commerce
spec:
  replicas: 2                    # 기본 HA
  selector:
    matchLabels:
      app: <서비스-이름>
  template:
    metadata:
      labels:
        app: <서비스-이름>
    spec:
      containers:
        - name: <서비스-이름>
          image: <서비스-이름>:latest
          imagePullPolicy: Never   # k3d 이미지 임포트
          ports:
            - containerPort: 8080
          envFrom:
            - configMapRef:
                name: commerce-common-config
            - secretRef:
                name: commerce-common-secret
          env:
            - name: DB_NAME
              value: commerce-<서비스>
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 60
            periodSeconds: 30
            timeoutSeconds: 5
            failureThreshold: 3
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 10
            timeoutSeconds: 5
            failureThreshold: 3
          resources:
            requests:
              memory: "512Mi"
              cpu: "250m"
            limits:
              memory: "1Gi"
              cpu: "1000m"
```

### 데이터베이스 스키마 매핑

각 서비스는 전용 데이터베이스 스키마를 사용합니다:

| 서비스 | DB_NAME | 포트 |
|--------|---------|------|
| auth-service | commerce-auth | 8089 |
| user-service | commerce-user | 8081 |
| catalog-service | commerce-catalog | 8084 |
| inventory-service | commerce-inventory | 8083 |
| order-service | commerce-order | 8085 |
| payment-service | commerce-payment | 8087 |

### Service 사양

```yaml
apiVersion: v1
kind: Service
metadata:
  name: <서비스-이름>
  namespace: commerce
  labels:
    app: <서비스-이름>
    app.kubernetes.io/part-of: commerce
spec:
  selector:
    app: <서비스-이름>
  ports:
    - port: 80           # 외부 포트
      targetPort: 8080   # 컨테이너 포트
      protocol: TCP
```

## 🌐 Ingress 설정

### 개발 환경 (ingress-dev.yaml)

**사용 사례**: k3d 로컬 개발

**기능**:
- HTTP만 사용 (TLS 없음)
- 모든 호스트 허용 (localhost 지원)
- 허용적 CORS (*)
- 높은 rate limit (1000 평균, 2000 버스트)

**미들웨어**:
```yaml
# CORS
commerce-dev-cors:
  accessControlAllowOriginList: ["*"]
  accessControlAllowCredentials: true

# Rate Limit
commerce-dev-ratelimit:
  average: 1000
  burst: 2000
```

**라우팅**:
```
/api/auth → auth-service:80
/api/users → user-service:80
/api/catalog → catalog-service:80
/api/inventory → inventory-service:80
/api/orders → order-service:80
/api/payments → payment-service:80
```

### 운영 환경 (ingress.yaml)

**사용 사례**: k3s 운영 배포

**기능**:
- TLS가 포함된 HTTPS
- 특정 도메인만 허용
- 제한적 CORS
- 엄격한 rate limit

**Ingress 수정 시기**:
- 새 서비스 경로 추가 시
- 경로 접두사 변경 시
- CORS 정책 업데이트 시
- rate limit 조정 시
- TLS 인증서 추가 시

## 🔨 Makefile 참조

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

**초기 배포**:
```bash
make k8s-ns-create
make k8s-apply-all ENV=dev
make k8s-start
make k8s-status
```

**서비스 업데이트**:
```bash
make k8s-services-apply
make k8s-restart
```

**서비스 스케일링**:
```bash
make k8s-scale REPLICAS=5
```

**정상 종료**:
```bash
make k8s-stop
```

**완전 삭제**:
```bash
make k8s-ns-delete
```

## 🐛 일반적인 문제 및 해결책

### 문제: Pod가 CrashLoopBackOff 상태

**증상**: Pod가 반복적으로 재시작
**원인**:
- 애플리케이션 시작 실패
- 데이터베이스 연결 문제
- 환경 변수 누락
- 리소스 제약

**디버깅 단계**:
```bash
# Pod 상태 확인
kubectl get pods -n commerce

# Pod 로그 확인
kubectl logs <pod-이름> -n commerce

# Pod 이벤트 확인
kubectl describe pod <pod-이름> -n commerce

# 리소스 사용량 확인
kubectl top pods -n commerce
```

**일반적인 해결책**:
- ConfigMap/Secret이 적용되었는지 확인
- 데이터베이스 연결 확인: `DB_HOST: host.k3d.internal`
- 헬스 프로브의 `initialDelaySeconds` 증가
- 리소스 제한 증가

### 문제: 서비스가 데이터베이스에 연결할 수 없음

**증상**: 연결 타임아웃, 알 수 없는 호스트 오류
**근본 원인**:
- MariaDB가 노드 레벨에서 실행되지 않음
- ConfigMap의 잘못된 DB_HOST
- 데이터베이스 스키마가 존재하지 않음
- 잘못된 자격 증명

**디버깅 단계**:
```bash
# Pod에서 DB 연결 테스트
kubectl run -it --rm debug --image=mysql:8 --restart=Never -n commerce \
  -- mysql -h host.k3d.internal -u admin -padmin1234

# ConfigMap 확인
kubectl get configmap commerce-common-config -n commerce -o yaml

# Secret 확인
kubectl get secret commerce-common-secret -n commerce -o yaml
```

**일반적인 해결책**:
- MariaDB 실행 확인: `docker ps | grep mariadb`
- DB_HOST가 `host.k3d.internal`인지 확인 (`localhost` 아님)
- 누락된 데이터베이스 스키마 생성
- 자격 증명의 base64 인코딩 확인

### 문제: 서비스가 Kafka에 연결할 수 없음

**증상**: 연결 거부, 토픽을 찾을 수 없음
**근본 원인**:
- Kafka가 노드 레벨에서 실행되지 않음
- 잘못된 부트스트랩 서버
- 토픽이 존재하지 않음

**디버깅 단계**:
```bash
# Kafka 연결 테스트
kubectl run -it --rm kafka-test --image=confluentinc/cp-kafka:latest \
  --restart=Never -n commerce \
  -- kafka-topics --list --bootstrap-server host.k3d.internal:9092

# ConfigMap 확인
kubectl get configmap commerce-common-config -n commerce -o yaml | grep KAFKA

# Kafka 실행 확인
docker ps | grep kafka
```

**일반적인 해결책**:
- 노드 레벨에서 Kafka 시작
- `SPRING_KAFKA_BOOTSTRAP_SERVERS: host.k3d.internal:9092` 확인
- 필요한 Kafka 토픽 생성

### 문제: Ingress가 트래픽을 라우팅하지 않음

**증상**: 404 Not Found, 503 Service Unavailable
**근본 원인**:
- Ingress가 적용되지 않음
- Traefik이 실행되지 않음
- 서비스 이름 불일치
- 경로 접두사 불일치

**디버깅 단계**:
```bash
# Ingress 상태 확인
kubectl get ingress -n commerce

# Traefik 확인
kubectl get pods -n kube-system | grep traefik

# Traefik IP 확인
kubectl get svc -n kube-system traefik

# 엔드포인트 테스트
curl http://<traefik-ip>/api/auth/health
```

**일반적인 해결책**:
- Ingress 적용: `make k8s-ingress-apply ENV=dev`
- 필요시 Traefik 재시작
- Ingress에서 서비스 이름 일치 확인
- 애플리케이션 경로의 경로 접두사 확인

### 문제: 이미지 풀 오류

**증상**: ImagePullBackOff, ErrImagePull
**근본 원인**:
- 이미지가 k3d로 임포트되지 않음
- 잘못된 imagePullPolicy
- 이미지 이름 불일치

**디버깅 단계**:
```bash
# k3d의 이미지 목록
docker exec -it k3d-<cluster>-server-0 crictl images

# Deployment 스펙 확인
kubectl get deployment <service> -n commerce -o yaml | grep image
```

**일반적인 해결책**:
```bash
# 이미지 빌드
./gradlew :services:auth-service:build
docker build -t auth-service:latest services/auth-service/

# k3d로 임포트
k3d image import auth-service:latest -c <클러스터-이름>

# Deployment에서 imagePullPolicy: Never 확인
```

### 문제: 헬스 체크 실패

**증상**: Pod가 Ready 상태가 되지 않음
**근본 원인**:
- Actuator 엔드포인트를 사용할 수 없음
- initialDelaySeconds가 너무 짧음
- 포트 불일치

**디버깅 단계**:
```bash
# Actuator 엔드포인트 테스트
kubectl exec -it <pod-이름> -n commerce -- \
  wget -qO- http://localhost:8080/actuator/health

# 프로브 설정 확인
kubectl get deployment <service> -n commerce -o yaml | grep -A 10 Probe
```

**일반적인 해결책**:
- `initialDelaySeconds` 증가 (liveness 60초, readiness 30초)
- Spring Boot에서 actuator 활성화 확인
- 포트가 8080인지 확인 (8081, 8089 등이 아님)

## 🔍 무엇을 언제 수정할지

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

## 📚 모범 사례

### 리소스 관리

1. **항상 리소스 요청/제한 설정**: 리소스 고갈 방지
2. **적절한 프로브 타이밍 사용**: 시작 중 오탐 방지
3. **리소스 사용량 모니터링**: `kubectl top pods -n commerce`

### 설정 관리

1. **비민감 데이터는 ConfigMap 사용**: 환경 변수, 엔드포인트
2. **민감 데이터는 Secret 사용**: 비밀번호, 토큰, 키
3. **시크릿은 Base64 인코딩**: Kubernetes 요구사항
4. **설정 변경 후 재시작**: Pod는 자동으로 재로드하지 않음

### 배포 전략

1. **항상 2개 이상의 복제본 사용**: 고가용성
2. **롤링 업데이트 사용**: 무중단 배포
3. **운영 전에 개발에서 테스트**: ENV=dev 먼저 사용
4. **헬스 체크 확인**: actuator 엔드포인트 작동 확인

### 보안

1. **디코딩된 시크릿 커밋 금지**: base64 인코딩 사용
2. **정기적으로 시크릿 교체**: JWT 키, DB 비밀번호
3. **운영에서는 제한적 CORS 사용**: `*` 허용 금지
4. **rate limiting 구현**: 남용 방지

### 문제 해결

1. **먼저 로그 확인**: `kubectl logs <pod>`
2. **이벤트 확인**: `kubectl describe pod <pod>`
3. **연결 테스트**: 디버그 pod 사용
4. **리소스 사용량 모니터링**: `kubectl top`

## 🔗 관련 문서

- **프로젝트 루트**: `../` - 메인 프로젝트 README
- **서비스**: `../services/` - 개별 서비스 문서
- **공통 모듈**: `../common/` - 공유 라이브러리 문서
- **부하 테스트**: `../load-test/` - k6 성능 테스트

---

**최종 업데이트**: 2026-01-26
**대상**: Claude Code (AI 개발 어시스턴트)
**목적**: 인프라 운영을 위한 개발자 가이드
