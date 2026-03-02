package com.koosco.inventoryservice.contract.outbound.inventory

import com.koosco.common.core.event.IntegrationEvent
import com.koosco.inventoryservice.domain.enums.StockConfirmFailReason

/**
 * 재고 확정 성공
 * OrderCompleted → Inventory.confirm 성공 시 발행
 */
data class StockConfirmedEvent(
    val orderId: Long,
    val items: List<ConfirmedItem>,

    val correlationId: String,
    val causationId: String?,
) : IntegrationEvent {
    override val aggregateId: String get() = orderId.toString()

    override fun getEventType(): String = "stock.confirmed"

    override fun getSubject(): String = "inventory/$orderId"

    data class ConfirmedItem(val skuId: String, val quantity: Int)
}

/**
 * 재고 확정 실패
 * OrderCompleted → Inventory.confirm 실패 시 발행
 */
data class StockConfirmFailedEvent(
    val orderId: Long,
    val reason: StockConfirmFailReason?,

    val correlationId: String,
    val causationId: String? = null,
) : IntegrationEvent {
    override val aggregateId: String get() = orderId.toString()

    override fun getEventType(): String = "stock.confirm.failed"

    override fun getSubject(): String = "inventory/$orderId"
}
