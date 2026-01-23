# 재고 감소 동시성 테스트 (Inventory Decrease Concurrency Test)

## 📋 테스트 개요

단일 SKU에 대해 다수의 동시 요청이 발생할 때 재고 감소 엔드포인트의 **동시성 제어(Concurrency Control)**가 정상적으로 작동하는지 검증하는 부하 테스트입니다.

### 테스트 목적

- ✅ 동시성 환경에서 재고 데이터 정합성 검증
- ✅ Race Condition 발생 여부 확인
- ✅ 재고 초과 차감 방지 메커니즘 검증
- ✅ 응답 시간 및 에러율 측정

### 테스트 시나리오

1. 초기 재고: 10000개
2. 동시 요청: 500명의 가상 사용자(VUs)가 각 2개씩 주문
3. 예상 결과: 정확히 1000개만 차감, 초과 요청은 실패 처리

## 🔌 API Endpoint

**POST** `/api/inventories/{skuId}/decrease`

### 요청 (Request)

**Path Parameter:**

- `skuId` (String): 재고 식별자 (`00008217-b1ae-4045-9500-2d4b9fffaa32`)

**Request Body:**

```json
{
  "quantity": 2
}
```

### 응답 (Response)

**성공 응답 (200 OK):**

```json
{
  "success": true,
  "error": null,
  "timestamp": "2024-12-30T02:30:00Z"
}
```

**에러 응답 (500 Internal Server Error):**

```json
{
  "success": false,
  "error": "INTERNAL_SERVER_ERROR",
  "message": "재고 처리 중 오류가 발생했습니다",
  "timestamp": "2024-12-30T02:30:00Z"
}
```

## 🎯 테스트 유형

3가지 부하 테스트 시나리오를 통해 단계적으로 동시성 제어를 검증합니다.

### 1. Smoke Test (기본 기능 검증)

**목적**: 최소 부하로 API 기본 동작 확인

| 항목 | 값 |
|------|-----|
| VUs | 2 |
| Duration | 30초 |
| Thresholds | P95 < 1s, 에러율 < 10% |

**검증 항목**:
- API 연결 정상 여부
- 기본 응답 형식 검증
- 성공 응답 (200) 처리

### 2. Baseline Test (정상 부하 테스트)

**목적**: 일반적인 트래픽 환경에서 성능 기준선 측정

| 항목 | 값 |
|------|-----|
| Stages | Ramp-up(1m) → Load(3m) → Hold(2m) → Ramp-down(1m) |
| Peak VUs | 50 |
| Total Duration | 7분 |
| Thresholds | P95 < 500ms, P99 < 1s, 에러율 < 1% |

**검증 항목**:
- 정상 부하에서의 응답 시간
- 데이터 정합성 유지
- 커스텀 메트릭 수집 (성공/실패 건수)

### 3. Stress Test (고부하 스트레스 테스트)

**목적**: 시스템 한계 및 Race Condition 발생 여부 확인

| 항목 | 값 |
|------|-----|
| Stages | Warm-up(2m) → Ramp-up(3m) → Peak(5m) → Hold(5m) → Recovery(3m) → Cool-down(2m) |
| Peak VUs | 500 |
| Total Duration | 20분 |
| Thresholds | P95 < 1s, P99 < 2s, 에러율 < 5% |

**검증 항목**:
- 대량 동시 요청 처리 능력
- 동시성 제어 메커니즘 안정성
- 시스템 Breaking Point 탐색
- 상세한 에러 추적 및 분석

## 🚀 실행 방법

### 전제 조건

1. Inventory Service가 `http://localhost:8083`에서 실행 중
2. 테스트 데이터 준비:
   - SKU ID: `00008217-b1ae-4045-9500-2d4b9fffaa32`
   - 초기 재고: 10,000개
   - 테스트 데이터는 `data/` 디렉토리에서 관리
