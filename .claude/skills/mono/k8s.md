# mono Kubernetes Deployment

이 skill은 k8s/k3d 배포 가이드를 참조합니다.

## 사용 시점

- Kubernetes 배포가 필요할 때
- 서비스 스케일링이 필요할 때
- 로그 확인이 필요할 때
- 포트 포워딩이 필요할 때

## 작업 디렉토리

모든 Makefile 명령어는 `infra/` 디렉토리에서 실행합니다:

```bash
cd infra
```

## Quick Reference

### 1. Namespace 관리

```bash
# Namespace 생성
make k8s-ns-create

# Namespace 삭제 (확인 필요)
make k8s-ns-delete

# Namespace 목록
make k8s-ns-list

# 현재 컨텍스트를 commerce namespace로 설정
make k8s-ns-switch
```

### 2. 리소스 적용

```bash
# 전체 리소스 적용 (개발 환경)
make k8s-apply-all ENV=dev

# 전체 리소스 적용 (운영 환경)
make k8s-apply-all ENV=prod

# 상태 확인
make k8s-status
```

### 3. 서비스 시작/중지

```bash
# 모든 서비스 시작
make k8s-start

# 모든 서비스 중지 (replicas=0)
make k8s-stop

# 모든 서비스 재시작 (rolling restart)
make k8s-restart
```

### 4. 스케일링

```bash
# 전체 서비스 스케일링
make k8s-scale REPLICAS=3

# 특정 서비스 스케일링
make k8s-scale-service SERVICE=auth-service REPLICAS=3
```

### 5. 상태 확인

```bash
# Deployment 상태
make k8s-deployments

# 전체 상태 (namespace, ingress, svc, pods)
make k8s-status

# 특정 리소스만
make k8s-status resource=pods
make k8s-status resource=svc
make k8s-status resource=ingress
```

### 6. Ingress 관리

```bash
# Ingress 적용 (개발)
make k8s-ingress-apply ENV=dev

# Ingress 적용 (운영)
make k8s-ingress-apply ENV=prod

# Ingress 목록
make k8s-ingress-list

# Ingress 상세 정보
make k8s-ingress-describe
```

### 7. 로컬 접근 (포트 포워딩)

```bash
# Traefik IP 확인
make k8s-traefik-ip

# 포트 포워딩 (기본 8080)
make k8s-port-forward

# 다른 포트로 포워딩
make k8s-port-forward PORT=3000

# 서비스 접근 URL
# http://localhost:8080/api/auth
# http://localhost:8080/api/users
# http://localhost:8080/api/catalog
# http://localhost:8080/api/orders
# http://localhost:8080/api/inventory
# http://localhost:8080/api/payments
```

## kubectl 직접 사용

### Pod 관리

```bash
# Pod 목록
kubectl get pods -n commerce

# Pod 상세 정보
kubectl describe pod <pod-name> -n commerce

# Pod 로그
kubectl logs <pod-name> -n commerce

# 실시간 로그
kubectl logs -f <pod-name> -n commerce

# 이전 컨테이너 로그
kubectl logs <pod-name> -n commerce --previous

# Pod 접속
kubectl exec -it <pod-name> -n commerce -- /bin/sh
```

### Deployment 관리

```bash
# Deployment 목록
kubectl get deployments -n commerce

# Deployment 상세
kubectl describe deployment auth-service -n commerce

# 이미지 업데이트
kubectl set image deployment/auth-service auth-service=auth-service:v2 -n commerce

# Rollout 상태
kubectl rollout status deployment/auth-service -n commerce

# Rollback
kubectl rollout undo deployment/auth-service -n commerce
```

### Service 관리

```bash
# Service 목록
kubectl get svc -n commerce

# Service 상세
kubectl describe svc auth-service -n commerce

# 특정 서비스 포트 포워딩
kubectl port-forward svc/auth-service 8089:80 -n commerce
```

### ConfigMap & Secret

```bash
# ConfigMap 목록
kubectl get configmap -n commerce

# Secret 목록
kubectl get secret -n commerce

# Secret 값 확인 (base64 디코딩)
kubectl get secret <secret-name> -n commerce -o jsonpath='{.data.password}' | base64 -d
```

## k3d 클러스터 관리

```bash
# 클러스터 목록
k3d cluster list

# 클러스터 시작
k3d cluster start commerce

# 클러스터 중지
k3d cluster stop commerce

# 클러스터 삭제
k3d cluster delete commerce
```

## 트러블슈팅

### Pod이 시작되지 않을 때

```bash
# 이벤트 확인
kubectl get events -n commerce --sort-by='.lastTimestamp'

# Pod 상태 확인
kubectl describe pod <pod-name> -n commerce

# 로그 확인
kubectl logs <pod-name> -n commerce
```

### OOMKilled (메모리 부족)

```bash
# 리소스 사용량 확인
kubectl top pods -n commerce

# 리소스 제한 확인
kubectl describe pod <pod-name> -n commerce | grep -A5 "Limits\|Requests"
```

### ImagePullBackOff

```bash
# 이미지 확인
kubectl describe pod <pod-name> -n commerce | grep -A5 "Image"

# Docker 레지스트리 인증 확인
kubectl get secret -n commerce
```

## Ingress 라우팅

| Path | Service | Port |
|------|---------|------|
| `/api/auth` | auth-service | 80 |
| `/api/users` | user-service | 80 |
| `/api/orders` | order-service | 80 |
| `/api/catalog` | catalog-service | 80 |
| `/api/inventory` | inventory-service | 80 |
| `/api/payments` | payment-service | 80 |

## Kafka for k3d

k3d 클러스터에서 Kafka 사용:

```bash
# k3d용 Kafka 시작
make kafka-dev

# host.k3d.internal로 접근 가능
```
