package com.koosco.orderservice.contract.outbound.order

import com.koosco.common.core.event.IntegrationEvent

class OrderConfirmedEvent(
    val orderId: Long,
    val items: List<ConfirmedItem>,
    val correlationId: String,
    val causationId: String?,
) : IntegrationEvent {
    data class ConfirmedItem(val skuId: Long, val quantity: Int)

    override val aggregateId: String get() = orderId.toString()

    override fun getEventType(): String = "order.confirmed"

    override fun getSubject(): String = "order/$orderId"
}
