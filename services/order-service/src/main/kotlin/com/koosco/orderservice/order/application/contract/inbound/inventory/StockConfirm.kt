package com.koosco.orderservice.order.application.contract.inbound.inventory

/**
 * fileName       : StockConfirm
 * author         : koo
 * date           : 2025. 12. 23. 오전 3:31
 * description    :
 */
// 재고 확정 성공
data class StockConfirmedEvent(
    val orderId: Long,
    val reservationId: String? = null,
    val items: List<ConfirmedItem>,

    val correlationId: String,
    val causationId: String?,
) {
    data class ConfirmedItem(val skuId: String, val quantity: Int)
}

// 재고 확정 실패
data class StockConfirmFailedEvent(
    val orderId: Long,
    val reservationId: String? = null,
    val reason: String, // e.g. RESERVATION_NOT_FOUND, NOT_ENOUGH_STOCK, INTERNAL_ERROR

    val correlationId: String,
    val causationId: String? = null,
)
