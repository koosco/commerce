package com.koosco.inventoryservice.application.result

import com.koosco.inventoryservice.domain.enums.InventoryAction
import java.time.LocalDateTime

data class GetInventoryLogsResult(
    val id: Long,
    val skuId: String,
    val orderId: Long?,
    val action: InventoryAction,
    val quantity: Int,
    val createdAt: LocalDateTime,
)
