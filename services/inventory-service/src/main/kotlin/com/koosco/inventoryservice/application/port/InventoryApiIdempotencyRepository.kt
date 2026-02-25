package com.koosco.inventoryservice.application.port

import com.koosco.inventoryservice.domain.entity.InventoryApiIdempotency

interface InventoryApiIdempotencyRepository {
    fun existsByIdempotencyKeyAndOperationType(idempotencyKey: String, operationType: String): Boolean
    fun save(entry: InventoryApiIdempotency): InventoryApiIdempotency
}
