package com.koosco.orderservice.order.infra.outbox

import com.koosco.orderservice.order.domain.entity.OrderOutboxEntry

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
