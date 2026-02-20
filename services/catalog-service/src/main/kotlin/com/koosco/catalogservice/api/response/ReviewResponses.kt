package com.koosco.catalogservice.api.response

import com.koosco.catalogservice.application.result.ReviewResult
import com.koosco.catalogservice.domain.enums.ContentStatus
import java.time.LocalDateTime

data class ReviewResponse(
    val reviewId: Long,
    val productId: Long,
    val userId: Long,
    val orderItemId: Long?,
    val title: String,
    val content: String,
    val rating: Int,
    val status: ContentStatus,
    val likeCount: Int,
    val imageUrls: List<String>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(result: ReviewResult): ReviewResponse = ReviewResponse(
            reviewId = result.reviewId,
            productId = result.productId,
            userId = result.userId,
            orderItemId = result.orderItemId,
            title = result.title,
            content = result.content,
            rating = result.rating,
            status = result.status,
            likeCount = result.likeCount,
            imageUrls = result.imageUrls,
            createdAt = result.createdAt,
            updatedAt = result.updatedAt,
        )
    }
}

data class LikeToggleResponse(val liked: Boolean)
