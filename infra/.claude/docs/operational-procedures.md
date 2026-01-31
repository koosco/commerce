# 운영 절차 가이드

인프라 운영 작업에 대한 상세 절차입니다.

## 새 서비스 추가

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

## 환경 변수 변경

### 비민감 (ConfigMap)

```bash
# ConfigMap 편집
vim k8s/common/configmap.yaml

# 변경사항 적용
kubectl apply -f k8s/common/configmap.yaml -n commerce

# 변경사항 반영을 위해 서비스 재시작
make k8s-restart
```

### 민감 (Secret)

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

## 서비스 스케일링

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

## 서비스 설정 업데이트

### Deployment 변경 (복제본, 리소스, 프로브)

```bash
# 매니페스트 편집
vim k8s/services/order-service.yaml

# 적용
kubectl apply -f k8s/services/order-service.yaml -n commerce

# 확인
kubectl get deployment order-service -n commerce
```

### 롤링 업데이트 (코드 변경 후)

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
