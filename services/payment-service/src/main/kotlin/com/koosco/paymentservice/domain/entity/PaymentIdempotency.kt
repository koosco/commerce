package com.koosco.paymentservice.domain.entity

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
    name = "payment_event_idempotency",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uq_payment_idempotency",
            columnNames = ["message_id", "action"],
        ),
    ],
    indexes = [
        Index(name = "idx_payment_idempotency_aggregate", columnList = "aggregate_type, aggregate_id"),
        Index(name = "idx_payment_idempotency_processed", columnList = "processed_at"),
    ],
)
class PaymentIdempotency(
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
    val aggregateType: String = "Payment",

    @Column(name = "processed_at", nullable = false, updatable = false)
    val processedAt: Instant = Instant.now(),
) {
    companion object {
        object Actions {
            const val CREATE = "CREATE"
            const val APPROVE = "APPROVE"
            const val CANCEL = "CANCEL"
        }

        fun create(messageId: String, action: String, aggregateId: String): PaymentIdempotency = PaymentIdempotency(
            messageId = messageId,
            action = action,
            aggregateId = aggregateId,
        )
    }
}
