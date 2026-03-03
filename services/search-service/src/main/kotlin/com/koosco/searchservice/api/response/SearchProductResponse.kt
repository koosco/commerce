package com.koosco.searchservice.api.response

import com.koosco.searchservice.application.result.SearchProductResult

/**
 * 검색 결과 상품 응답.
 */
data class SearchProductResponse(
    val productId: Long,
    val name: String,
    val sellingPrice: Long,
    val thumbnailImageUrl: String?,
    val averageRating: Double,
    val reviewCount: Int,
) {
    companion object {
        fun from(result: SearchProductResult): SearchProductResponse = SearchProductResponse(
            productId = result.productId,
            name = result.name,
            sellingPrice = result.sellingPrice,
            thumbnailImageUrl = result.thumbnailImageUrl,
            averageRating = result.averageRating,
            reviewCount = result.reviewCount,
        )
    }
}
