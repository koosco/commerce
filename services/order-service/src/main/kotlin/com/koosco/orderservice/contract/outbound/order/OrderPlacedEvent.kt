package com.koosco.orderservice.contract.outbound.order

import com.koosco.common.core.event.IntegrationEvent

data class OrderPlacedEvent(
    val orderId: Long,
    val userId: Long,
    val payableAmount: Long,
    val items: List<PlacedItem>,

    val correlationId: String,
    val causationId: String? = null,
) : IntegrationEvent {
    data class PlacedItem(val skuId: Long, val quantity: Int, val unitPrice: Long)

    override val aggregateId: String get() = orderId.toString()

    override fun getEventType(): String = "order.placed"

    override fun getSubject(): String = "order/$orderId"
}
