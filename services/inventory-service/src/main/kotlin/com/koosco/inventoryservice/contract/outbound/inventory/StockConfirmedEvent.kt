package com.koosco.inventoryservice.contract.outbound.inventory

import com.koosco.inventoryservice.contract.InventoryIntegrationEvent
import com.koosco.inventoryservice.domain.enums.StockConfirmFailReason

/**
 * fileName       : StockConfirmedEvent
 * author         : koo
 * date           : 2025. 12. 24. 오전 2:32
 * description    :
 */
/**
 * 재고 확정 성공
 * OrderCompleted → Inventory.confirm 성공 시 발행
 */
data class StockConfirmedEvent(
    override val orderId: Long,
    val items: List<ConfirmedItem>,

    val correlationId: String,
    val causationId: String?,
) : InventoryIntegrationEvent {
    override fun getEventType(): String = "stock.confirmed"

    data class ConfirmedItem(val skuId: String, val quantity: Int)
}

/**
 * 재고 확정 실패
 * OrderCompleted → Inventory.confirm 실패 시 발행
 */
data class StockConfirmFailedEvent(
    override val orderId: Long,
    val reason: StockConfirmFailReason?,

    val correlationId: String,
    val causationId: String? = null,
) : InventoryIntegrationEvent {
    override fun getEventType(): String = "stock.confirm.failed"
}
