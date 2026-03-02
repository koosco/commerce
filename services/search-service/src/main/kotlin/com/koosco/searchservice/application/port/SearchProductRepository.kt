package com.koosco.searchservice.application.port

import com.koosco.searchservice.domain.entity.SearchProduct

interface SearchProductRepository {

    fun save(searchProduct: SearchProduct): SearchProduct

    fun findByProductId(productId: Long): SearchProduct?

    fun findOrNull(id: Long): SearchProduct?

    fun deleteByProductId(productId: Long)
}
