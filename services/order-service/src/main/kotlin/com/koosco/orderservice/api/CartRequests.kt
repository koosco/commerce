package com.koosco.orderservice.api

import com.koosco.orderservice.application.command.AddCartItemCommand
import com.koosco.orderservice.application.command.UpdateCartItemCommand
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class AddCartItemRequest(
    @field:NotNull
    @field:Positive
    val skuId: Long,

    @field:NotNull
    @field:Positive
    val qty: Int,
) {
    fun toCommand(userId: Long): AddCartItemCommand = AddCartItemCommand(
        userId = userId,
        skuId = skuId,
        qty = qty,
    )
}

data class UpdateCartItemRequest(
    @field:NotNull
    @field:Positive
    val qty: Int,
) {
    fun toCommand(userId: Long, cartItemId: Long): UpdateCartItemCommand = UpdateCartItemCommand(
        userId = userId,
        cartItemId = cartItemId,
        qty = qty,
    )
}
