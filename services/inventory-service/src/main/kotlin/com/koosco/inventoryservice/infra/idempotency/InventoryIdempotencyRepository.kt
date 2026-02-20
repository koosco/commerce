package com.koosco.inventoryservice.infra.idempotency

import com.koosco.inventoryservice.domain.entity.InventoryEventIdempotency

/**
 * Repository for managing InventoryEventIdempotency entities.
 */
interface InventoryIdempotencyRepository {
    fun existsByMessageIdAndAction(messageId: String, action: String): Boolean
    fun save(entry: InventoryEventIdempotency): InventoryEventIdempotency
    fun findByMessageIdAndAction(messageId: String, action: String): InventoryEventIdempotency?
}
