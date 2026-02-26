package com.koosco.catalogservice.application.port

import com.koosco.catalogservice.domain.entity.Promotion
import java.time.LocalDateTime

interface PromotionRepository {
    fun save(promotion: Promotion): Promotion

    fun findOrNull(promotionId: Long): Promotion?

    fun findActiveByProductId(productId: Long, now: LocalDateTime): List<Promotion>

    fun findActiveByProductIds(productIds: List<Long>, now: LocalDateTime): List<Promotion>
}
