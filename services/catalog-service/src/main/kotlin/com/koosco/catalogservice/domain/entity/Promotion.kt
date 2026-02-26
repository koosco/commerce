package com.koosco.catalogservice.domain.entity

import com.koosco.catalogservice.domain.enums.PromotionType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.LocalDateTime

/**
 * Insert-only 패턴 기반 프로모션 엔티티.
 *
 * 상품 가격을 직접 UPDATE하지 않고 프로모션을 INSERT하여 할인가를 관리한다.
 * startAt ~ endAt 범위 내의 활성 프로모션 중 우선순위가 가장 높은(priority 값이 가장 낮은)
 * 프로모션의 discountPrice가 최종 가격으로 적용된다.
 */
@Entity
@Table(
    name = "promotions",
    indexes = [
        Index(
            name = "idx_promotion_product_active",
            columnList = "product_id, start_at, end_at",
        ),
    ],
)
class Promotion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "product_id", nullable = false)
    val productId: Long,

    @Column(name = "discount_price", nullable = false)
    val discountPrice: Long,

    @Column(name = "start_at", nullable = false)
    val startAt: LocalDateTime,

    @Column(name = "end_at", nullable = false)
    val endAt: LocalDateTime,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val type: PromotionType,

    @Column(nullable = false)
    val priority: Int,

    @Column(name = "description", length = 500)
    val description: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    init {
        require(discountPrice >= 0) { "할인가는 0 이상이어야 합니다." }
        require(!endAt.isBefore(startAt)) { "종료 시간은 시작 시간 이후여야 합니다." }
        require(priority >= 0) { "우선순위는 0 이상이어야 합니다." }
    }

    fun isActiveAt(now: LocalDateTime): Boolean = !now.isBefore(startAt) && !now.isAfter(endAt)

    companion object {
        fun create(
            productId: Long,
            discountPrice: Long,
            startAt: LocalDateTime,
            endAt: LocalDateTime,
            type: PromotionType,
            priority: Int,
            description: String? = null,
        ): Promotion = Promotion(
            productId = productId,
            discountPrice = discountPrice,
            startAt = startAt,
            endAt = endAt,
            type = type,
            priority = priority,
            description = description,
        )
    }
}
