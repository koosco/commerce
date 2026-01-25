# Event Contracts Documentation

> Last updated: 2025-01-25

이 문서는 프로젝트 전체에서 사용하는 이벤트 계약을 정의합니다.

## 1. CloudEvent Specification (v1.0)

프로젝트는 [CNCF CloudEvents v1.0 specification](https://github.com/cloudevents/spec/blob/v1.0.2/cloudevents/spec.md)을 따릅니다.

### 1.1 필수 필드 (Required)

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| `id` | String | 이벤트 고유 식별자 (UUID) | `"550e8400-e29b-41d4-a716-446655440000"` |
| `source` | String (URI) | 이벤트 발생 출처 | `"urn:koosco:order-service"` |
| `specversion` | String | CloudEvents 버전 | `"1.0"` |
| `type` | String | 이벤트 타입 | `"order.placed"` |

### 1.2 선택 필드 (Optional)

| Field | Type | Description | Default |
|-------|------|-------------|---------|
| `datacontenttype` | String | 데이터 MIME 타입 | `"application/json"` |
| `dataschema` | String (URI) | 데이터 스키마 위치 | `null` |
| `subject` | String | 이벤트 주체 | `"order/123"` |
| `time` | Instant (RFC 3339) | 이벤트 발생 시각 | 현재 시각 |
| `data` | Object | 이벤트 페이로드 | - |

### 1.3 확장 필드 (Extension)

모든 이벤트 데이터에 다음 필드를 포함합니다:

| Field | Type | Description | Usage |
|-------|------|-------------|-------|
| `correlationId` | String | 트랜잭션 추적 ID | 주문 ID 등 비즈니스 식별자 |
| `causationId` | String? | 원인 이벤트 ID | 직전 이벤트의 CloudEvent.id |

### 1.4 검증 규칙 (EventValidator)

`EventValidator.kt` 에서 다음 규칙을 검증합니다:

```kotlin
// Required field validation
- id: not blank
- source: not blank, valid URI-reference (RFC 3986)
- type: not blank
- specversion: must be "1.0"

// URI formats allowed for source:
- URN: "urn:namespace:specific-string" (예: "urn:koosco:order-service")
- HTTP/HTTPS: "https://api.koosco.com/orders"

// Content type validation (RFC 2046)
- datacontenttype: valid MIME type pattern
```

### 1.5 CloudEvent 생성 예시

```kotlin
// 방법 1: companion object 사용
val event = CloudEvent.of(
    source = "urn:koosco:order-service",
    type = "order.placed",
    data = orderPlacedEvent,
    subject = "order/123",
)

// 방법 2: IntegrationEvent 인터페이스
val cloudEvent = orderPlacedEvent.toCloudEvent("order-service")
```

---

## 2. Service Event Catalog

### 2.1 Event Summary

| Service | Publishes | Consumes | Idempotency |
|---------|-----------|----------|-------------|
| order-service | 3 | 7 | State transition |
| inventory-service | 4 | 4 | State transition |
| payment-service | 1 | 1 | DB idempotency (IdempotencyRepository) |
| catalog-service | 1 | 0 | N/A |

### 2.2 order-service

#### Published Events (Outbound)

| Event Type | Topic | Description | Partition Key |
|------------|-------|-------------|---------------|
| `order.placed` | `order.placed` | 주문 생성 완료 | orderId |
| `order.confirmed` | `order.confirmed` | 주문 확정 (결제+재고 완료) | orderId |
| `order.cancelled` | `order.cancelled` | 주문 취소 | orderId |

**OrderPlacedEvent**
```kotlin
data class OrderPlacedEvent(
    val orderId: Long,           // 주문 ID
    val userId: Long,            // 사용자 ID
    val payableAmount: Long,     // 결제 금액
    val items: List<PlacedItem>, // 주문 상품 목록
    val correlationId: String,   // = orderId.toString()
    val causationId: String?,    // UUID
) {
    data class PlacedItem(
        val skuId: String,
        val quantity: Int,
        val unitPrice: Long,
    )
}
```

**OrderConfirmedEvent**
```kotlin
data class OrderConfirmedEvent(
    val orderId: Long,
    val items: List<ConfirmedItem>,
    val correlationId: String,
    val causationId: String?,
) {
    data class ConfirmedItem(
        val skuId: String,
        val quantity: Int,
    )
}
```

**OrderCancelledEvent**
```kotlin
data class OrderCancelledEvent(
    val orderId: Long,
    val reason: OrderCancelReason,  // enum
    val items: List<CancelledItem>,
    val correlationId: String,
    val causationId: String?,
) {
    data class CancelledItem(
        val skuId: String,
        val quantity: Int,
    )
}
```

#### Consumed Events (Inbound)

| Event Type | Source | Description |
|------------|--------|-------------|
| `payment.created` | payment-service | 결제 초기화 완료 |
| `payment.completed` | payment-service | 결제 승인 완료 |
| `payment.failed` | payment-service | 결제 실패/취소 |
| `stock.reserved` | inventory-service | 재고 예약 성공 |
| `stock.reservation.failed` | inventory-service | 재고 예약 실패 |
| `stock.confirmed` | inventory-service | 재고 확정 성공 |
| `stock.confirm.failed` | inventory-service | 재고 확정 실패 |

### 2.3 inventory-service

#### Published Events (Outbound)

| Event Type | Topic | Description | Partition Key |
|------------|-------|-------------|---------------|
| `stock.reserved` | `stock.reserved` | 재고 예약 성공 | orderId |
| `stock.reservation.failed` | `stock.reservation.failed` | 재고 예약 실패 | orderId |
| `stock.confirmed` | `stock.confirmed` | 재고 확정 성공 | orderId |
| `stock.confirm.failed` | `stock.confirm.failed` | 재고 확정 실패 | orderId |

**StockReservedEvent**
```kotlin
data class StockReservedEvent(
    val orderId: Long,
    val items: List<Item>,
    val correlationId: String,
    val causationId: String?,
) {
    data class Item(
        val skuId: String,
        val quantity: Int,
    )
}
```

**StockReservationFailedEvent**
```kotlin
data class StockReservationFailedEvent(
    val orderId: Long,
    val reason: StockReservationFailReason?,  // enum
    val failedItems: List<FailedItem>?,
    val correlationId: String,
    val causationId: String?,
) {
    data class FailedItem(
        val skuId: String,
        val requestedQuantity: Int,
        val availableQuantity: Int?,
    )
}
```

**StockConfirmedEvent**
```kotlin
data class StockConfirmedEvent(
    val orderId: Long,
    val items: List<ConfirmedItem>,
    val correlationId: String,
    val causationId: String?,
) {
    data class ConfirmedItem(
        val skuId: String,
        val quantity: Int,
    )
}
```

**StockConfirmFailedEvent**
```kotlin
data class StockConfirmFailedEvent(
    val orderId: Long,
    val reason: StockConfirmFailReason?,  // enum
    val correlationId: String,
    val causationId: String?,
)
```

#### Consumed Events (Inbound)

| Event Type | Source | Description |
|------------|--------|-------------|
| `order.placed` | order-service | 주문 생성 → 재고 예약 |
| `order.confirmed` | order-service | 주문 확정 → 재고 확정 |
| `order.cancelled` | order-service | 주문 취소 → 재고 복구 |
| `product.sku.created` | catalog-service | SKU 생성 → 재고 초기화 |

### 2.4 payment-service

#### Published Events (Outbound)

| Event Type | Topic | Description | Partition Key |
|------------|-------|-------------|---------------|
| `payment.created` | `payment.created` | 결제 초기화 완료 | paymentId |

**PaymentCreatedEvent**
```kotlin
data class PaymentCreatedEvent(
    val paymentId: String,
    val orderId: Long,
)
```

> Note: `payment.completed`, `payment.failed` 이벤트는 Toss Payments 웹훅에서 발행됩니다.

#### Consumed Events (Inbound)

| Event Type | Source | Description |
|------------|--------|-------------|
| `order.placed` | order-service | 주문 생성 → 결제 초기화 |

### 2.5 catalog-service

#### Published Events (Outbound)

| Event Type | Topic | Description | Partition Key |
|------------|-------|-------------|---------------|
| `product.sku.created` | `product.sku.created` | SKU 생성 완료 | skuId |

**ProductSkuCreatedEvent**
```kotlin
data class ProductSkuCreatedEvent(
    val skuId: String,
    val productId: Long,
    val productCode: String,
    val price: Long,
    val optionValues: String,
    val initialQuantity: Int,
    val createdAt: LocalDateTime,
)
```

#### Consumed Events (Inbound)

catalog-service는 이벤트를 소비하지 않습니다 (Producer only).

---

## 3. Event Flow Diagrams

### 3.1 주문 생성 → 결제 → 재고 확정 Flow

```
┌─────────────────┐
│  order-service  │
│    (Client)     │
└────────┬────────┘
         │ POST /orders
         ▼
┌─────────────────┐     order.placed      ┌──────────────────┐
│  order-service  │────────────────────▶  │ inventory-service │
│                 │                       │                   │
│  Order: PENDING │                       │  Reserve Stock    │
└────────┬────────┘                       └─────────┬─────────┘
         │                                          │
         │     order.placed                         │ stock.reserved
         │                                          │ (or stock.reservation.failed)
         ▼                                          ▼
┌─────────────────┐                       ┌──────────────────┐
│ payment-service │◀──────────────────────│  order-service   │
│                 │                       │                  │
│ Initialize Pay  │                       │ Order: PENDING   │
└────────┬────────┘                       │ (waiting both)   │
         │                                └──────────────────┘
         │ payment.created
         ▼
┌─────────────────┐     (Toss Webhook)    ┌──────────────────┐
│  order-service  │◀──────────────────────│   Toss Payments  │
│                 │   payment.completed   │                  │
│ Order: PENDING  │   (or payment.failed) │                  │
└────────┬────────┘                       └──────────────────┘
         │
         │ Both events received? → Order: CONFIRMED (or CANCELLED)
         │
         │ order.confirmed
         ▼
┌─────────────────┐
│inventory-service│
│                 │
│  Confirm Stock  │
│                 │
└────────┬────────┘
         │ stock.confirmed
         ▼
┌─────────────────┐
│  order-service  │
│                 │
│ Order: COMPLETED│
└─────────────────┘
```

### 3.2 주문 취소 Flow

```
┌─────────────────┐
│  order-service  │
│                 │
│ Order:CANCELLED │
└────────┬────────┘
         │ order.cancelled
         ▼
┌─────────────────┐
│inventory-service│
│                 │
│  Release Stock  │
│  (Restore qty)  │
└─────────────────┘
```

---

## 4. Event Versioning Strategy

### 4.1 현재 네이밍 (Current)

```
{domain}.{action}

예시:
- order.placed
- order.confirmed
- stock.reserved
- payment.created
```

### 4.2 향후 버전 네이밍 (Proposed)

Breaking change 발생 시 다음 패턴 사용:

```
{domain}.{action}.v{version}

예시:
- order.placed.v2
- stock.reserved.v2
```

### 4.3 버전 전환 전략

1. **Dual-write**: 새 버전과 기존 버전 동시 발행
2. **Consumer migration**: 소비자 점진적 전환
3. **Deprecation**: 기존 버전 폐기 공지 (최소 2주)
4. **Cleanup**: 기존 버전 발행 중단

---

## 5. Best Practices

### 5.1 멱등성 처리 패턴

**State Transition 기반 (order-service, inventory-service)**
```kotlin
@Transactional
fun onPaymentCompleted(event: PaymentCompletedEvent) {
    val order = orderRepository.findById(event.orderId)
        ?: return  // Not found → skip

    if (order.status != OrderStatus.PENDING) {
        return  // Already processed → skip
    }

    order.markPaymentCompleted()
    orderRepository.save(order)
}
```

**DB Idempotency Key 기반 (payment-service)**
```kotlin
@Transactional
fun onOrderPlaced(event: OrderPlacedEvent, messageId: String) {
    if (idempotencyRepository.existsById(messageId)) {
        return  // Duplicate → skip
    }

    // Process event...

    idempotencyRepository.save(IdempotencyRecord(messageId))
}
```

### 5.2 correlationId / causationId 활용

```kotlin
// 1. 최초 이벤트 발행 (order-service)
val orderPlacedEvent = OrderPlacedEvent(
    orderId = savedOrder.id,
    correlationId = savedOrder.id.toString(),  // 비즈니스 ID
    causationId = UUID.randomUUID().toString(),
)

// 2. 연쇄 이벤트 발행 (inventory-service)
val stockReservedEvent = StockReservedEvent(
    orderId = event.orderId,
    correlationId = event.correlationId,  // 동일하게 유지
    causationId = cloudEvent.id,          // 직전 CloudEvent.id
)
```

### 5.3 Consumer 표준 패턴

```kotlin
@Component
@Validated
class KafkaOrderPlacedEventConsumer(
    private val useCase: ReserveStockUseCase,
) {
    @KafkaListener(
        topics = ["\${service.topic.mappings.order.placed}"],
        groupId = "\${spring.kafka.consumer.group-id}",
    )
    fun onOrderPlaced(
        @Valid event: CloudEvent<*>,
        ack: Acknowledgment,
    ) {
        // 1. Null check (poison message)
        val data = event.data ?: run {
            logger.warn("Null data, skipping: ${event.id}")
            ack.acknowledge()
            return
        }

        // 2. Deserialize
        val eventDto = try {
            objectMapper.convertValue(data, OrderPlacedEvent::class.java)
        } catch (e: Exception) {
            logger.error("Deserialization failed, skipping: ${event.id}", e)
            ack.acknowledge()
            return
        }

        // 3. Execute use case
        try {
            val context = MessageContext(
                correlationId = eventDto.correlationId,
                causationId = event.id,
            )
            useCase.execute(command, context)
            ack.acknowledge()
        } catch (e: BusinessException) {
            // Business error → ack (no retry)
            logger.warn("Business error: ${e.message}")
            ack.acknowledge()
        } catch (e: Exception) {
            // Infra error → throw (retry)
            logger.error("Infra error, will retry", e)
            throw e
        }
    }
}
```

### 5.4 Topic 설정 가이드

```yaml
# application.yml
service:
  topic:
    mappings:
      order:
        placed: order.placed
        confirmed: order.confirmed
        cancelled: order.cancelled
      stock:
        reserved: stock.reserved
        reservation-failed: stock.reservation.failed
        confirmed: stock.confirmed
        confirm-failed: stock.confirm.failed
      payment:
        created: payment.created
        completed: payment.completed
        failed: payment.failed
```

---

## 6. 파일 위치 참조

| Service | 이벤트 계약 위치 |
|---------|------------------|
| order-service | `services/order-service/.../application/contract/outbound/order/*.kt` |
| inventory-service | `services/inventory-service/.../application/contract/outbound/inventory/*.kt` |
| payment-service | `services/payment-service/.../application/contract/outbound/payment/*.kt` |
| catalog-service | `services/catalog-service/.../application/contract/outbound/*.kt` |

| Module | 공통 이벤트 구현 |
|--------|------------------|
| common-core | `common/common-core/src/main/kotlin/.../event/CloudEvent.kt` |
| common-core | `common/common-core/src/main/kotlin/.../event/EventValidator.kt` |
| common-core | `common/common-core/src/main/kotlin/.../event/DomainEvent.kt` |
