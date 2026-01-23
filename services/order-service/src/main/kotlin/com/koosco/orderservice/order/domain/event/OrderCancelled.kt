package com.koosco.orderservice.order.domain.event

import com.koosco.common.core.event.AbstractDomainEvent
import com.koosco.orderservice.order.domain.Item
import com.koosco.orderservice.order.domain.enums.OrderCancelReason

/**
 * fileName       : OrderCancelled
 * author         : koo
 * date           : 2025. 12. 24. 오전 3:49
 * description    :
 */
data class OrderCancelled(val orderId: Long, val reason: OrderCancelReason, val items: List<Item>) :
    AbstractDomainEvent() {

    override fun getEventType(): String = "OrderCancelled"

    override fun getAggregateId(): String = orderId.toString()
}
