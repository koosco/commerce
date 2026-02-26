package com.koosco.catalogservice.contract.inbound.inventory

/**
 * inventory-service에서 발행하는 재고 소진 이벤트
 * 재고 확정 후 가용 재고가 0이 되었을 때 수신
 */
data class StockDepletedEvent(
    val orderId: Long,
    val skuId: String,
    val correlationId: String,
    val causationId: String?,
)

/**
 * inventory-service에서 발행하는 재고 복구 이벤트
 * 주문 취소로 가용 재고가 0에서 복구되었을 때 수신
 */
data class StockRestoredEvent(
    val orderId: Long,
    val skuId: String,
    val correlationId: String,
    val causationId: String?,
)
