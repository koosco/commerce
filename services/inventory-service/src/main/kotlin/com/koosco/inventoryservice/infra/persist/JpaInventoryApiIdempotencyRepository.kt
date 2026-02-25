package com.koosco.inventoryservice.infra.persist

import com.koosco.inventoryservice.domain.entity.InventoryApiIdempotency
import org.springframework.data.jpa.repository.JpaRepository

interface JpaInventoryApiIdempotencyRepository : JpaRepository<InventoryApiIdempotency, Long> {
    fun existsByIdempotencyKeyAndOperationType(idempotencyKey: String, operationType: String): Boolean
}
