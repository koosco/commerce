# Baseline Load Test Report

- **실행일시**: 2026-02-03 02:00 ~ 03:40 (KST)
- **환경**: Production (k3s, `commerce.koomango.com`)
- **테스트 도구**: k6 v1.5.0
- **테스트 구성**: 3-Stage Ramp (1m→20VU, 1m→50VU, 5m hold 50VU), 총 7분/테스트
- **Threshold 기준**: p(95) < 500ms, p(99) < 1000ms, error rate < 1%

---

## 1. 종합 결과 요약

| # | Service | Endpoint | Requests | RPS | Avg (ms) | Med (ms) | P95 (ms) | Error Rate | 판정 |
|---|---------|----------|----------|-----|----------|----------|----------|------------|------|
| 1 | Auth | `POST /api/auth/login` | 4,932 | 11.6 | 2,602 | 1,658 | 7,193 | 0% | **FAIL** |
| 2 | Catalog | `GET /api/categories` | 16,234 | 38.5 | 88.5 | 84.0 | 110.4 | 0% | PASS |
| 3 | Catalog | `GET /api/categories/tree` | 16,300 | 38.6 | 84.7 | 82.6 | 107.7 | 0% | PASS |
| 4 | Catalog | `GET /api/products` | 15,965 | 37.8 | 107.5 | 104.8 | 130.7 | 0% | PASS |
| 5 | Catalog | `GET /api/products/{id}` | 16,261 | 38.5 | 87.5 | 83.2 | 108.5 | 0% | PASS |
| 6 | Catalog | `GET /api/products/{id}/skus` | 4 | 0.01 | 286.8 | 316.6 | 393.3 | 0% | **N/A** |
| 7 | User | `POST /api/users` | 4,591 | 10.8 | 2,870 | 2,253 | 5,576 | 18.95% | **FAIL** |
| 8 | Inventory | `GET /api/inventories/{skuId}` | 16,304 | 38.5 | 85.4 | 82.3 | 108.4 | 0.03% | PASS |
| 9 | Inventory | `POST /api/inventories/bulk` | 16,251 | 38.5 | 88.3 | 82.7 | 108.6 | 0.03% | PASS |
| 10 | Inventory | `POST /api/inventories/{id}/increase` | 16,330 | 38.6 | 83.7 | 82.1 | 107.4 | 0.03% | PASS |
| 11 | Inventory | `POST /api/inventories/{id}/decrease` | 16,331 | 38.5 | 83.5 | 81.5 | 107.7 | 44.96% | **FAIL*** |
| 12 | Order | `GET /api/orders` | 16,235 | 38.4 | 89.1 | 83.8 | 110.4 | 0% | PASS |
| 13 | Order | `POST /api/orders` | 13,771 | 32.6 | 285.0 | 271.4 | 424.9 | 0.04% | PASS |
| 14 | Payment | `POST /api/payments/confirm` | 16,340 | 38.7 | 82.4 | 82.1 | 102.8 | 99.98% | **FAIL*** |

> `*` 표시: 테스트 시나리오 특성상 에러가 예상되는 케이스 (아래 상세 분석 참조)

---

## 2. 서비스별 상세 분석

### 2.1 Auth Service - `POST /api/auth/login`

| Metric | Value |
|--------|-------|
| Avg / Med / P95 / Max | 2,602ms / 1,658ms / 7,193ms / 13,298ms |
| 총 요청 / RPS | 4,932 / 11.6 |
| HTTP 에러율 | 0% (모든 요청 200 OK) |
| Threshold | **FAIL** (p95: 7,193ms >> 500ms) |

**분석**:
- 모든 요청이 성공(200)하지만 응답 시간이 매우 느림
- 50VU 부하에서 p95가 7초를 초과하여 baseline 기준(500ms)의 14배
- RPS가 11.6으로 다른 서비스 대비 1/3 수준 → 요청 처리가 순차적으로 병목
- **원인 추정**: bcrypt 비밀번호 해싱이 CPU-intensive하여 동시 요청 처리 시 큐잉 발생
- `has accessToken` 체크 100% 실패 → 응답 필드명이 테스트 스크립트와 불일치 (기능 이슈 아님)

