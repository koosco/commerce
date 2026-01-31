# Payment Service 도메인 모델

## Payment (Aggregate Root)

```kotlin
class Payment(
    val paymentId: UUID,
    val orderId: Long,
    val userId: Long,
    val amount: Money
) {
    var status: PaymentStatus = PaymentStatus.READY

    fun approve(transaction: PaymentTransaction)
    fun fail(transaction: PaymentTransaction)
    fun cancel(transaction: PaymentTransaction)
}
```

## PaymentStatus

- `READY`: 결제 준비 완료
- `APPROVED`: 승인 완료
- `FAILED`: 승인 실패
- `CANCELED`: 결제 취소
