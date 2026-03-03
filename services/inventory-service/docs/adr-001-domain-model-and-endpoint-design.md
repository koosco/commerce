# ADR-001: 공통 도메인 모델 및 엔드포인트 설계

## Status

Accepted

## Context

일반 구매와 Flash Sale을 동일한 주문 흐름으로만 처리하면 내부 로직이 지나치게 복잡해진다.
외부 API는 리소스 중심으로 유지하되, 내부에서는 일반 구매와 Flash Sale의 재고 확보 전략을 분리할 필요가 있다.

## Decision

### 1. 외부 API 경로

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/orders` | 주문 생성 (일반 구매 / reservationId 기반 Flash Sale) |
| GET | `/api/flash-sales` | Flash Sale 목록 조회 |
| GET | `/api/flash-sales/{flashSaleId}` | Flash Sale 상세 조회 |
| POST | `/api/flash-sales/{flashSaleId}/reservations` | Flash Sale 예약 생성 |
| DELETE | `/api/flash-sales/{flashSaleId}/reservations/{reservationId}` | Flash Sale 예약 취소 |

### 2. 내부 API 경로

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/internal/inventory/reservations` | 재고 예약 (일반 구매 시 order-service가 호출) |
| POST | `/internal/inventory/confirmations` | 재고 확정 (결제 성공 후) |
| POST | `/internal/inventory/releases` | 재고 해제 (주문 취소/결제 실패 시) |

### 3. 주문 상태 모델

Order 상태는 `order-service`에서 관리하며, 기존 `OrderStatus`와 호환됩니다.

| 상태 | 설명 | 비고 |
|------|------|------|
| CREATED | 주문 생성됨 | 기존 유지 |
| RESERVED | 재고 예약 완료 | 기존 유지 |
| PAYMENT_CREATED | 결제 초기화 완료 | 기존 유지 |
| PAYMENT_PENDING | 결제 대기 중 | 기존 유지 |
| PAID | 결제 완료 | 기존 유지 |
| CONFIRMED | 재고 확정 차감 완료 | 기존 유지 |
| CANCELLED | 결제 취소 | 기존 유지 |
| FAILED | 실패 | 기존 유지 |

이슈에서 정의한 PENDING_PAYMENT, EXPIRED는 기존 상태와 매핑됩니다:
- `PENDING_PAYMENT` -> `PAYMENT_PENDING` (기존 상태 유지)
- `EXPIRED` -> Flash Sale 예약의 만료는 `ReservationStatus.EXPIRED`로 분리

### 4. Flash Sale 예약 상태 모델

`ReservationStatus` enum으로 inventory-service 도메인에서 관리합니다.

| 상태 | 설명 | 전이 조건 |
|------|------|----------|
| RESERVED | 예약 완료 | 사용자가 Flash Sale 예약 시 |
| CONFIRMED | 확정됨 | 주문 생성 시 reservationId로 확정 |
| RELEASED | 해제됨 | 사용자 취소 또는 시스템 판단 |
| EXPIRED | 만료됨 | TTL 만료 시 자동 해제 |

### 5. 재고 표현 방식

#### 일반 구매 (기존 Stock VO)
```kotlin
data class Stock(
    val total: Int,      // 전체 재고
    val reserved: Int,   // 예약 재고
) {
    val available: Int   // = total - reserved
}
```

#### Flash Sale (FlashSaleStock VO)
```kotlin
data class FlashSaleStock(
    val available: Int,  // 구매 가능 수량
    val reserved: Int,   // 예약 중인 수량
    val sold: Int,       // 판매 확정 수량
) {
    val total: Int       // = available + reserved + sold
}
```

### 6. 식별자/멱등성 키 규칙

| 식별자 | 형식 | 생성 주체 | 용도 |
|--------|------|----------|------|
| orderId | Long (DB auto-increment) | order-service | 주문 식별 |
| reservationId | String (UUID) | inventory-service | Flash Sale 예약 식별 |
| eventId | String (UUID) | 발행 서비스 | Kafka 이벤트 멱등성 |
| idempotencyKey | String | 호출 서비스 | 내부 API 멱등성 |

### 7. 내부 API 요청/응답 DTO

#### POST /internal/inventory/reservations

Request:
```json
{
  "orderId": 12345,
  "items": [
    { "skuId": "SKU-001", "quantity": 2 },
    { "skuId": "SKU-002", "quantity": 1 }
  ],
  "idempotencyKey": "reserve-order-12345",
  "correlationId": "order-12345"
}
```

Response:
```json
{
  "orderId": 12345,
  "reservedItems": [
    { "skuId": "SKU-001", "quantity": 2 },
    { "skuId": "SKU-002", "quantity": 1 }
  ]
}
```

#### POST /internal/inventory/confirmations

Request:
```json
{
  "orderId": 12345,
  "items": [
    { "skuId": "SKU-001", "quantity": 2 },
    { "skuId": "SKU-002", "quantity": 1 }
  ],
  "idempotencyKey": "confirm-order-12345",
  "correlationId": "order-12345"
}
```

Response:
```json
{
  "orderId": 12345,
  "confirmedItems": [
    { "skuId": "SKU-001", "quantity": 2 },
    { "skuId": "SKU-002", "quantity": 1 }
  ]
}
```

#### POST /internal/inventory/releases

Request:
```json
{
  "orderId": 12345,
  "items": [
    { "skuId": "SKU-001", "quantity": 2 },
    { "skuId": "SKU-002", "quantity": 1 }
  ],
  "reason": "PAYMENT_FAILED",
  "idempotencyKey": "release-order-12345",
  "correlationId": "order-12345"
}
```

Response:
```json
{
  "orderId": 12345,
  "releasedItems": [
    { "skuId": "SKU-001", "quantity": 2 },
    { "skuId": "SKU-002", "quantity": 1 }
  ]
}
```

## Consequences

- 일반 구매와 Flash Sale의 재고 확보 전략이 명확히 분리됨
- 외부 API는 리소스 중심(RESTful)을 유지하면서 내부는 동작 기반으로 설계
- 내부 API는 `/internal/inventory/*` 경로로 통일하여 서비스 간 호출 규약이 명확해짐
- Flash Sale 예약은 독립 엔티티(`FlashSaleReservation`)로 관리되어 일반 주문 흐름과 간섭하지 않음
- 멱등성 키가 모든 내부 API에 일관되게 적용됨
