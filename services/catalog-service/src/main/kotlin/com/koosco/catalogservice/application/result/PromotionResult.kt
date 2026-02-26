package com.koosco.catalogservice.application.result

import com.koosco.catalogservice.domain.entity.Promotion
import com.koosco.catalogservice.domain.enums.PromotionType
import java.time.LocalDateTime

data class PromotionInfo(
    val id: Long,
    val productId: Long,
    val discountPrice: Long,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val type: PromotionType,
    val priority: Int,
    val description: String?,
    val active: Boolean,
) {
    companion object {
        fun from(promotion: Promotion, now: LocalDateTime = LocalDateTime.now()): PromotionInfo = PromotionInfo(
            id = promotion.id!!,
            productId = promotion.productId,
            discountPrice = promotion.discountPrice,
            startAt = promotion.startAt,
            endAt = promotion.endAt,
            type = promotion.type,
            priority = promotion.priority,
            description = promotion.description,
            active = promotion.isActiveAt(now),
        )
    }
}

data class PromotionPriceInfo(
    val productId: Long,
    val originalPrice: Long,
    val discountPrice: Long?,
    val finalPrice: Long,
    val hasActivePromotion: Boolean,
)
