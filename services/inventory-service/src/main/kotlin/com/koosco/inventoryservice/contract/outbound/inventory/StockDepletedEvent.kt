package com.koosco.inventoryservice.contract.outbound.inventory

import com.koosco.inventoryservice.contract.InventoryIntegrationEvent

/**
 * 재고 소진 이벤트
 * 재고 확정(confirm) 후 가용 재고가 0이 되었을 때 발행
 * catalog-service가 소비하여 상품 상태를 OUT_OF_STOCK으로 변경
 */
data class StockDepletedEvent(
    override val orderId: Long,
    val skuId: String,
    val correlationId: String,
    val causationId: String?,
) : InventoryIntegrationEvent {
    override fun getEventType(): String = "stock.depleted"

    override fun getPartitionKey(): String = skuId

    override fun getSubject(): String = "inventory/$skuId"
}

/**
 * 재고 복구 이벤트
 * 재고 취소(cancel) 후 가용 재고가 0에서 복구되었을 때 발행
 * catalog-service가 소비하여 상품 상태를 ACTIVE로 변경
 */
data class StockRestoredEvent(
    override val orderId: Long,
    val skuId: String,
    val correlationId: String,
    val causationId: String?,
) : InventoryIntegrationEvent {
    override fun getEventType(): String = "stock.restored"

    override fun getPartitionKey(): String = skuId

    override fun getSubject(): String = "inventory/$skuId"
}
