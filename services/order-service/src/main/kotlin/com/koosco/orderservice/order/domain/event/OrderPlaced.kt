package com.koosco.orderservice.order.domain.event

import com.koosco.common.core.event.AbstractDomainEvent
import com.koosco.orderservice.order.domain.Item

/**
 * 주문이 생성되었음을 나타내는 도메인 이벤트
 */
data class OrderPlaced(
    val orderId: Long,
    val userId: Long,
    val totalAmount: Long,
    val payableAmount: Long,
    val items: List<Item>,
) : AbstractDomainEvent() {
    override fun getEventType(): String = "OrderCreated"

    override fun getAggregateId(): String = orderId.toString()
}
