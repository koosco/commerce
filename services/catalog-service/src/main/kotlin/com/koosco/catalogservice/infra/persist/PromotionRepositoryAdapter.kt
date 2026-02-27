package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.application.port.PromotionRepository
import com.koosco.catalogservice.domain.entity.Promotion
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class PromotionRepositoryAdapter(private val jpaPromotionRepository: JpaPromotionRepository) : PromotionRepository {

    override fun save(promotion: Promotion): Promotion = jpaPromotionRepository.save(promotion)

    override fun findOrNull(promotionId: Long): Promotion? = jpaPromotionRepository.findByIdOrNull(promotionId)

    override fun findActiveByProductId(productId: Long, now: LocalDateTime): List<Promotion> =
        jpaPromotionRepository.findActiveByProductId(productId, now)

    override fun findActiveByProductIds(productIds: List<Long>, now: LocalDateTime): List<Promotion> =
        jpaPromotionRepository.findActiveByProductIds(productIds, now)
}
