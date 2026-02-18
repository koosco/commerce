package com.koosco.inventoryservice.inventory.application.port

import com.koosco.inventoryservice.inventory.domain.enums.InventoryAction
import java.time.LocalDateTime

interface InventoryLogPort {

    fun log(skuId: String, orderId: Long?, action: InventoryAction, quantity: Int)

    fun logBatch(entries: List<LogEntry>)

    fun findBySkuId(skuId: String, from: LocalDateTime?, to: LocalDateTime?): List<LogView>

    data class LogEntry(val skuId: String, val orderId: Long?, val action: InventoryAction, val quantity: Int)

    data class LogView(
        val id: Long,
        val skuId: String,
        val orderId: Long?,
        val action: InventoryAction,
        val quantity: Int,
        val createdAt: LocalDateTime,
    )
}
