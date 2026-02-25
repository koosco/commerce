package com.koosco.orderservice.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.Instant

@Entity
@Table(
    name = "cart_idempotency",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uq_cart_idempotency_user_key",
            columnNames = ["user_id", "idempotency_key"],
        ),
    ],
    indexes = [
        Index(name = "idx_cart_idempotency_item", columnList = "cart_item_id"),
    ],
)
class CartIdempotency(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "user_id", nullable = false)
    val userId: Long,
    @Column(name = "idempotency_key", nullable = false, length = 100)
    val idempotencyKey: String,
    @Column(name = "cart_item_id", nullable = false)
    val cartItemId: Long,
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
) {
    companion object {
        fun create(userId: Long, idempotencyKey: String, cartItemId: Long): CartIdempotency = CartIdempotency(
            userId = userId,
            idempotencyKey = idempotencyKey,
            cartItemId = cartItemId,
        )
    }
}
