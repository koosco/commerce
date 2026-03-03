package com.koosco.searchservice.domain.repository

import com.koosco.searchservice.domain.entity.SearchProduct
import org.springframework.data.jpa.repository.JpaRepository

interface SearchProductRepository : JpaRepository<SearchProduct, Long> {

    fun findByProductId(productId: Long): SearchProduct?

    fun deleteByProductId(productId: Long)
}
