package com.koosco.inventoryservice.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.Instant

@Entity
@Table(
    name = "inventory_api_idempotency",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uq_inventory_api_idempotency",
            columnNames = ["idempotency_key", "operation_type"],
        ),
    ],
)
class InventoryApiIdempotency(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "idempotency_key", nullable = false, length = 100)
    val idempotencyKey: String,

    @Column(name = "operation_type", nullable = false, length = 30)
    val operationType: String,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
) {
    companion object {
        const val ADD_STOCK = "ADD_STOCK"
        const val DECREASE_STOCK = "DECREASE_STOCK"

        fun create(idempotencyKey: String, operationType: String) = InventoryApiIdempotency(
            idempotencyKey = idempotencyKey,
            operationType = operationType,
        )
    }
}
