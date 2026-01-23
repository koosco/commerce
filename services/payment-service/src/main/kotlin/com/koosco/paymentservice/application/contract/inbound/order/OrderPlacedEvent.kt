package com.koosco.paymentservice.application.contract.inbound.order

/**
 * fileName       : OrderPlacedEvent
 * author         : koo
 * date           : 2025. 12. 24. 오전 6:01
 * description    :
 */
data class OrderPlacedEvent(
    val orderId: Long,
    val userId: Long,
    val payableAmount: Long,
    val items: List<PlacedItem>,

    val correlationId: String,
    val causationId: String? = null,
) {
    data class PlacedItem(val skuId: String, val quantity: Int, val unitPrice: Long)
}
