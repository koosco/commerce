package com.koosco.inventoryservice.application.contract.inbound.order

import jakarta.validation.constraints.NotNull

/**
 * fileName       : StockConfirmedEvent
 * author         : koo
 * date           : 2025. 12. 24. 오전 2:41
 * description    :
 */
data class OrderConfirmedEvent(
    @field:NotNull
    val orderId: Long,
    val items: List<ConfirmedItem>,

    val correlationId: String,
    val causationId: String?,
) {
    data class ConfirmedItem(val skuId: String, val quantity: Int)
}
