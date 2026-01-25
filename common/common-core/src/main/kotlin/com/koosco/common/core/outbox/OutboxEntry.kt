package com.koosco.common.core.outbox

import jakarta.persistence.Column
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import java.time.Instant

/**
 * Abstract base class for Outbox pattern entities.
 *
 * The Outbox pattern ensures atomicity between domain state changes and event publishing
 * by storing events in the same transaction as the domain changes.
 *
 * ## Usage with CDC (Debezium)
 * 1. Save domain entity and outbox entry in the same transaction
 * 2. Debezium captures the INSERT via binlog
 * 3. Debezium's Outbox Event Router transforms and publishes to Kafka
 * 4. Entry can be deleted after processing or kept for audit
 *
 * ## Implementation
 * Each service should create a concrete entity extending this class:
 * ```kotlin
 * @Entity
 * @Table(name = "order_outbox")
 * class OrderOutboxEntry(
 *     aggregateId: String,
 *     eventType: String,
 *     payload: String,
 *     val topic: String,
 *     val partitionKey: String,
 * ) : OutboxEntry(aggregateId = aggregateId, aggregateType = "Order",
 *                 eventType = eventType, payload = payload)
 * ```
 */
@MappedSuperclass
abstract class OutboxEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long = 0,

    /**
     * The identifier of the aggregate that produced this event.
     * Example: orderId, paymentId, inventoryId
     */
    @Column(name = "aggregate_id", nullable = false)
    open val aggregateId: String,

    /**
     * The type of the aggregate that produced this event.
     * Example: "Order", "Payment", "Inventory"
     */
    @Column(name = "aggregate_type", nullable = false, length = 100)
    open val aggregateType: String,

    /**
     * The type of the event.
     * Example: "order.placed", "payment.completed"
     */
    @Column(name = "event_type", nullable = false)
    open val eventType: String,

    /**
     * The serialized event payload (typically JSON CloudEvent).
     */
    @Column(name = "payload", columnDefinition = "TEXT", nullable = false)
    open val payload: String,

    /**
     * The current status of the outbox entry.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    open var status: OutboxStatus = OutboxStatus.PENDING,

    /**
     * The timestamp when this entry was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    open val createdAt: Instant = Instant.now(),
)
