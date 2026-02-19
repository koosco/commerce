package com.koosco.orderservice.infra.outbox

import com.koosco.orderservice.domain.entity.OrderOutboxEntry

/**
 * Repository for managing OrderOutboxEntry entities.
 */
interface OrderOutboxRepository {
    /**
     * Save an outbox entry.
     * Should be called within the same transaction as the domain operation.
     */
    fun save(entry: OrderOutboxEntry): OrderOutboxEntry
}
