package com.koosco.orderservice.contract.outbound.order

import com.koosco.common.core.event.IntegrationEvent
import com.koosco.orderservice.domain.enums.OrderCancelReason

data class OrderCancelledEvent(
    val orderId: Long,
    val reason: OrderCancelReason,
    val items: List<CancelledItem>,
    val correlationId: String,
    val causationId: String? = null,
) : IntegrationEvent {
    data class CancelledItem(val skuId: Long, val quantity: Int)

    override val aggregateId: String get() = orderId.toString()

    override fun getEventType(): String = "order.cancelled"

    override fun getPartitionKey(): String = orderId.toString()

    override fun getSubject(): String = "order/$orderId"
}
