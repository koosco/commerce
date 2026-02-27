package com.koosco.catalogservice.domain.entity

import com.koosco.catalogservice.domain.enums.DiscountType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "discount_policies")
class DiscountPolicy(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product,

    @Column(nullable = false, length = 100)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    val discountType: DiscountType,

    /** RATE: 할인율 (0~100), AMOUNT: 할인금액 */
    @Column(name = "discount_value", nullable = false)
    val discountValue: Long,

    @Column(name = "start_at", nullable = false)
    val startAt: LocalDateTime,

    @Column(name = "end_at", nullable = false)
    val endAt: LocalDateTime,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {

    /** 현재 시각 기준으로 적용 가능한 할인인지 확인 */
    fun isActiveAt(now: LocalDateTime): Boolean = now in startAt..endAt

    /** 원래 가격에 이 할인을 적용했을 때의 할인 금액 계산 */
    fun calculateDiscountAmount(originalPrice: Long): Long = when (discountType) {
        DiscountType.RATE -> (originalPrice * discountValue / 100).coerceAtMost(originalPrice)
        DiscountType.AMOUNT -> discountValue.coerceAtMost(originalPrice)
    }

    /** 원래 가격에 이 할인을 적용했을 때의 판매가 계산 */
    fun calculateSellingPrice(originalPrice: Long): Long =
        (originalPrice - calculateDiscountAmount(originalPrice)).coerceAtLeast(0)

    init {
        require(discountValue > 0) { "할인 값은 0보다 커야 합니다." }
        require(!startAt.isAfter(endAt)) { "할인 시작일은 종료일보다 이후일 수 없습니다." }
        if (discountType == DiscountType.RATE) {
            require(discountValue in 1..100) { "할인율은 1~100 사이여야 합니다." }
        }
    }

    companion object {
        fun create(
            product: Product,
            name: String,
            discountType: DiscountType,
            discountValue: Long,
            startAt: LocalDateTime,
            endAt: LocalDateTime,
        ): DiscountPolicy = DiscountPolicy(
            product = product,
            name = name,
            discountType = discountType,
            discountValue = discountValue,
            startAt = startAt,
            endAt = endAt,
        )
    }
}
