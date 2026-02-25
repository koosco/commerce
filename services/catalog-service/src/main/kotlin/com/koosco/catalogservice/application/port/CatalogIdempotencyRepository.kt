package com.koosco.catalogservice.application.port

import com.koosco.catalogservice.domain.entity.CatalogIdempotency

interface CatalogIdempotencyRepository {
    fun findByIdempotencyKeyAndResourceType(idempotencyKey: String, resourceType: String): CatalogIdempotency?
    fun save(entry: CatalogIdempotency): CatalogIdempotency
}
