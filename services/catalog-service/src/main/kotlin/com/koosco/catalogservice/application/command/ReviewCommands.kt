package com.koosco.catalogservice.application.command

data class CreateReviewCommand(
    val productId: Long,
    val userId: Long,
    val orderItemId: Long?,
    val title: String,
    val content: String,
    val rating: Int,
    val imageUrls: List<String> = emptyList(),
    val idempotencyKey: String? = null,
)

data class UpdateReviewCommand(
    val reviewId: Long,
    val userId: Long,
    val title: String?,
    val content: String?,
    val rating: Int?,
)

data class DeleteReviewCommand(val reviewId: Long, val userId: Long)
