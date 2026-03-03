package com.koosco.searchservice.application.command

import com.koosco.searchservice.domain.enums.SearchSortStrategy
import org.springframework.data.domain.Pageable

/**
 * 상품 검색 커맨드.
 */
data class SearchProductCommand(
    val keyword: String?,
    val categoryId: Long?,
    val brandId: Long?,
    val minPrice: Long?,
    val maxPrice: Long?,
    val status: String = "ACTIVE",
    val sort: SearchSortStrategy = SearchSortStrategy.RELEVANCE,
    val pageable: Pageable,
)
