package com.koosco.searchservice.application.result

import com.koosco.searchservice.domain.entity.SearchProduct

/**
 * 검색 결과 상품 정보.
 */
data class SearchProductResult(
    val productId: Long,
    val name: String,
    val sellingPrice: Long,
    val thumbnailImageUrl: String?,
    val averageRating: Double,
    val reviewCount: Int,
) {
    companion object {
        fun from(entity: SearchProduct): SearchProductResult = SearchProductResult(
            productId = entity.productId,
            name = entity.name,
            sellingPrice = entity.sellingPrice,
            thumbnailImageUrl = entity.thumbnailImageUrl,
            averageRating = entity.averageRating,
            reviewCount = entity.reviewCount,
        )
    }
}
