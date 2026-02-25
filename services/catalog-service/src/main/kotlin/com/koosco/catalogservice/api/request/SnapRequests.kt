package com.koosco.catalogservice.api.request

import com.koosco.catalogservice.application.command.CreateSnapCommand
import com.koosco.catalogservice.application.command.UpdateSnapCommand
import jakarta.validation.constraints.NotNull

data class CreateSnapRequest(
    @field:NotNull
    val productId: Long,

    val caption: String? = null,

    val imageUrls: List<String> = emptyList(),

    val idempotencyKey: String? = null,
) {
    fun toCommand(userId: Long): CreateSnapCommand = CreateSnapCommand(
        productId = productId,
        userId = userId,
        caption = caption,
        imageUrls = imageUrls,
        idempotencyKey = idempotencyKey,
    )
}

data class UpdateSnapRequest(val caption: String? = null) {
    fun toCommand(snapId: Long, userId: Long): UpdateSnapCommand = UpdateSnapCommand(
        snapId = snapId,
        userId = userId,
        caption = caption,
    )
}
