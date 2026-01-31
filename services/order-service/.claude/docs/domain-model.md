# Order Service 도메인 모델

## Order (주문 애그리거트)

```kotlin
class Order(
    val id: Long?,
    val userId: Long,
    var status: OrderStatus,
    val totalAmount: Money,      // 주문 원금
    val discountAmount: Money,   // 할인 금액
    val payableAmount: Money,    // 실제 결제 금액
    val items: MutableList<OrderItem>,
)
```

## OrderItem (주문 아이템)

```kotlin
class OrderItem(
    val id: Long?,
    val order: Order,
    val skuId: String,
    val quantity: Int,
    val unitPrice: Money,
    var status: OrderItemStatus,
)
```

## OrderStatus (주문 상태)

```kotlin
enum class OrderStatus {
    INIT, CREATED, RESERVED, PAYMENT_CREATED, PAYMENT_PENDING,
    PAID, CONFIRMED, PARTIALLY_REFUNDED, REFUNDED, CANCELLED, FAILED
}
```
