package com.koosco.orderservice.order.domain.event

import com.koosco.common.core.event.AbstractDomainEvent
import com.koosco.orderservice.order.domain.Item

/**
 * 주문 아이템이 환불되었음을 나타내는 도메인 이벤트
 */
data class OrderItemsRefunded(val orderId: Long, val refundedAmount: Long, val refundedItems: List<Item>) :
    AbstractDomainEvent() {
    override fun getEventType(): String = "com.koosco.order.items.refunded"
    override fun getAggregateId(): String = orderId.toString()
}
