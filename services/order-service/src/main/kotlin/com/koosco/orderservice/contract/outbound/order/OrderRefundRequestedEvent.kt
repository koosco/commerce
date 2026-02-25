package com.koosco.orderservice.contract.outbound.order

import com.koosco.orderservice.contract.OrderIntegrationEvent

data class OrderRefundRequestedEvent(
    override val orderId: Long,
    val userId: Long,
    val refundAmount: Long,
    val refundedItemIds: List<Long>,
    val correlationId: String,
    val causationId: String? = null,
) : OrderIntegrationEvent {
    override fun getEventType(): String = "order.refund.requested"
}
