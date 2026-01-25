package com.koosco.inventoryservice.inventory.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.Instant

/**
 * Tracks processed events for idempotency in inventory-service.
 *
 * Prevents duplicate processing of the same event.
 * The combination of eventId and action must be unique.
 *
 * Note: Does not extend common-core IdempotencyEntry due to QueryDSL limitations
 * with @MappedSuperclass across modules.
 */
@Entity
@Table(
    name = "inventory_event_idempotency",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uq_inventory_idempotency",
            columnNames = ["event_id", "action"],
        ),
    ],
    indexes = [
        Index(name = "idx_inventory_idempotency_reference", columnList = "reference_id"),
        Index(name = "idx_inventory_idempotency_processed", columnList = "processed_at"),
    ],
)
class InventoryEventIdempotency(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "event_id", nullable = false)
    val eventId: String,

    @Column(name = "action", nullable = false, length = 100)
    val action: String,

    @Column(name = "processed_at", nullable = false, updatable = false)
    val processedAt: Instant = Instant.now(),

    @Column(name = "reference_id", nullable = false)
    val referenceId: String,
) {
    companion object {
        /**
         * Action constants for idempotency tracking
         */
        object Actions {
            const val RESERVE_STOCK = "RESERVE_STOCK"
            const val CONFIRM_STOCK = "CONFIRM_STOCK"
            const val RELEASE_STOCK = "RELEASE_STOCK"
            const val INITIALIZE_STOCK = "INITIALIZE_STOCK"
        }

        fun create(eventId: String, action: String, referenceId: String): InventoryEventIdempotency =
            InventoryEventIdempotency(
                eventId = eventId,
                action = action,
                referenceId = referenceId,
            )
    }
}
