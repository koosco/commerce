package com.koosco.searchservice.application.port

import com.koosco.searchservice.application.command.SearchProductCommand
import com.koosco.searchservice.domain.entity.SearchProduct
import org.springframework.data.domain.Page

interface SearchProductRepository {

    fun save(searchProduct: SearchProduct): SearchProduct

    fun findByProductId(productId: Long): SearchProduct?

    fun findOrNull(id: Long): SearchProduct?

    fun deleteByProductId(productId: Long)

    fun search(command: SearchProductCommand): Page<SearchProduct>
}
