package com.koosco.orderservice.application.command

/**
 * 재고 확정 이후 결제 대기 상태로 변경
 */
data class MarkOrderPaymentPendingCommand(val orderId: Long, val items: List<MarkedPaymentPendingItem> = emptyList()) {
    data class MarkedPaymentPendingItem(val skuId: Long, val quantity: Int)
}

data class MarkOrderConfirmedCommand(
    val orderId: Long,
    val reservationId: String? = null,
    val items: List<MarkedConfirmedItem> = emptyList(),
) {
    data class MarkedConfirmedItem(val skuId: Long, val quantity: Int)
}
