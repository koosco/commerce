package com.koosco.inventoryservice.contract.outbound.inventory

import com.koosco.common.core.event.IntegrationEvent
import com.koosco.inventoryservice.domain.enums.StockReservationFailReason

data class StockReservedEvent(
    val orderId: Long,
    val items: List<Item>,

    val correlationId: String,
    val causationId: String?,
) : IntegrationEvent {
    override val aggregateId: String get() = orderId.toString()

    override fun getEventType(): String = "stock.reserved"

    override fun getSubject(): String = "inventory/$orderId"

    data class Item(val skuId: String, val quantity: Int)
}

/**
 * 재고 예약 실패
 * OrderPlaced → Inventory.reserve 실패 시 발행
 */
data class StockReservationFailedEvent(
    val orderId: Long,
    val reason: StockReservationFailReason?,
    val failedItems: List<FailedItem>?,

    val correlationId: String,
    val causationId: String?,
) : IntegrationEvent {
    override val aggregateId: String get() = orderId.toString()

    override fun getEventType(): String = "stock.reservation.failed"

    override fun getSubject(): String = "inventory/$orderId"

    data class FailedItem(val skuId: String, val requestedQuantity: Int, val availableQuantity: Int? = null)
}
