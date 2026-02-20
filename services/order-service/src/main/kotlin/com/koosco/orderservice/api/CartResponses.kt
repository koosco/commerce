package com.koosco.orderservice.api

import com.koosco.orderservice.application.result.CartItemResult
import com.koosco.orderservice.application.result.CartResult

data class CartResponse(val cartId: Long, val items: List<CartItemResponse>) {
    companion object {
        fun from(result: CartResult): CartResponse = CartResponse(
            cartId = result.cartId,
            items = result.items.map { CartItemResponse.from(it) },
        )
    }
}

data class CartItemResponse(val cartItemId: Long, val skuId: Long, val qty: Int) {
    companion object {
        fun from(result: CartItemResult): CartItemResponse = CartItemResponse(
            cartItemId = result.cartItemId,
            skuId = result.skuId,
            qty = result.qty,
        )
    }
}
