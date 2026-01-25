package com.koosco.catalogservice.product.infra.outbox

import com.koosco.catalogservice.product.domain.entity.CatalogOutboxEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * JPA implementation of CatalogOutboxRepository.
 */
@Repository
interface JpaCatalogOutboxRepository :
    CatalogOutboxRepository,
    JpaRepository<CatalogOutboxEntry, Long>
