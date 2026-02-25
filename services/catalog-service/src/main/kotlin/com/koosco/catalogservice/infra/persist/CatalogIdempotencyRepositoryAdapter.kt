package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.application.port.CatalogIdempotencyRepository
import com.koosco.catalogservice.domain.entity.CatalogIdempotency
import org.springframework.stereotype.Repository

@Repository
class CatalogIdempotencyRepositoryAdapter(private val jpaRepository: JpaCatalogIdempotencyRepository) :
    CatalogIdempotencyRepository {

    override fun findByIdempotencyKeyAndResourceType(
        idempotencyKey: String,
        resourceType: String,
    ): CatalogIdempotency? = jpaRepository.findByIdempotencyKeyAndResourceType(idempotencyKey, resourceType)

    override fun save(entry: CatalogIdempotency): CatalogIdempotency = jpaRepository.save(entry)
}
