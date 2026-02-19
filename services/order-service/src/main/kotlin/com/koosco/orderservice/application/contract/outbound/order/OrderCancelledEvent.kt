package com.koosco.orderservice.application.contract.outbound.order

import com.koosco.orderservice.application.contract.OrderIntegrationEvent
import com.koosco.orderservice.domain.enums.OrderCancelReason

/**
 * fileName       : OrderCancelledEvent
 * author         : koo
 * date           : 2025. 12. 24. 오전 3:34
 * description    :
 */
data class OrderCancelledEvent(
    override val orderId: Long,
    val reason: OrderCancelReason,
    val items: List<CancelledItem>,
    val correlationId: String,
    val causationId: String? = null,
) : OrderIntegrationEvent {
    data class CancelledItem(val skuId: String, val quantity: Int)

    override fun getEventType(): String = "order.cancelled"
    override fun getPartitionKey(): String = orderId.toString()
}
