package com.koosco.orderservice.order.domain.entity

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
 * Tracks processed events for idempotency in order-service.
 *
 * Prevents duplicate processing of the same event.
 * The combination of eventId and action must be unique.
 *
 * Note: Does not extend common-core IdempotencyEntry due to QueryDSL limitations
 * with @MappedSuperclass across modules.
 */
@Entity
@Table(
    name = "order_event_idempotency",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uq_order_idempotency",
            columnNames = ["event_id", "action"],
        ),
    ],
    indexes = [
        Index(name = "idx_order_idempotency_order", columnList = "order_id"),
        Index(name = "idx_order_idempotency_processed", columnList = "processed_at"),
    ],
)
class OrderEventIdempotency(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "event_id", nullable = false)
    val eventId: String,

    @Column(name = "action", nullable = false, length = 100)
    val action: String,

    @Column(name = "processed_at", nullable = false, updatable = false)
    val processedAt: Instant = Instant.now(),

    @Column(name = "order_id", nullable = false)
    val orderId: Long,
) {
    companion object {
        /**
         * Action constants for idempotency tracking
         */
        object Actions {
            const val MARK_RESERVED = "MARK_RESERVED"
            const val MARK_PAYMENT_CREATED = "MARK_PAYMENT_CREATED"
            const val MARK_PAYMENT_PENDING = "MARK_PAYMENT_PENDING"
            const val MARK_PAID = "MARK_PAID"
            const val MARK_CONFIRMED = "MARK_CONFIRMED"
            const val CANCEL_BY_PAYMENT_FAILURE = "CANCEL_BY_PAYMENT_FAILURE"
        }

        fun create(eventId: String, action: String, orderId: Long): OrderEventIdempotency = OrderEventIdempotency(
            eventId = eventId,
            action = action,
            orderId = orderId,
        )
    }
}
