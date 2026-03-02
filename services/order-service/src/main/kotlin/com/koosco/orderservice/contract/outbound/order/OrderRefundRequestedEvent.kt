package com.koosco.orderservice.contract.outbound.order

import com.koosco.common.core.event.IntegrationEvent

data class OrderRefundRequestedEvent(
    val orderId: Long,
    val userId: Long,
    val refundAmount: Long,
    val refundedItemIds: List<Long>,
    val correlationId: String,
    val causationId: String? = null,
) : IntegrationEvent {
    override val aggregateId: String get() = orderId.toString()

    override fun getEventType(): String = "order.refund.requested"

    override fun getSubject(): String = "order/$orderId"
}
