# API 명세

Order Service는 4개의 REST API 엔드포인트를 제공합니다.

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/orders` | 주문 생성 |
| GET | `/api/orders` | 주문 목록 조회 |
| GET | `/api/orders/{orderId}` | 주문 상세 조회 |
| POST | `/api/orders/{orderId}/refund` | 환불 요청 |

모든 API는 `Authorization: Bearer {JWT_TOKEN}` 헤더가 필요합니다.

---

## POST /api/orders

주문을 생성합니다. 주문 생성 후 `OrderPlacedEvent`가 Kafka로 발행되어 Saga 흐름이 시작됩니다.

### 요청

```http
POST /api/orders
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "items": [
    {
      "skuId": "SKU-001",
      "quantity": 2,
      "unitPrice": 25000
    }
  ],
  "discountAmount": 5000
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `items` | `Array` | O | 주문 아이템 목록 |
| `items[].skuId` | `String` | O | SKU 식별자 |
| `items[].quantity` | `Int` | O | 수량 |
| `items[].unitPrice` | `Long` | O | 단가 (원) |
| `discountAmount` | `Long` | X | 할인 금액 (기본값: 0) |

### 응답

```json
{
  "success": true,
  "data": {
    "orderId": 123,
    "status": "CREATED",
    "payableAmount": 45000
  },
  "timestamp": "2026-01-25T12:34:56Z"
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `orderId` | `Long` | 생성된 주문 ID |
| `status` | `String` | 주문 상태 (`CREATED`) |
| `payableAmount` | `Long` | 실제 결제 요청 금액 (원금 - 할인) |

---

## GET /api/orders

현재 사용자의 주문 목록을 페이징으로 조회합니다.

### 요청

```http
GET /api/orders?page=0&size=20&sort=createdAt,desc
Authorization: Bearer {JWT_TOKEN}
```

| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| `page` | `Int` | X | 페이지 번호 (기본값: 0) |
| `size` | `Int` | X | 페이지 크기 (기본값: 20) |
| `sort` | `String` | X | 정렬 기준 (기본값: `createdAt,desc`) |

### 응답

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "orderId": 123,
        "userId": 1,
        "status": "CONFIRMED",
        "totalAmount": 50000,
        "payableAmount": 45000,
        "createdAt": "2026-01-25T12:34:56Z"
      }
    ],
    "totalElements": 10,
    "totalPages": 1,
    "number": 0,
    "size": 20
  }
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `content` | `Array` | 주문 목록 |
| `content[].orderId` | `Long` | 주문 ID |
| `content[].userId` | `Long` | 사용자 ID |
| `content[].status` | `String` | 주문 상태 |
| `content[].totalAmount` | `Long` | 주문 원금 |
| `content[].payableAmount` | `Long` | 실제 결제 금액 |
| `content[].createdAt` | `String` | 주문 생성 시각 (ISO 8601) |
| `totalElements` | `Long` | 전체 주문 수 |
| `totalPages` | `Int` | 전체 페이지 수 |
| `number` | `Int` | 현재 페이지 번호 |
| `size` | `Int` | 페이지 크기 |

---

## GET /api/orders/{orderId}

특정 주문의 상세 정보를 조회합니다. 주문 아이템 목록이 포함됩니다.

### 요청

```http
GET /api/orders/{orderId}
Authorization: Bearer {JWT_TOKEN}
```

| 경로 변수 | 타입 | 설명 |
|----------|------|------|
| `orderId` | `Long` | 조회할 주문 ID |

### 응답

```json
{
  "success": true,
  "data": {
    "orderId": 123,
    "userId": 1,
    "status": "CONFIRMED",
    "totalAmount": 50000,
    "discountAmount": 5000,
    "payableAmount": 45000,
    "items": [
      {
        "itemId": 1,
        "skuId": "SKU-001",
        "quantity": 2,
        "unitPrice": 25000,
        "status": "CONFIRMED"
      }
    ],
    "createdAt": "2026-01-25T12:34:56Z",
    "updatedAt": "2026-01-25T12:35:30Z"
  }
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `orderId` | `Long` | 주문 ID |
| `userId` | `Long` | 사용자 ID |
| `status` | `String` | 주문 상태 |
| `totalAmount` | `Long` | 주문 원금 (아이템 합계) |
| `discountAmount` | `Long` | 할인 금액 |
| `payableAmount` | `Long` | 실제 결제 금액 |
| `items` | `Array` | 주문 아이템 목록 |
| `items[].itemId` | `Long` | 아이템 ID |
| `items[].skuId` | `String` | SKU 식별자 |
| `items[].quantity` | `Int` | 수량 |
| `items[].unitPrice` | `Long` | 단가 (원) |
| `items[].status` | `String` | 아이템 상태 |
| `createdAt` | `String` | 주문 생성 시각 (ISO 8601) |
| `updatedAt` | `String` | 주문 수정 시각 (ISO 8601) |

---

## POST /api/orders/{orderId}/refund

확정된 주문의 특정 아이템을 환불합니다. 전체 아이템을 환불하면 주문 상태가 `REFUNDED`로, 일부만 환불하면 `PARTIALLY_REFUNDED`로 전이됩니다.

### 요청

```http
POST /api/orders/{orderId}/refund
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "itemIds": [1, 2]
}
```

| 경로 변수 | 타입 | 설명 |
|----------|------|------|
| `orderId` | `Long` | 환불할 주문 ID |

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `itemIds` | `Array<Long>` | O | 환불할 아이템 ID 목록 |

### 응답

```json
{
  "success": true,
  "data": {
    "orderId": 123,
    "refundedAmount": 50000,
    "status": "REFUNDED"
  }
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `orderId` | `Long` | 주문 ID |
| `refundedAmount` | `Long` | 환불된 금액 |
| `status` | `String` | 환불 후 주문 상태 (`PARTIALLY_REFUNDED` 또는 `REFUNDED`) |
