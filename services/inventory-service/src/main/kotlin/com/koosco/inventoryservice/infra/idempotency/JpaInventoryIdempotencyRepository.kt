package com.koosco.inventoryservice.infra.idempotency

import com.koosco.inventoryservice.domain.entity.InventoryEventIdempotency
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * JPA repository for InventoryEventIdempotency.
 */
@Repository
interface JpaInventoryIdempotencyRepository :
    JpaRepository<InventoryEventIdempotency, Long>,
    InventoryIdempotencyRepository {
    override fun findByMessageIdAndAction(messageId: String, action: String): InventoryEventIdempotency?
}
