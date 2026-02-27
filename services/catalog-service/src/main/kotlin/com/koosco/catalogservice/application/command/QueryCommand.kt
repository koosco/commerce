package com.koosco.catalogservice.application.command

import com.koosco.catalogservice.domain.enums.SortStrategy
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
    val sort: SortStrategy,
    val pageable: Pageable,
    val attributeFilters: Map<Long, String> = emptyMap(),
    val userId: Long? = null,
)

/**
 * @deprecated Use [SortStrategy] instead. Kept for backward compatibility.
 */
@Deprecated("Use SortStrategy instead", ReplaceWith("SortStrategy"))
typealias ProductSortType = SortStrategy

data class GetProductDetailCommand(val productId: Long, val userId: Long? = null)
