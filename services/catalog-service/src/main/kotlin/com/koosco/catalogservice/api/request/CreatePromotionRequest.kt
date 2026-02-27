package com.koosco.catalogservice.api.request

import com.koosco.catalogservice.application.command.CreatePromotionCommand
import com.koosco.catalogservice.domain.enums.PromotionType
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class CreatePromotionRequest(
    @field:NotNull(message = "Product ID is required")
    val productId: Long,

    @field:NotNull(message = "Discount price is required")
    @field:Min(value = 0, message = "Discount price must be non-negative")
    val discountPrice: Long,

    @field:NotNull(message = "Start time is required")
    val startAt: LocalDateTime,

    @field:NotNull(message = "End time is required")
    val endAt: LocalDateTime,

    @field:NotNull(message = "Promotion type is required")
    val type: PromotionType,

    @field:NotNull(message = "Priority is required")
    @field:Min(value = 0, message = "Priority must be non-negative")
    val priority: Int,

    val description: String? = null,
) {
    fun toCommand(): CreatePromotionCommand = CreatePromotionCommand(
        productId = productId,
        discountPrice = discountPrice,
        startAt = startAt,
        endAt = endAt,
        type = type,
        priority = priority,
        description = description,
    )
}
