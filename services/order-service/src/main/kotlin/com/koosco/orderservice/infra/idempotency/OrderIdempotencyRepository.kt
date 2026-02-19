package com.koosco.orderservice.infra.idempotency

import com.koosco.orderservice.domain.entity.OrderEventIdempotency

/**
 * Repository for managing OrderEventIdempotency entities.
 */
interface OrderIdempotencyRepository {
    /**
     * Check if an event has already been processed for a given action.
     */
    fun existsByEventIdAndAction(eventId: String, action: String): Boolean

    /**
     * Save an idempotency entry.
     * Should be called within the same transaction as the business logic.
     */
    fun save(entry: OrderEventIdempotency): OrderEventIdempotency

    /**
     * Find an idempotency entry by event ID and action.
     */
    fun findByEventIdAndAction(eventId: String, action: String): OrderEventIdempotency?
}
