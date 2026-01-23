package com.koosco.orderservice.order.domain.event

import com.koosco.common.core.event.AbstractDomainEvent
import com.koosco.orderservice.order.domain.Item

/**
 * 주문이 결제 완료되었음을 나타내는 도메인 이벤트
 */
data class OrderPaid(val orderId: Long, val paidAmount: Long, val items: List<Item>) : AbstractDomainEvent() {
    override fun getEventType(): String = "OrderPaid"

    override fun getAggregateId(): String = orderId.toString()
}
