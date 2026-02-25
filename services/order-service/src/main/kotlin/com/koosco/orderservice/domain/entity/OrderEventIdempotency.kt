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
    name = "order_event_idempotency",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uq_order_idempotency",
            columnNames = ["message_id", "action"],
        ),
    ],
    indexes = [
        Index(name = "idx_order_idempotency_aggregate", columnList = "aggregate_type, aggregate_id"),
        Index(name = "idx_order_idempotency_processed", columnList = "processed_at"),
    ],
)
class OrderEventIdempotency(
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
    val aggregateType: String = "Order",

    @Column(name = "processed_at", nullable = false, updatable = false)
    val processedAt: Instant = Instant.now(),
) {
    companion object {
        object Actions {
            const val MARK_RESERVED = "MARK_RESERVED"
            const val MARK_PAYMENT_CREATED = "MARK_PAYMENT_CREATED"
            const val MARK_PAYMENT_PENDING = "MARK_PAYMENT_PENDING"
            const val MARK_PAID = "MARK_PAID"
            const val MARK_CONFIRMED = "MARK_CONFIRMED"
            const val CANCEL_BY_PAYMENT_FAILURE = "CANCEL_BY_PAYMENT_FAILURE"
            const val MARK_FAILED_BY_STOCK_RESERVATION = "MARK_FAILED_BY_STOCK_RESERVATION"
            const val CANCEL_BY_STOCK_CONFIRM_FAILURE = "CANCEL_BY_STOCK_CONFIRM_FAILURE"
            const val MARK_REFUND_COMPLETED = "MARK_REFUND_COMPLETED"
        }

        fun create(messageId: String, action: String, aggregateId: String): OrderEventIdempotency =
            OrderEventIdempotency(
                messageId = messageId,
                action = action,
                aggregateId = aggregateId,
            )
    }
}
