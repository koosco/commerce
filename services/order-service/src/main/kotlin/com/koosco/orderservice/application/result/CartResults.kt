package com.koosco.orderservice.application.result

import com.koosco.orderservice.domain.entity.Cart
import com.koosco.orderservice.domain.entity.CartItem

data class CartResult(val cartId: Long, val items: List<CartItemResult>) {
    companion object {
        fun from(cart: Cart): CartResult = CartResult(
            cartId = cart.id!!,
            items = cart.items.map { CartItemResult.from(it) },
        )
    }
}

data class CartItemResult(val cartItemId: Long, val skuId: Long, val qty: Int) {
    companion object {
        fun from(item: CartItem): CartItemResult = CartItemResult(
            cartItemId = item.id!!,
            skuId = item.skuId,
            qty = item.qty,
        )
    }
}
