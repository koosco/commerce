package com.koosco.orderservice.contract.outbound.order

import com.koosco.orderservice.contract.OrderIntegrationEvent

/**
 * fileName       : OrderContract
 * author         : koo
 * date           : 2025. 12. 23. 오전 2:50
 * description    :
 */

/**
 * 주문 생성 성공
 * [
 *      payment service : 결제 초기화
 *      inventory service : 재고 예약
 * ]
 */
data class OrderPlacedEvent(
    override val orderId: Long,
    val userId: Long,
    val payableAmount: Long,
    val items: List<PlacedItem>,

    val correlationId: String,
    val causationId: String? = null,
) : OrderIntegrationEvent {
    data class PlacedItem(val skuId: String, val quantity: Int, val unitPrice: Long)

    override fun getEventType(): String = "order.placed"
}
