package com.koosco.orderservice.domain.entity

import com.koosco.common.core.exception.NotFoundException
import com.koosco.orderservice.common.error.CartErrorCode
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "cart_cart")
class Cart(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val userId: Long,

    @OneToMany(mappedBy = "cart", cascade = [CascadeType.ALL], orphanRemoval = true)
    val items: MutableList<CartItem> = mutableListOf(),

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {

    fun addItem(skuId: Long, qty: Int): CartItem {
        require(qty > 0) { "수량은 1 이상이어야 합니다." }

        val existing = items.find { it.skuId == skuId }
        if (existing != null) {
            existing.qty += qty
            existing.updatedAt = LocalDateTime.now()
            updatedAt = LocalDateTime.now()
            return existing
        }

        val item = CartItem(cart = this, skuId = skuId, qty = qty)
        items.add(item)
        updatedAt = LocalDateTime.now()
        return item
    }

    fun updateItemQuantity(cartItemId: Long, qty: Int): CartItem {
        require(qty > 0) { "수량은 1 이상이어야 합니다." }

        val item = items.find { it.id == cartItemId }
            ?: throw NotFoundException(CartErrorCode.CART_ITEM_NOT_FOUND)
        item.qty = qty
        item.updatedAt = LocalDateTime.now()
        updatedAt = LocalDateTime.now()
        return item
    }

    fun removeItem(cartItemId: Long) {
        val item = items.find { it.id == cartItemId }
            ?: throw NotFoundException(CartErrorCode.CART_ITEM_NOT_FOUND)
        items.remove(item)
        updatedAt = LocalDateTime.now()
    }

    fun clear() {
        items.clear()
        updatedAt = LocalDateTime.now()
    }

    companion object {
        fun create(userId: Long): Cart = Cart(userId = userId)
    }
}
