package com.koosco.inventoryservice.infra.persist

import com.koosco.inventoryservice.application.port.InventoryApiIdempotencyRepository
import com.koosco.inventoryservice.domain.entity.InventoryApiIdempotency
import org.springframework.stereotype.Repository

@Repository
class InventoryApiIdempotencyRepositoryAdapter(private val jpaRepository: JpaInventoryApiIdempotencyRepository) :
    InventoryApiIdempotencyRepository {
    override fun existsByIdempotencyKeyAndOperationType(idempotencyKey: String, operationType: String) =
        jpaRepository.existsByIdempotencyKeyAndOperationType(idempotencyKey, operationType)

    override fun save(entry: InventoryApiIdempotency) = jpaRepository.save(entry)
}
