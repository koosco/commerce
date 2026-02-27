package com.koosco.catalogservice.api.response

import com.koosco.catalogservice.application.result.PromotionInfo
import com.koosco.catalogservice.application.result.PromotionPriceInfo
import com.koosco.catalogservice.domain.enums.PromotionType
import java.time.LocalDateTime

data class PromotionResponse(
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
        fun from(info: PromotionInfo): PromotionResponse = PromotionResponse(
            id = info.id,
            productId = info.productId,
            discountPrice = info.discountPrice,
            startAt = info.startAt,
            endAt = info.endAt,
            type = info.type,
            priority = info.priority,
            description = info.description,
            active = info.active,
        )
    }
}

data class PromotionPriceResponse(
    val productId: Long,
    val originalPrice: Long,
    val discountPrice: Long?,
    val finalPrice: Long,
    val hasActivePromotion: Boolean,
) {
    companion object {
        fun from(info: PromotionPriceInfo): PromotionPriceResponse = PromotionPriceResponse(
            productId = info.productId,
            originalPrice = info.originalPrice,
            discountPrice = info.discountPrice,
            finalPrice = info.finalPrice,
            hasActivePromotion = info.hasActivePromotion,
        )
    }
}
