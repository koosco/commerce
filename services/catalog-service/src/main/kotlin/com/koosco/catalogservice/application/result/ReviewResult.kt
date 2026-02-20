package com.koosco.catalogservice.application.result

import com.koosco.catalogservice.domain.entity.Review
import com.koosco.catalogservice.domain.enums.ContentStatus
import java.time.LocalDateTime

data class ReviewResult(
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
        fun from(review: Review): ReviewResult = ReviewResult(
            reviewId = review.id!!,
            productId = review.productId,
            userId = review.userId,
            orderItemId = review.orderItemId,
            title = review.title,
            content = review.content,
            rating = review.rating,
            status = review.status,
            likeCount = review.likeCount,
            imageUrls = review.images.sortedBy { it.ordering }.map { it.imageUrl },
            createdAt = review.createdAt,
            updatedAt = review.updatedAt,
        )
    }
}
