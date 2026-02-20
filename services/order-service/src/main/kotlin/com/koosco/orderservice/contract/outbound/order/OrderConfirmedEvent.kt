package com.koosco.orderservice.contract.outbound.order

import com.koosco.orderservice.contract.OrderIntegrationEvent

class OrderConfirmedEvent(
    override val orderId: Long,
    val items: List<ConfirmedItem>,
    val correlationId: String,
    val causationId: String?,
) : OrderIntegrationEvent {
    data class ConfirmedItem(val skuId: Long, val quantity: Int)

    override fun getEventType(): String = "order.confirmed"
}
