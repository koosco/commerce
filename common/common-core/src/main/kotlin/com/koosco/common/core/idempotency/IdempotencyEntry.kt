package com.koosco.common.core.idempotency

import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import java.time.Instant

/**
 * Abstract base class for event idempotency tracking.
 *
 * Ensures that the same event is not processed multiple times.
 * Each consumer should create a concrete entity extending this class
 * with a unique constraint on (eventId, action).
 *
 * ## Usage
 * 1. Before processing an event, check if an entry exists for the event ID
 * 2. If exists, skip processing (idempotent - already processed)
 * 3. If not exists, process the event and save the idempotency entry
 *    in the same transaction as the business logic
 *
 * ## Implementation
 * Each service should create a concrete entity extending this class:
 * ```kotlin
 * @Entity
 * @Table(
 *     name = "order_event_idempotency",
 *     uniqueConstraints = [
 *         UniqueConstraint(name = "uq_order_idempotency", columnNames = ["event_id", "action"])
 *     ]
 * )
 * class OrderEventIdempotency(
 *     eventId: String,
 *     action: String,
 *     val orderId: Long,
 * ) : IdempotencyEntry(eventId = eventId, action = action)
 * ```
 */
@MappedSuperclass
abstract class IdempotencyEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long = 0,

    /**
     * The unique identifier of the event (CloudEvent.id).
     * Combined with [action] to form a unique constraint.
     */
    @Column(name = "event_id", nullable = false)
    open val eventId: String,

    /**
     * The action/handler that processed this event.
     * Allows the same event to be processed by different handlers.
     * Example: "MARK_ORDER_PAID", "RESERVE_STOCK", "CREATE_PAYMENT"
     */
    @Column(name = "action", nullable = false, length = 100)
    open val action: String,

    /**
     * The timestamp when this event was processed.
     */
    @Column(name = "processed_at", nullable = false, updatable = false)
    open val processedAt: Instant = Instant.now(),
)
