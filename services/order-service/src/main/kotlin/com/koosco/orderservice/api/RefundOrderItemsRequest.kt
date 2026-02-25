package com.koosco.orderservice.api

import com.koosco.orderservice.application.command.RefundOrderItemsCommand
import jakarta.validation.constraints.NotEmpty

data class RefundOrderItemsRequest(
    @field:NotEmpty
    val itemIds: List<Long>,
) {
    fun toCommand(orderId: Long, userId: Long): RefundOrderItemsCommand = RefundOrderItemsCommand(
        orderId = orderId,
        userId = userId,
        itemIds = itemIds,
    )
}
