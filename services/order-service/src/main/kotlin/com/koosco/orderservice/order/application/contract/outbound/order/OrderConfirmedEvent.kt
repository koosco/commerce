package com.koosco.orderservice.order.application.contract.outbound.order

import com.koosco.orderservice.order.application.contract.OrderIntegrationEvent

/**
 * fileName       : OrderConfirmedEvent
 * author         : koo
 * date           : 2025. 12. 24. 오전 3:33
 * description    :
 */
class OrderConfirmedEvent(
    override val orderId: Long,
    val items: List<ConfirmedItem>,
    val correlationId: String,
    val causationId: String?,
) : OrderIntegrationEvent {
    data class ConfirmedItem(val skuId: String, val quantity: Int)

    override fun getEventType(): String = "order.confirmed"
}
