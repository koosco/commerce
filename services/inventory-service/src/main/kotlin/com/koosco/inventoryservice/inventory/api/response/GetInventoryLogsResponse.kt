package com.koosco.inventoryservice.inventory.api.response

import com.koosco.inventoryservice.inventory.domain.enums.InventoryAction
import java.time.LocalDateTime

data class GetInventoryLogsResponse(val logs: List<InventoryLogInfo>) {
    data class InventoryLogInfo(
        val id: Long,
        val skuId: String,
        val orderId: Long?,
        val action: InventoryAction,
        val quantity: Int,
        val createdAt: LocalDateTime,
    )
}
