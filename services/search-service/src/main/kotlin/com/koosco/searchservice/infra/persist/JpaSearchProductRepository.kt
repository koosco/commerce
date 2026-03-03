package com.koosco.searchservice.infra.persist

import com.koosco.searchservice.domain.entity.SearchProduct
import org.springframework.data.jpa.repository.JpaRepository

interface JpaSearchProductRepository : JpaRepository<SearchProduct, Long> {

    fun findByProductId(productId: Long): SearchProduct?

    fun deleteByProductId(productId: Long)
}