**권장 조치**:
- bcrypt round 수 검토 (현재 값 확인 필요)
- Auth pod 수평 확장 (현재 2 replica) 또는 CPU 리소스 상향
- 테스트 스크립트의 `has accessToken` 체크 필드명 수정

---

### 2.2 Catalog Service

| Endpoint | Avg (ms) | P95 (ms) | RPS | 판정 |
|----------|----------|----------|-----|------|
| `GET /api/categories` | 88.5 | 110.4 | 38.5 | PASS |
| `GET /api/categories/tree` | 84.7 | 107.7 | 38.6 | PASS |
| `GET /api/products` | 107.5 | 130.7 | 37.8 | PASS |
| `GET /api/products/{id}` | 87.5 | 108.5 | 38.5 | PASS |
| `GET /api/products/{id}/skus` | 286.8 | 393.3 | 0.01 | **N/A** |

**분석**:
- 카테고리/상품 조회 API 전반적으로 우수한 성능 (p95 < 131ms)
- Product List가 상대적으로 약간 느림 (107ms avg) → 페이지네이션 쿼리 + 데이터 양 영향
- **Product SKUs 테스트 이상**: 7분 동안 4건만 처리 → setup 단계에서 productId 조회 실패 추정
  - `fetchSkuIds()` 또는 product ID 선택 로직 점검 필요

**권장 조치**:
- Product SKUs 테스트 스크립트의 setup 로직 디버깅
- Catalog 서비스는 현재 성능이 양호하므로 추가 조치 불필요

---

### 2.3 User Service - `POST /api/users`

| Metric | Value |
|--------|-------|
| Avg / Med / P95 / Max | 2,870ms / 2,253ms / 5,576ms / 6,083ms |
| 총 요청 / RPS | 4,591 / 10.8 |
| HTTP 에러율 | 18.95% |
| Threshold | **FAIL** (에러율 18.95% >> 1%) |

**분석**:
- Auth와 유사하게 높은 지연시간 → 회원가입 시 bcrypt 해싱이 병목
- 18.95% 에러율 → 높은 부하에서 타임아웃 또는 DB 제약조건 충돌 가능성
- RPS 10.8로 Auth(11.6)와 비슷한 수준 → 두 서비스 모두 CPU-bound 패턴

**권장 조치**:
- User pod 수평 확장 또는 CPU 리소스 상향
- 에러 응답 로그 분석 (409 Conflict vs 500 Server Error 구분)

---

### 2.4 Inventory Service

| Endpoint | Avg (ms) | P95 (ms) | Error Rate | 판정 |
|----------|----------|----------|------------|------|
| `GET /api/inventories/{skuId}` | 85.4 | 108.4 | 0.03% | PASS |
| `POST /api/inventories/bulk` | 88.3 | 108.6 | 0.03% | PASS |
| `POST /api/inventories/{id}/increase` | 83.7 | 107.4 | 0.03% | PASS |
| `POST /api/inventories/{id}/decrease` | 83.5 | 107.7 | 44.96% | FAIL* |

**분석**:
- 재고 조회/증가 API는 매우 안정적 (p95 < 109ms, 에러율 0.03%)
- Redis + MariaDB 하이브리드 구조가 효과적으로 작동
- **Decrease (재고 차감)**: 44.96% 에러율은 테스트 특성에 기인
  - 동시 50VU가 동일 SKU의 재고를 차감 → 재고 부족 에러가 정상적으로 발생
  - 응답 시간 자체는 83.5ms로 양호 → 에러 처리가 빠르게 수행됨
  - 이는 **동시성 제어가 정상 작동**하고 있음을 의미

**권장 조치**:
- Inventory 서비스는 현재 성능이 양호
- Decrease 테스트의 높은 에러율은 예상된 동작 (재고 소진)

---

### 2.5 Order Service

| Endpoint | Avg (ms) | P95 (ms) | RPS | 판정 |
|----------|----------|----------|-----|------|
| `GET /api/orders` | 89.1 | 110.4 | 38.4 | PASS |
| `POST /api/orders` | 285.0 | 424.9 | 32.6 | PASS |

