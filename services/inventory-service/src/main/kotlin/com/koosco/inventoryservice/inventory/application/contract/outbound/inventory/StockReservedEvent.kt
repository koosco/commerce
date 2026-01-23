package com.koosco.inventoryservice.inventory.application.contract.outbound.inventory

import com.koosco.inventoryservice.inventory.application.contract.InventoryIntegrationEvent
import com.koosco.inventoryservice.inventory.domain.enums.StockReservationFailReason

/**
 * fileName       : StockReservedEvent
 * author         : koo
 * date           : 2025. 12. 24. 오전 3:53
 * description    :
 */
data class StockReservedEvent(
    override val orderId: Long,
    val items: List<Item>,

    val correlationId: String,
    val causationId: String?,
) : InventoryIntegrationEvent {
    override fun getEventType(): String = "stock.reserved"

    data class Item(val skuId: String, val quantity: Int)
}

/**
 * 재고 예약 실패
 * OrderPlaced → Inventory.reserve 실패 시 발행
 */
data class StockReservationFailedEvent(
    override val orderId: Long,
    val reason: StockReservationFailReason?,
    val failedItems: List<FailedItem>?,

    val correlationId: String,
    val causationId: String?,
) : InventoryIntegrationEvent {
    override fun getEventType(): String = "stock.reservation.failed"

    data class FailedItem(val skuId: String, val requestedQuantity: Int, val availableQuantity: Int? = null)
}
