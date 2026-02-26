---
name: mono-k8s
description: Kubernetes/k3d 배포 가이드. 서비스 배포, 스케일링, 로그 확인, 포트 포워딩이 필요할 때 사용합니다.
---

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
# http://localhost:8080/api/categories
# http://localhost:8080/api/products
# http://localhost:8080/api/orders
# http://localhost:8080/api/inventories
# http://localhost:8080/api/payments
```

---

## 서비스 배포 패턴

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

---

## Ingress 설정

### 개발 환경 (ingress-dev.yaml)

- HTTP만 사용 (TLS 없음), 모든 호스트 허용 (localhost 지원)
- 허용적 CORS (`*`), 높은 rate limit (1000 평균, 2000 버스트)

**라우팅**:
```
/api/auth → auth-service:80
/api/users → user-service:80
/api/categories → catalog-service:80
/api/products → catalog-service:80
/api/inventories → inventory-service:80
/api/orders → order-service:80
/api/payments → payment-service:80
```

### 운영 환경 (ingress.yaml)

- TLS가 포함된 HTTPS, 특정 도메인만 허용
- 제한적 CORS, 엄격한 rate limit

### Ingress 수정 시기

- 새 서비스 경로 추가 시
- 경로 접두사 변경 시
- CORS 정책 업데이트 시
- rate limit 조정 시

---

## ConfigMap & Secret

### ConfigMap (k8s/common/configmap.yaml)

| 변수 | 값 | 용도 |
|------|-----|------|
| DB_HOST | `host.k3d.internal` | 외부 MariaDB |
| DB_PORT | `3306` | DB 포트 |
| SPRING_KAFKA_BOOTSTRAP_SERVERS | `host.k3d.internal:9092` | Kafka |
| REDIS_HOST / REDIS_PORT | `host.k3d.internal` / `6379` | Redis |
| JWT_EXPIRATION | `86400` (24시간) | JWT 만료 |
| JWT_REFRESH_EXPIRATION | `604800` (7일) | Refresh 만료 |
| SPRING_PROFILES_ACTIVE | `dev` | Spring 프로파일 |
| LOGGING_LEVEL_ROOT / COM_KOOSCO | `INFO` / `DEBUG` | 로그 레벨 |

### Secret (k8s/common/secret.yaml)

| 변수 | 설명 |
|------|------|
| DB_USERNAME | DB 사용자명 |
| DB_PASSWORD | DB 비밀번호 |
| JWT_SECRET | JWT 서명 키 |

```bash
# 인코딩/디코딩
echo -n "new-password" | base64
echo "YWRtaW4xMjM0" | base64 -d
```

---

## 새 서비스 추가 체크리스트

1. **Deployment + Service YAML 생성**: `cp k8s/services/auth-service.yaml k8s/services/new-service.yaml`
2. **YAML 파일 수정**: `metadata.name`, `selector`, `labels`, `containers[0].name/image`, `env.DB_NAME`
3. **Ingress 경로 추가**: `k8s/ingress-dev.yaml`, `k8s/ingress.yaml`
4. **Makefile 업데이트**: `SERVICES` 변수에 새 서비스 추가
5. **배포**: `make k8s-services-apply && make k8s-ingress-apply ENV=dev`

---

## kubectl 직접 사용

### Pod 관리

```bash
kubectl get pods -n commerce
kubectl describe pod <pod-name> -n commerce
kubectl logs <pod-name> -n commerce
kubectl logs -f <pod-name> -n commerce          # 실시간 로그
kubectl logs <pod-name> -n commerce --previous   # 이전 컨테이너
kubectl exec -it <pod-name> -n commerce -- /bin/sh
```

### Deployment 관리

```bash
kubectl get deployments -n commerce
kubectl describe deployment auth-service -n commerce
kubectl set image deployment/auth-service auth-service=auth-service:v2 -n commerce
kubectl rollout status deployment/auth-service -n commerce
kubectl rollout undo deployment/auth-service -n commerce
```

### Service 관리

```bash
kubectl get svc -n commerce
kubectl describe svc auth-service -n commerce
kubectl port-forward svc/auth-service 8089:80 -n commerce
```

### ConfigMap & Secret

```bash
kubectl get configmap -n commerce
kubectl get secret -n commerce
kubectl get secret <secret-name> -n commerce -o jsonpath='{.data.password}' | base64 -d
```

## k3d 클러스터 관리

```bash
k3d cluster list
k3d cluster start commerce
k3d cluster stop commerce
k3d cluster delete commerce
```

## 트러블슈팅

### Pod이 시작되지 않을 때

```bash
kubectl get events -n commerce --sort-by='.lastTimestamp'
kubectl describe pod <pod-name> -n commerce
kubectl logs <pod-name> -n commerce
```

### OOMKilled (메모리 부족)

```bash
kubectl top pods -n commerce
kubectl describe pod <pod-name> -n commerce | grep -A5 "Limits\|Requests"
```

### ImagePullBackOff

```bash
kubectl describe pod <pod-name> -n commerce | grep -A5 "Image"
kubectl get secret -n commerce
```

## Ingress 라우팅

| Path | Service | Port |
|------|---------|------|
| `/api/auth` | auth-service | 80 |
| `/api/users` | user-service | 80 |
| `/api/orders` | order-service | 80 |
| `/api/categories` | catalog-service | 80 |
| `/api/products` | catalog-service | 80 |
| `/api/inventories` | inventory-service | 80 |
| `/api/payments` | payment-service | 80 |

## Kafka for k3d

```bash
# k3d용 Kafka 시작
make kafka-dev

# host.k3d.internal로 접근 가능
```
