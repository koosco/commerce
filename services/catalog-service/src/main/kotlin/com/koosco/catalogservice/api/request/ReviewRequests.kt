package com.koosco.catalogservice.api.request

import com.koosco.catalogservice.application.command.CreateReviewCommand
import com.koosco.catalogservice.application.command.UpdateReviewCommand
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CreateReviewRequest(
    @field:NotNull
    val productId: Long,

    val orderItemId: Long? = null,

    @field:NotBlank
    val title: String,

    @field:NotBlank
    val content: String,

    @field:NotNull
    @field:Min(1)
    @field:Max(5)
    val rating: Int,

    val imageUrls: List<String> = emptyList(),

    val idempotencyKey: String? = null,
) {
    fun toCommand(userId: Long): CreateReviewCommand = CreateReviewCommand(
        productId = productId,
        userId = userId,
        orderItemId = orderItemId,
        title = title,
        content = content,
        rating = rating,
        imageUrls = imageUrls,
        idempotencyKey = idempotencyKey,
    )
}

data class UpdateReviewRequest(
    val title: String? = null,
    val content: String? = null,

    @field:Min(1)
    @field:Max(5)
    val rating: Int? = null,
) {
    fun toCommand(reviewId: Long, userId: Long): UpdateReviewCommand = UpdateReviewCommand(
        reviewId = reviewId,
        userId = userId,
        title = title,
        content = content,
        rating = rating,
    )
}
