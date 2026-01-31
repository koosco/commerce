# Smoke Test 서버 측 이슈 진단

> 마지막 업데이트: 2026-02-01

Load-test 스크립트 수정으로 해결된 항목(A 그룹)은 이미 반영 완료.
아래는 서버 측 확인/수정이 필요한 항목(B 그룹).

---

## B-1. Catalog Service — GET 엔드포인트 401

### 현상
- `/api/products`, `/api/categories` 등 GET 요청이 prod에서 401 반환
- `CatalogPublicEndpointProvider`가 해당 경로를 public으로 선언하고 있음에도 인증 실패

### 원인 후보
1. `CatalogPublicEndpointProvider` 빈이 로딩되지 않음 (component scan 누락, auto-configuration 조건 미충족)
2. SecurityFilterChain 순서 문제로 JWT 필터가 먼저 적용
3. `PublicEndpointProvider`가 path 기반이라 HTTP method 구분 불가 — wildcard(`/api/products/**`)가 POST/PUT/DELETE까지 public으로 열리는 보안 허점 존재

### 확인 방법
```bash
# 빈 로딩 확인
kubectl logs -l app=catalog-service | grep -i "PublicEndpoint\|SecurityFilterChain"

# 직접 요청 테스트
kubectl exec -it <pod> -- curl -v http://localhost:8084/api/products
```

### 권장 수정
`PublicEndpointProvider` 대신 서비스별 `SecurityFilterChain`에서 HTTP method를 구분:

```kotlin
// 예시
http.authorizeHttpRequests {
    it.requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
    it.requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
    it.anyRequest().authenticated()
}
```

| 접근 | 엔드포인트 | HTTP 메서드 |
|------|----------|------------|
| Public | `/api/products`, `/api/products/{id}`, `/api/products/{id}/skus` | GET |
| Public | `/api/categories`, `/api/categories/tree` | GET |
| Authenticated | `/api/products`, `/api/products/{id}` | POST, PUT, DELETE |
| Authenticated | `/api/categories`, `/api/categories/tree` | POST |

---

## B-2. Inventory Service 500 — Redis/데이터 문제

### 현상
- inventory-service 호출 시 500 Internal Server Error
- 재고 조회 및 차감 모두 실패

### 원인 후보
1. Redis 연결 실패 (`REDIS_HOST: 192.168.75.174`)
2. 조회 대상 SKU UUID (`00008217-b1ae-4045-9500-2d4b9fffaa32`)가 prod DB에 존재하지 않음
3. Redis AOF 파일 손상 또는 메모리 부족

### 확인 방법
```bash
# Redis 연결 상태
kubectl exec -it <redis-pod> -- redis-cli ping

# inventory-service 로그에서 실제 에러 확인
kubectl logs -l app=inventory-service --tail=100 | grep -i "error\|exception"

# SKU 존재 여부 확인
kubectl exec -it <mariadb-pod> -- mysql -u root -p commerce_inventory \
  -e "SELECT * FROM sku WHERE sku_uuid = '00008217-b1ae-4045-9500-2d4b9fffaa32';"
```

### 영향
- **B-5 (Order Create 실패)** 가 이 이슈에 의존. B-2 해결 시 자동 해결 예상.

---

## B-3. Auth Service 간헐적 실패 (64% 성공률)

### 현상
- 로그인 요청 중 약 36%가 실패
- 타임아웃 또는 5xx 응답

### 원인 후보
1. auth-service 파드 리소스 부족 (CPU/메모리 limit)
2. MariaDB 커넥션 풀 고갈
3. JVM GC 압박 (힙 사이즈 부족)

### 확인 방법
```bash
# 파드 리소스 사용량
kubectl top pod -l app=auth-service

# 로그 확인
kubectl logs -l app=auth-service --tail=200 | grep -i "error\|timeout\|connection"

# DB 커넥션 상태
kubectl exec -it <mariadb-pod> -- mysql -u root -p -e "SHOW PROCESSLIST;"
```

### 영향
- **B-4 (Registration 실패)** 가 이 이슈에 의존. B-3 해결 시 자동 해결 예상.

---

## B-4. Registration 96% 실패

### 현상
- user-service 회원가입 요청 대부분 실패
- user-service가 auth-service로 동기 HTTP 콜백 → 실패 → 보상 트랜잭션으로 user 삭제

### 원인
- B-3 (auth-service 간헐적 실패)의 연쇄 영향
- auth-service가 불안정하면 user-service의 회원가입 플로우 전체가 실패

### 해결
- B-3 해결 시 자동 해결 예상

---

## B-5. Order Create 실패

### 현상
- 주문 생성 시 재고 확인 단계에서 실패

### 원인
- B-2 (inventory-service 500)의 연쇄 영향
- inventory-service가 재고 조회/차감에 실패하면 주문 플로우 중단

### 해결
- B-2 해결 시 자동 해결 예상

---

## 의존 관계 요약

```
B-2 (Inventory 500) ──→ B-5 (Order Create 실패)
B-3 (Auth 간헐적 실패) ──→ B-4 (Registration 실패)
B-1 (Catalog 401) ──→ 독립 이슈
```

**우선순위**: B-3 → B-2 → B-1 순으로 해결하면 5개 이슈 중 4개가 해소됨.
