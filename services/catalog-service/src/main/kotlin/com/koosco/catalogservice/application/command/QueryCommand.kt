package com.koosco.catalogservice.application.command

import org.springframework.data.domain.Pageable

/**
 * fileName       : QueryCommand
 * author         : koo
 * date           : 2025. 12. 25. 오전 1:45
 * description    :
 */
data class FindSkuCommand(val productId: Long, val options: Map<String, String>)

data class GetProductListCommand(
    val categoryId: Long?,
    val keyword: String?,
    val brandId: Long?,
    val minPrice: Long?,
    val maxPrice: Long?,
    val sort: ProductSortType,
    val pageable: Pageable,
    val attributeFilters: Map<Long, String> = emptyMap(),
    val userId: Long? = null,
)

enum class ProductSortType {
    LATEST,
    PRICE_ASC,
    PRICE_DESC,
    RATING_DESC,
    REVIEW_COUNT_DESC,
}

data class GetProductDetailCommand(val productId: Long, val userId: Long? = null)
