package com.koosco.catalogservice.domain.entity

import com.koosco.common.core.outbox.OutboxStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.Instant

/**
 * Outbox entry for catalog-service integration events.
 *
 * Events are stored here within the same transaction as domain operations,
 * then published to Kafka by Debezium CDC.
 *
 * Note: Does not extend common-core OutboxEntry due to QueryDSL limitations
 * with @MappedSuperclass across modules.
 */
@Entity
@Table(
    name = "catalog_outbox",
    indexes = [
        Index(name = "idx_catalog_outbox_status", columnList = "status, created_at"),
    ],
)
class CatalogOutboxEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "aggregate_id", nullable = false)
    val aggregateId: String,

    @Column(name = "aggregate_type", nullable = false, length = 100)
    val aggregateType: String = "Catalog",

    @Column(name = "event_type", nullable = false)
    val eventType: String,

    @Column(name = "payload", columnDefinition = "TEXT", nullable = false)
    val payload: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: OutboxStatus = OutboxStatus.PENDING,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "topic", nullable = false)
    val topic: String,

    @Column(name = "partition_key", nullable = false)
    val partitionKey: String,
) {
    companion object {
        fun create(
            aggregateId: String,
            eventType: String,
            payload: String,
            topic: String,
            partitionKey: String,
        ): CatalogOutboxEntry = CatalogOutboxEntry(
            aggregateId = aggregateId,
            eventType = eventType,
            payload = payload,
            topic = topic,
            partitionKey = partitionKey,
        )
    }
}