3. k6 설치: `brew install k6` (macOS) 또는 [k6 설치 가이드](https://k6.io/docs/getting-started/installation/)
4. 결과 저장 디렉토리: `results/` (자동 생성됨)

### 권장 실행 순서

테스트는 반드시 순서대로 실행하는 것을 권장합니다:

```bash
# Step 1: Smoke Test (필수) - 기본 기능 확인
k6 run scripts/inventory/decrease_concurrency/smoke.test.js

# Step 2: Baseline Test - 정상 부하 성능 측정
k6 run scripts/inventory/decrease_concurrency/baseline.test.js

# Step 3: Stress Test - 고부하 테스트 (DB 백업 후 실행 권장)
k6 run scripts/inventory/decrease_concurrency/stress.test.js
````

### 환경별 실행

```bash
# Local 환경 (기본)
k6 run scripts/inventory/decrease_concurrency/smoke.test.js

# Dev 환경
k6 run -e ENV=dev scripts/inventory/decrease_concurrency/baseline.test.js

# 커스텀 VUs 및 Duration 설정
k6 run --vus 100 --duration 60s scripts/inventory/decrease_concurrency/stress.test.js
```

### 결과 파일 저장

모든 테스트 결과는 스크립트 구조와 동일하게 `results/` 디렉토리에 HTML 형식으로 자동 저장됩니다:

```bash
# 결과 저장 구조
scripts/inventory/decrease_concurrency/smoke.test.js
  → results/inventory/decrease_concurrency/smoke.test.result.html

scripts/inventory/decrease_concurrency/baseline.test.js
  → results/inventory/decrease_concurrency/baseline.test.result.html

scripts/inventory/decrease_concurrency/stress.test.js
  → results/inventory/decrease_concurrency/stress.test.result.html
```

각 테스트의 `handleSummary` 함수에서 자동으로 HTML 리포트를 생성합니다.

**추가 출력 옵션**:

```bash
# JSON 형식으로 추가 저장 (선택사항)
k6 run --out json=results/inventory/decrease_concurrency/smoke.test.json \
  scripts/inventory/decrease_concurrency/smoke.test.js

# InfluxDB로 결과 전송 (실시간 모니터링)
k6 run --out influxdb=http://localhost:8086/k6 \
  scripts/inventory/decrease_concurrency/baseline.test.js
```

## ✅ 검증 항목

### 1. 데이터 정합성

- [ ] 최종 재고 = 초기 재고 - (성공 요청 수 × 차감 수량)
- [ ] 재고가 0 미만으로 떨어지지 않음
- [ ] 동시 요청 간 재고 중복 차감 없음

### 2. 응답 상태

- [ ] 성공 시: HTTP 200 OK
- [ ] 서버 에러 시: HTTP 500 Internal Server Error
- [ ] 잘못된 요청 시: HTTP 400 Bad Request

### 3. 성능 지표

- [ ] P95 응답 시간 < 500ms
- [ ] 에러율 < 1%
- [ ] Throughput ≥ 100 RPS

## 🔍 동시성 문제 설명

### Race Condition 시나리오

```
시간 | Thread A          | Thread B          | 재고
-----|-------------------|-------------------|-----
T0   | 재고 조회 (100)   |                   | 100
T1   |                   | 재고 조회 (100)   | 100
T2   | 차감 (100-2=98)   |                   | 100
T3   |                   | 차감 (100-2=98)   | 100
T4   | 저장 (98)         |                   | 98
T5   |                   | 저장 (98)         | 98  ❌ 4개 차감했지만 2개만 반영
```

### 해결 방법

- **비관적 락 (Pessimistic Lock)**: SELECT FOR UPDATE
- **낙관적 락 (Optimistic Lock)**: Version 컬럼 사용
- **분산 락 (Distributed Lock)**: Redis SETNX, Redisson
- **데이터베이스 제약**: CHECK 제약 조건

## 📊 결과 분석

### Smoke Test 결과 예시

```bash
✓ status is 200              ... 100% (60/60)
✓ response time < 1s         ... 100% (60/60)
✓ response has body          ... 100% (60/60)

http_req_duration..............: avg=120ms min=45ms med=110ms max=280ms p(95)=180ms
http_req_failed................: 0.00% (0/60)
vus............................: 2
duration.......................: 30s
```

### Baseline Test 결과 예시

```bash
✓ status is 200              ... 100% (1500/1500)
✓ response time < 500ms      ... 98.7% (1480/1500)
✓ success field is true      ... 100% (1500/1500)

http_req_duration..............: avg=245ms min=45ms med=220ms max=890ms p(95)=420ms
http_req_failed................: 0.00% (0/1500)
http_reqs......................: 1500 (50/s)
successful_decreases...........: 1500
```

### Stress Test 결과 예시

```bash
=============================================================
        STRESS TEST SUMMARY - 재고 감소 동시성 테스트
=============================================================

📊 Request Statistics:
  Total Requests: 15,000
  Successful Decreases (200): 14,950 (99.67%)
  Actual Errors (5xx): 50 (0.33%)

⚡ Performance Metrics:
  Average Response Time: 345ms
  P95 Response Time: 820ms
  P99 Response Time: 1,450ms

📦 Inventory Data:
  Expected Total Decrease: 29,900 items (14,950 requests × 2)

✅ Test Criteria:
  P95 < 1000ms: PASS ✓
  P99 < 2000ms: PASS ✓
  Error Rate < 1%: PASS ✓
```

### 데이터 정합성 검증 방법

테스트 완료 후 데이터베이스에서 직접 확인:

```sql
-- 최종 재고 확인
SELECT sku_id, quantity
FROM inventories
WHERE sku_id = '00008217-b1ae-4045-9500-2d4b9fffaa32';

-- 예상 재고 계산
-- 초기 재고: 10,000개
-- 성공한 차감: (성공 요청 수 × 2)
-- 최종 재고: 10,000 - (성공 요청 수 × 2)
```

## 📁 테스트 파일 구성

```
scripts/inventory/decrease_concurrency/
├── README.md              # 이 문서
├── smoke.test.js          # Smoke Test - 기본 기능 검증
├── baseline.test.js       # Baseline Test - 정상 부하 테스트
└── stress.test.js         # Stress Test - 고부하 스트레스 테스트
```

### 파일 상세

| 파일               | 유형   | VUs        | Duration | 목적               |
| ------------------ | ------ | ---------- | -------- | ------------------ |
| `smoke.test.js`    | Smoke  | 2          | 30초     | API 기본 동작 확인 |
| `baseline.test.js` | Load   | 50 (peak)  | 7분      | 성능 기준선 측정   |
| `stress.test.js`   | Stress | 500 (peak) | 20분     | 시스템 한계 테스트 |

### 공통 의존성

- `/config/local.js`: 로컬 환경 설정 (Inventory Service URL)
- `/config/dev.js`: 개발 환경 설정

## 🔗 참고 자료

- [k6 Documentation](https://k6.io/docs/)
- [동시성 제어 패턴](https://martinfowler.com/articles/patterns-of-distributed-systems/)
- [데이터베이스 락 전략](https://vladmihalcea.com/database-locking-patterns/)
