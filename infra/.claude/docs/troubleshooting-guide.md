# 문제 해결 가이드

이 문서는 인프라 운영 중 발생할 수 있는 일반적인 문제와 해결책을 제공합니다.

## 문제 1: Pod가 CrashLoopBackOff 상태

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

---

## 문제 2: 서비스가 데이터베이스에 연결할 수 없음

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

---

## 문제 3: 서비스가 Kafka에 연결할 수 없음

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

---

## 문제 4: Ingress가 트래픽을 라우팅하지 않음

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

---

## 문제 5: 이미지 풀 오류

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

---

## 문제 6: 헬스 체크 실패

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

---

## 빠른 진단 체크리스트

### Pod 문제
1. `kubectl get pods -n commerce` - 상태 확인
2. `kubectl logs <pod> -n commerce` - 로그 확인
3. `kubectl describe pod <pod> -n commerce` - 이벤트 확인
4. `kubectl top pods -n commerce` - 리소스 사용량

### 연결 문제
1. ConfigMap/Secret 확인
2. `host.k3d.internal` 연결 테스트
3. 노드 레벨 인프라 실행 확인 (`docker ps`)

### Ingress 문제
1. `kubectl get ingress -n commerce` - Ingress 상태
2. Traefik 실행 확인
3. 서비스 이름/경로 일치 확인

### 이미지 문제
1. `imagePullPolicy: Never` 확인
2. k3d에 이미지 임포트 여부 확인
3. 이미지 이름 일치 확인
