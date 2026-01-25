package com.koosco.inventoryservice.inventory.infra.idempotency

import com.koosco.inventoryservice.inventory.domain.entity.InventoryEventIdempotency
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * JPA repository for InventoryEventIdempotency.
 */
@Repository
interface JpaInventoryIdempotencyRepository :
    JpaRepository<InventoryEventIdempotency, Long>,
    InventoryIdempotencyRepository {
    override fun findByEventIdAndAction(eventId: String, action: String): InventoryEventIdempotency?
}
