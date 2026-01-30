# K8s 배포 가이드

이 문서는 Kubernetes 배포 패턴과 Ingress 설정에 대한 상세 가이드를 제공합니다.

## 서비스 배포 패턴

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

## Ingress 설정

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
/api/categories → catalog-service:80
/api/products → catalog-service:80
/api/inventories → inventory-service:80
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

### Ingress 수정 시기

- 새 서비스 경로 추가 시
- 경로 접두사 변경 시
- CORS 정책 업데이트 시
- rate limit 조정 시
- TLS 인증서 추가 시

## 새 서비스 추가 체크리스트

1. **Deployment + Service YAML 생성**
   ```bash
   cp k8s/services/auth-service.yaml k8s/services/new-service.yaml
   ```

2. **YAML 파일 수정**
   - `metadata.name` 변경
   - `spec.selector.matchLabels.app` 변경
   - `spec.template.metadata.labels.app` 변경
   - `spec.template.spec.containers[0].name` 변경
   - `spec.template.spec.containers[0].image` 변경
   - `env.DB_NAME` 값 변경

3. **Ingress 경로 추가**
   - `k8s/ingress-dev.yaml` 편집
   - `k8s/ingress.yaml` 편집 (운영용)

4. **Makefile 업데이트**
   - `SERVICES` 변수에 새 서비스 추가

5. **배포**
   ```bash
   make k8s-services-apply
   make k8s-ingress-apply ENV=dev
   ```
