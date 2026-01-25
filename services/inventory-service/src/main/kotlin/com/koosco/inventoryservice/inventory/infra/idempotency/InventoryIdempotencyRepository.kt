package com.koosco.inventoryservice.inventory.infra.idempotency

import com.koosco.inventoryservice.inventory.domain.entity.InventoryEventIdempotency

/**
 * Repository for managing InventoryEventIdempotency entities.
 */
interface InventoryIdempotencyRepository {
    /**
     * Check if an event has already been processed for a given action.
     */
    fun existsByEventIdAndAction(eventId: String, action: String): Boolean

    /**
     * Save an idempotency entry.
     * Should be called within the same transaction as the business logic.
     */
    fun save(entry: InventoryEventIdempotency): InventoryEventIdempotency

    /**
     * Find an idempotency entry by event ID and action.
     */
    fun findByEventIdAndAction(eventId: String, action: String): InventoryEventIdempotency?
}
