# 모범 사례

이 문서는 인프라 운영을 위한 리소스, 설정, 배포, 보안 모범 사례를 제공합니다.

## 리소스 관리

1. **항상 리소스 요청/제한 설정**: 리소스 고갈 방지
   ```yaml
   resources:
     requests:
       memory: "512Mi"
       cpu: "250m"
     limits:
       memory: "1Gi"
       cpu: "1000m"
   ```

2. **적절한 프로브 타이밍 사용**: 시작 중 오탐 방지
   ```yaml
   livenessProbe:
     initialDelaySeconds: 60
     periodSeconds: 30
   readinessProbe:
     initialDelaySeconds: 30
     periodSeconds: 10
   ```

3. **리소스 사용량 모니터링**:
   ```bash
   kubectl top pods -n commerce
   ```

## 설정 관리

1. **비민감 데이터는 ConfigMap 사용**: 환경 변수, 엔드포인트
   ```yaml
   envFrom:
     - configMapRef:
         name: commerce-common-config
   ```

2. **민감 데이터는 Secret 사용**: 비밀번호, 토큰, 키
   ```yaml
   envFrom:
     - secretRef:
         name: commerce-common-secret
   ```

3. **시크릿은 Base64 인코딩**: Kubernetes 요구사항
   ```bash
   echo -n "password" | base64
   ```

4. **설정 변경 후 재시작**: Pod는 자동으로 재로드하지 않음
   ```bash
   make k8s-restart
   ```

## 배포 전략

1. **항상 2개 이상의 복제본 사용**: 고가용성
   ```yaml
   spec:
     replicas: 2
   ```

2. **롤링 업데이트 사용**: 무중단 배포
   ```bash
   kubectl rollout restart deployment/order-service -n commerce
   kubectl rollout status deployment/order-service -n commerce
   ```

3. **운영 전에 개발에서 테스트**: ENV=dev 먼저 사용
   ```bash
   # 개발에서 먼저 테스트
   make k8s-apply-all ENV=dev

   # 확인 후 운영 적용
   make k8s-apply-all ENV=prod
   ```

4. **헬스 체크 확인**: actuator 엔드포인트 작동 확인
   ```bash
   kubectl exec -it <pod> -n commerce -- \
     wget -qO- http://localhost:8080/actuator/health
   ```

## 보안

1. **디코딩된 시크릿 커밋 금지**: base64 인코딩 사용
   - `.gitignore`에 평문 시크릿 파일 추가
   - 커밋 전 시크릿 값 확인

2. **정기적으로 시크릿 교체**: JWT 키, DB 비밀번호
   ```bash
   # 새 비밀번호 인코딩
   echo -n "new-password" | base64

   # Secret 업데이트 후 적용
   kubectl apply -f k8s/common/secret.yaml -n commerce
   make k8s-restart
   ```

3. **운영에서는 제한적 CORS 사용**: `*` 허용 금지
   ```yaml
   # 개발용 (허용적)
   accessControlAllowOriginList: ["*"]

   # 운영용 (제한적)
   accessControlAllowOriginList: ["https://your-domain.com"]
   ```

4. **rate limiting 구현**: 남용 방지
   ```yaml
   # 개발용 (높은 한도)
   average: 1000
   burst: 2000

   # 운영용 (엄격한 한도)
   average: 100
   burst: 200
   ```

## 문제 해결

1. **먼저 로그 확인**:
   ```bash
   kubectl logs <pod> -n commerce
   kubectl logs <pod> -n commerce --previous  # 이전 컨테이너 로그
   ```

2. **이벤트 확인**:
   ```bash
   kubectl describe pod <pod> -n commerce
   kubectl get events -n commerce --sort-by='.lastTimestamp'
   ```

3. **연결 테스트**: 디버그 pod 사용
   ```bash
   # MySQL 연결 테스트
   kubectl run -it --rm debug --image=mysql:8 --restart=Never -n commerce \
     -- mysql -h host.k3d.internal -u admin -padmin1234

   # Kafka 연결 테스트
   kubectl run -it --rm kafka-test --image=confluentinc/cp-kafka:latest \
     --restart=Never -n commerce \
     -- kafka-topics --list --bootstrap-server host.k3d.internal:9092
   ```

4. **리소스 사용량 모니터링**:
   ```bash
   kubectl top pods -n commerce
   kubectl top nodes
   ```

## 체크리스트

### 배포 전 체크리스트
- [ ] ConfigMap/Secret 값 확인
- [ ] 이미지가 k3d에 임포트됨
- [ ] 리소스 요청/제한 설정됨
- [ ] 헬스 프로브 설정됨
- [ ] Ingress 경로 추가됨

### 배포 후 체크리스트
- [ ] Pod가 Running 상태
- [ ] 헬스 엔드포인트 응답
- [ ] 로그에 오류 없음
- [ ] Ingress 라우팅 작동
- [ ] 다른 서비스와 연결 확인
