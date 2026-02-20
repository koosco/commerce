package com.koosco.orderservice.api

import com.koosco.orderservice.application.command.CreateOrderCommand
import com.koosco.orderservice.domain.vo.Money
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class CreateOrderRequest(

    val idempotencyKey: String? = null,

    @field:NotEmpty
    @field:Valid
    val items: List<OrderItemRequest>,

    @field:NotNull
    @field:Min(0)
    val discountAmount: Long = 0L,

    @field:NotNull
    @field:Min(0)
    val shippingFee: Long = 0L,

    @field:NotNull
    @field:Valid
    val shippingAddress: ShippingAddressRequest,
) {
    fun toCommand(userId: Long): CreateOrderCommand = CreateOrderCommand(
        userId = userId,
        idempotencyKey = idempotencyKey,
        items = items.map { it.toCommand() },
        discountAmount = Money(discountAmount),
        shippingFee = Money(shippingFee),
        shippingAddress = shippingAddress.toCommand(),
    )
}

data class ShippingAddressRequest(
    @field:NotNull
    val recipient: String,

    @field:NotNull
    val phone: String,

    @field:NotNull
    val zipCode: String,

    @field:NotNull
    val address: String,

    @field:NotNull
    val addressDetail: String,
) {
    fun toCommand(): CreateOrderCommand.ShippingAddressCommand = CreateOrderCommand.ShippingAddressCommand(
        recipient = recipient,
        phone = phone,
        zipCode = zipCode,
        address = address,
        addressDetail = addressDetail,
    )
}

data class OrderItemRequest(
    @field:NotNull
    val skuId: Long,

    @field:NotNull
    @field:Positive
    val productId: Long,

    @field:NotNull
    @field:Positive
    val brandId: Long,

    @field:NotNull
    val titleSnapshot: String,

    val optionSnapshot: String? = null,

    @field:NotNull
    @field:Positive
    val quantity: Int,

    @field:NotNull
    @field:Positive
    val unitPrice: Long,
) {
    fun toCommand(): CreateOrderCommand.OrderItemCommand = CreateOrderCommand.OrderItemCommand(
        skuId = skuId,
        productId = productId,
        brandId = brandId,
        titleSnapshot = titleSnapshot,
        optionSnapshot = optionSnapshot,
        quantity = quantity,
        unitPrice = Money(unitPrice),
    )
}