**분석**:
- 주문 목록 조회는 빠르고 안정적 (p95 110ms)
- 주문 생성은 상대적으로 느리지만 baseline threshold 이내 (p95 425ms < 500ms)
  - 주문 생성은 여러 검증 + DB write + 이벤트 발행이 포함되므로 합리적
- RPS 32.6은 다른 조회 API 대비 약간 낮음 → write 작업의 특성

**권장 조치**:
- 현재 성능은 양호하나 p95(425ms)가 threshold(500ms)에 근접
- 향후 부하 증가 시 주문 생성 성능 모니터링 필요

---

### 2.6 Payment Service - `POST /api/payments/confirm`

| Metric | Value |
|--------|-------|
| Avg / Med / P95 / Max | 82.4ms / 82.1ms / 102.8ms / 469.6ms |
| 총 요청 / RPS | 16,340 / 38.7 |
| HTTP 에러율 | 99.98% |
| Threshold | **FAIL** (에러율 99.98% >> 50%) |

**분석**:
- 99.98% 에러율은 **테스트 환경 한계**에 기인
  - 테스트에서 가짜 `paymentKey`와 `orderId`를 사용 → 유효한 결제 세션이 없어 실패
  - 실제 결제 플로우(Toss Payments 연동)는 유효한 결제 키가 필요
- 응답 시간 자체는 82.4ms로 매우 빠름 → 서버 처리 성능은 양호
- 빠른 실패 응답은 입력 검증이 효율적으로 작동하고 있음을 의미

**권장 조치**:
- Payment 테스트는 현재 구조로는 의미 있는 성능 측정이 어려움
- Mock 결제 엔드포인트 또는 테스트 모드 도입 검토

---

## 3. 서비스별 성능 등급

| 등급 | 서비스 | 기준 |
|------|--------|------|
| **A** (우수) | Catalog, Inventory (조회/증가), Order (조회) | p95 < 120ms, 에러율 < 0.1% |
| **B** (양호) | Order (생성) | p95 < 500ms, 에러율 < 0.1% |
| **C** (개선 필요) | Auth (로그인), User (가입) | p95 > 5,000ms, 리소스 병목 |
| **N/A** (측정 불가) | Catalog (SKUs), Payment (확인) | 테스트 시나리오 한계 |

---

## 4. 주요 발견 사항

### 4.1 성능 병목 - Auth/User 서비스 (bcrypt)
- Auth 로그인과 User 가입 모두 p95 > 5초로 심각한 지연
- 두 서비스 모두 RPS ~11 수준으로 처리량 제한
- **공통 원인**: bcrypt 해싱의 CPU-intensive 특성
- k3s 환경의 제한된 CPU 리소스에서 동시 50VU 부하 시 큐잉 발생

### 4.2 우수한 성능 - Catalog/Inventory/Order 서비스
- 대부분의 조회 API가 p95 < 130ms로 안정적
- Inventory의 Redis 캐시 계층이 효과적으로 작동
- Order 생성도 p95 425ms로 threshold 이내

### 4.3 테스트 스크립트 이슈
- **Catalog Product SKUs**: setup 단계 실패로 4건만 실행 → 스크립트 수정 필요
- **Auth Login**: `has accessToken` 체크 필드명 불일치 → 스크립트 수정 필요
- **Payment Confirm**: 유효한 결제 세션 없이 테스트 → 테스트 전략 재검토 필요

---

## 5. 권장 개선 사항 (우선순위 순)

1. **Auth/User 서비스 성능 개선** (High)
   - Pod replica 증가 또는 CPU request/limit 상향
   - bcrypt cost factor 검토 (10 → 8 등 트레이드오프 고려)

2. **테스트 스크립트 수정** (Medium)
   - Product SKUs baseline 테스트 setup 로직 디버깅
   - Auth login `has accessToken` 필드명 수정
   - Payment confirm 테스트 전략 재설계

3. **Order 생성 성능 모니터링** (Low)
   - p95(425ms)가 threshold(500ms)에 근접하여 향후 부하 증가 시 초과 가능성

---

## 6. HTML 리포트 파일

각 테스트의 상세 HTML 리포트는 `load-test/results/` 디렉토리에 타임스탬프와 함께 저장되었습니다.

---

*Generated on 2026-02-03 by k6 baseline load test suite*
