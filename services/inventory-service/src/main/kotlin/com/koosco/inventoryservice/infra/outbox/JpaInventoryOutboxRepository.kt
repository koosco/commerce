package com.koosco.inventoryservice.infra.outbox

import com.koosco.inventoryservice.domain.entity.InventoryOutboxEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * JPA repository for InventoryOutboxEntry.
 */
@Repository
interface JpaInventoryOutboxRepository :
    JpaRepository<InventoryOutboxEntry, Long>,
    InventoryOutboxRepository
