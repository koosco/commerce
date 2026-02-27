package com.koosco.catalogservice.contract.inbound.order

/**
 * order-service에서 발행하는 주문 확정 이벤트
 * 결제 완료 후 재고 확정이 성공하면 수신
 */
data class OrderConfirmedEvent(
    val orderId: Long,
    val items: List<ConfirmedItem>,
    val correlationId: String,
    val causationId: String?,
) {
    data class ConfirmedItem(val skuId: Long, val quantity: Int)
}

/**
 * order-service에서 발행하는 주문 취소 이벤트
 * 주문이 취소되었을 때 수신
 */
data class OrderCancelledEvent(
    val orderId: Long,
    val items: List<CancelledItem>,
    val correlationId: String,
    val causationId: String?,
) {
    data class CancelledItem(val skuId: Long, val quantity: Int)
}
