# API 레퍼런스

Payment Service가 제공하는 API 엔드포인트 명세입니다.

## POST /api/payments/confirm

사용자가 Toss Widget에서 결제를 완료하면, 프론트엔드가 이 API를 호출하여 결제를 승인합니다.

### 요청

```http
POST /api/payments/confirm
Content-Type: application/json
```

**Request Body**

```json
{
  "paymentKey": "tgen_...",
  "orderId": 12345,
  "amount": 50000
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `paymentKey` | String | O | Toss Payments에서 발급한 결제 키 |
| `orderId` | Long | O | 주문 ID |
| `amount` | Long | O | 결제 금액 (원 단위) |

### 처리 흐름

1. `orderId`로 결제 엔티티를 조회합니다.
2. `paymentKey`와 `amount`를 검증합니다.
3. `PaymentGateway.approve()`를 호출하여 Toss API에 결제 승인을 요청합니다.
4. 성공 시: `Payment.approve()` 호출 후 `PaymentCompletedEvent`를 발행합니다.
5. 실패 시: `Payment.fail()` 호출 후 `PaymentFailedEvent`를 발행합니다.

### 응답

**200 OK** - 결제 승인 성공

결제가 정상적으로 승인되었으며, `PaymentCompletedEvent`가 발행됩니다.

**400 Bad Request** - 잘못된 요청

금액 불일치, 유효하지 않은 `paymentKey` 등의 사유로 요청이 거부됩니다.

**404 Not Found** - 결제 정보를 찾을 수 없음

해당 `orderId`에 대한 결제 엔티티가 존재하지 않습니다.

**500 Internal Server Error** - 서버 오류

서버 내부 오류가 발생했으며, `PaymentFailedEvent`가 발행될 수 있습니다.

## 에러 코드

### PaymentErrorCode enum

Payment Service에서 사용하는 도메인 에러 코드입니다. 모든 에러 코드는 `ErrorCode` 인터페이스를 구현합니다.

```kotlin
enum class PaymentErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus
) : ErrorCode {

    // 400 Bad Request
    INVALID_PAYMENT_AMOUNT("PAYMENT-400-001", "결제 금액이 올바르지 않습니다.", BAD_REQUEST),
    INVALID_PAYMENT_STATUS("PAYMENT-400-002", "결제 상태가 올바르지 않습니다.", BAD_REQUEST),
    PAYMENT_NOT_READY("PAYMENT-400-003", "결제가 가능한 상태가 아닙니다.", BAD_REQUEST),

    // 404 Not Found
    PAYMENT_NOT_FOUND("PAYMENT-404-001", "결제 정보를 찾을 수 없습니다.", NOT_FOUND),

    // 409 Conflict
    DUPLICATE_PAYMENT_REQUEST("PAYMENT-409-001", "중복된 결제 요청입니다.", CONFLICT),
    PAYMENT_ALREADY_APPROVED("PAYMENT-409-002", "이미 승인된 결제입니다.", CONFLICT),

    // 502 Bad Gateway (PG 오류)
    PAYMENT_GATEWAY_ERROR("PAYMENT-502-001", "결제 게이트웨이 오류가 발생했습니다.", BAD_GATEWAY)
}
```

### 에러 코드 상세

| 코드 | HTTP 상태 | 메시지 | 설명 |
|------|-----------|--------|------|
| `PAYMENT-400-001` | 400 Bad Request | 결제 금액이 올바르지 않습니다. | 요청 금액이 0 이하이거나 결제 엔티티의 금액과 불일치 |
| `PAYMENT-400-002` | 400 Bad Request | 결제 상태가 올바르지 않습니다. | 현재 상태에서 허용되지 않는 액션 요청 |
| `PAYMENT-400-003` | 400 Bad Request | 결제가 가능한 상태가 아닙니다. | 결제 상태가 `READY`가 아닌 상태에서 승인 요청 |
| `PAYMENT-404-001` | 404 Not Found | 결제 정보를 찾을 수 없습니다. | 해당 `orderId`에 대한 결제 엔티티가 존재하지 않음 |
| `PAYMENT-409-001` | 409 Conflict | 중복된 결제 요청입니다. | 이미 처리된 결제에 대한 재요청 |
| `PAYMENT-409-002` | 409 Conflict | 이미 승인된 결제입니다. | 이미 `APPROVED` 상태인 결제에 대한 승인 요청 |
| `PAYMENT-502-001` | 502 Bad Gateway | 결제 게이트웨이 오류가 발생했습니다. | Toss Payments API 호출 시 PG사 오류 발생 |

### 에러 응답 형식

모든 에러 응답은 `common-core`의 `GlobalExceptionHandler`를 통해 일관된 형식으로 반환됩니다.

```json
{
  "success": false,
  "error": {
    "code": "PAYMENT-404-001",
    "message": "결제 정보를 찾을 수 없습니다.",
    "timestamp": "2025-01-25T10:30:00"
  }
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `success` | Boolean | 항상 `false` |
| `error.code` | String | `PaymentErrorCode`의 `code` 값 |
| `error.message` | String | `PaymentErrorCode`의 `message` 값 |
| `error.timestamp` | String | 에러 발생 시각 (ISO 8601) |
