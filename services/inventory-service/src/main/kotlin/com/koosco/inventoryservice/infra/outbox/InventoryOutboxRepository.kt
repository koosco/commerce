package com.koosco.inventoryservice.infra.outbox

import com.koosco.inventoryservice.domain.entity.InventoryOutboxEntry

/**
 * Repository for managing InventoryOutboxEntry entities.
 */
interface InventoryOutboxRepository {
    /**
     * Save an outbox entry.
     * Should be called within the same transaction as the domain operation.
     */
    fun save(entry: InventoryOutboxEntry): InventoryOutboxEntry
}
