package com.koosco.inventoryservice.domain.entity

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
    name = "inventory_event_idempotency",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uq_inventory_idempotency",
            columnNames = ["message_id", "action"],
        ),
    ],
    indexes = [
        Index(name = "idx_inventory_idempotency_aggregate", columnList = "aggregate_type, aggregate_id"),
        Index(name = "idx_inventory_idempotency_processed", columnList = "processed_at"),
    ],
)
class InventoryEventIdempotency(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "message_id", nullable = false)
    val messageId: String,

    @Column(name = "action", nullable = false, length = 100)
    val action: String,

    @Column(name = "aggregate_id", nullable = false)
    val aggregateId: String,

    @Column(name = "aggregate_type", nullable = false, length = 100)
    val aggregateType: String = "Inventory",

    @Column(name = "processed_at", nullable = false, updatable = false)
    val processedAt: Instant = Instant.now(),
) {
    companion object {
        object Actions {
            const val RESERVE_STOCK = "RESERVE_STOCK"
            const val CONFIRM_STOCK = "CONFIRM_STOCK"
            const val RELEASE_STOCK = "RELEASE_STOCK"
            const val INITIALIZE_STOCK = "INITIALIZE_STOCK"
        }

        fun create(messageId: String, action: String, aggregateId: String): InventoryEventIdempotency =
            InventoryEventIdempotency(
                messageId = messageId,
                action = action,
                aggregateId = aggregateId,
            )
    }
}
