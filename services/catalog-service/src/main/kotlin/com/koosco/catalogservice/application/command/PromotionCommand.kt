package com.koosco.catalogservice.application.command

import com.koosco.catalogservice.domain.enums.PromotionType
import java.time.LocalDateTime

data class CreatePromotionCommand(
    val productId: Long,
    val discountPrice: Long,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val type: PromotionType,
    val priority: Int,
    val description: String? = null,
)

data class GetPromotionsByProductCommand(val productId: Long)

data class GetPromotionPriceCommand(val productId: Long)
