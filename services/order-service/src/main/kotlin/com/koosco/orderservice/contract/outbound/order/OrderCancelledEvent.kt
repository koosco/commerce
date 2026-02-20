package com.koosco.orderservice.contract.outbound.order

import com.koosco.orderservice.contract.OrderIntegrationEvent
import com.koosco.orderservice.domain.enums.OrderCancelReason

data class OrderCancelledEvent(
    override val orderId: Long,
    val reason: OrderCancelReason,
    val items: List<CancelledItem>,
    val correlationId: String,
    val causationId: String? = null,
) : OrderIntegrationEvent {
    data class CancelledItem(val skuId: Long, val quantity: Int)

    override fun getEventType(): String = "order.cancelled"
    override fun getPartitionKey(): String = orderId.toString()
}
