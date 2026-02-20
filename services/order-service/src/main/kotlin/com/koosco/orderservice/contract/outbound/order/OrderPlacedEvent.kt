package com.koosco.orderservice.contract.outbound.order

import com.koosco.orderservice.contract.OrderIntegrationEvent

data class OrderPlacedEvent(
    override val orderId: Long,
    val userId: Long,
    val payableAmount: Long,
    val items: List<PlacedItem>,

    val correlationId: String,
    val causationId: String? = null,
) : OrderIntegrationEvent {
    data class PlacedItem(val skuId: Long, val quantity: Int, val unitPrice: Long)

    override fun getEventType(): String = "order.placed"
}
