package com.koosco.orderservice.order.api

import com.koosco.orderservice.order.application.command.CreateOrderCommand
import com.koosco.orderservice.order.application.command.RefundOrderItemsCommand
import com.koosco.orderservice.order.domain.vo.Money
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class CreateOrderRequest(

    @field:NotEmpty
    @field:Valid
    val items: List<OrderItemRequest>,

    @field:NotNull
    @field:Min(0)
    val discountAmount: Long = 0L,
) {
    fun toCommand(userId: Long): CreateOrderCommand = CreateOrderCommand(
        userId = userId,
        items = items.map { it.toCommand() },
        discountAmount = Money(discountAmount),
    )
}

data class OrderItemRequest(
    @field:NotNull
    val skuId: String,

    @field:NotNull
    @field:Positive
    val quantity: Int,

    @field:NotNull
    @field:Positive
    val unitPrice: Long,
) {
    fun toCommand(): CreateOrderCommand.OrderItemCommand = CreateOrderCommand.OrderItemCommand(
        skuId = skuId,
        quantity = quantity,
        unitPrice = Money(unitPrice),
    )
}

data class RefundOrderItemsRequest(
    @field:NotEmpty
    val itemIds: List<Long>,
) {
    fun toCommand(orderId: Long): RefundOrderItemsCommand = RefundOrderItemsCommand(
        orderId = orderId,
        refundItemIds = itemIds,
    )
}
