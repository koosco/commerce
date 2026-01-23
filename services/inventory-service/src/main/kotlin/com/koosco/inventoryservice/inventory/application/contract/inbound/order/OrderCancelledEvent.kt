package com.koosco.inventoryservice.inventory.application.contract.inbound.order

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

/**
 * fileName       : StockReleaseRequestedEvent
 * author         : koo
 * date           : 2025. 12. 24. 오전 2:41
 * description    :
 */
data class OrderCancelledEvent(
    @field:NotNull
    val orderId: Long,
    val reason: String? = null,
    @field:NotEmpty
    val items: List<CancelledItem>,

    val correlationId: String,
    val causationId: String?,
) {
    data class CancelledItem(
        @field:NotNull
        val skuId: String,

        @field:Positive
        val quantity: Int,
    )
}
