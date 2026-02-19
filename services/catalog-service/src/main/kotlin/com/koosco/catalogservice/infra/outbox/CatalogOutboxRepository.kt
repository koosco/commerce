package com.koosco.catalogservice.infra.outbox

import com.koosco.catalogservice.domain.entity.CatalogOutboxEntry

/**
 * Repository for catalog outbox entries.
 *
 * Outbox entries are saved within the same transaction as domain operations.
 * Debezium CDC captures these inserts and publishes them to Kafka.
 */
interface CatalogOutboxRepository {
    fun save(entry: CatalogOutboxEntry): CatalogOutboxEntry
}
