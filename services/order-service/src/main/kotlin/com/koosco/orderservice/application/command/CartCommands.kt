package com.koosco.orderservice.application.command

data class AddCartItemCommand(val userId: Long, val skuId: Long, val qty: Int)

data class UpdateCartItemCommand(val userId: Long, val cartItemId: Long, val qty: Int)

data class RemoveCartItemCommand(val userId: Long, val cartItemId: Long)

data class ClearCartCommand(val userId: Long)

data class GetCartCommand(val userId: Long)
