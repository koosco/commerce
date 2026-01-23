package com.koosco.inventoryservice.inventory.application.contract.inbound.order

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

/**
 * fileName       : OrderPlacedEvent
 * author         : koo
 * date           : 2025. 12. 24. 오전 2:33
 * description    :
 */
/**
 * 주문 상품 정보
 */
data class OrderPlacedEvent(
    @field:NotNull
    val orderId: Long,
    val userId: Long,
    val payableAmount: Long,
    @field:NotEmpty
    val items: List<PlacedItem>,

    val correlationId: String,
    val causationId: String?,

) {
    data class PlacedItem(
        @field:NotNull
        val skuId: String,

        @field:Positive
        val quantity: Int,
    )
}
