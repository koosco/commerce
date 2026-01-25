package com.koosco.paymentservice.domain.entity

import com.koosco.common.core.outbox.OutboxEntry
import com.koosco.common.core.outbox.OutboxStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table

/**
 * Outbox entry for payment-service events.
 *
 * Stores events to be published to Kafka via CDC (Debezium).
 * Ensures atomicity between payment state changes and event publishing.
 */
@Entity
@Table(
    name = "payment_outbox",
    indexes = [
        Index(name = "idx_payment_outbox_status", columnList = "status, created_at"),
    ],
)
class PaymentOutboxEntry(
    aggregateId: String,
    eventType: String,
    payload: String,

    /**
     * The Kafka topic to publish this event to.
     * Used by Debezium Outbox Event Router.
     */
    @Column(name = "topic", nullable = false)
    val topic: String,

    /**
     * The partition key for Kafka message.
     * Ensures ordering for events with the same key.
     */
    @Column(name = "partition_key", nullable = false)
    val partitionKey: String,
) : OutboxEntry(
    aggregateId = aggregateId,
    aggregateType = "Payment",
    eventType = eventType,
    payload = payload,
    status = OutboxStatus.PENDING,
) {
    companion object {
        fun create(
            aggregateId: String,
            eventType: String,
            payload: String,
            topic: String,
            partitionKey: String,
        ): PaymentOutboxEntry = PaymentOutboxEntry(
            aggregateId = aggregateId,
            eventType = eventType,
            payload = payload,
            topic = topic,
            partitionKey = partitionKey,
        )
    }
}
