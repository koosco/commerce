package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.domain.entity.CatalogIdempotency
import org.springframework.data.jpa.repository.JpaRepository

interface JpaCatalogIdempotencyRepository : JpaRepository<CatalogIdempotency, Long> {
    fun findByIdempotencyKeyAndResourceType(idempotencyKey: String, resourceType: String): CatalogIdempotency?
}
