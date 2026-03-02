package com.koosco.catalogservice.domain.service

import com.koosco.catalogservice.domain.entity.Promotion
import com.koosco.catalogservice.domain.enums.PromotionType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("PromotionPriceResolver 도메인 서비스 테스트")
class PromotionPriceResolverTest {

    private val now = LocalDateTime.of(2025, 6, 15, 12, 0)

    @Nested
    @DisplayName("resolve 메서드는")
    inner class ResolveTest {

        @Test
        fun `프로모션이 없으면 null을 반환한다`() {
            val result = PromotionPriceResolver.resolve(emptyList())

            assertThat(result).isNull()
        }

        @Test
        fun `단일 프로모션이면 해당 할인가를 반환한다`() {
            val promotion = Promotion.create(
                productId = 1L,
                discountPrice = 8000,
                startAt = now.minusDays(1),
                endAt = now.plusDays(1),
                type = PromotionType.CAMPAIGN,
                priority = 0,
            )

            val result = PromotionPriceResolver.resolve(listOf(promotion))

            assertThat(result).isEqualTo(8000)
        }

        @Test
        fun `여러 프로모션 중 우선순위가 가장 높은 할인가를 반환한다`() {
            val lowPriority = Promotion.create(
                productId = 1L,
                discountPrice = 9000,
                startAt = now.minusDays(1),
                endAt = now.plusDays(1),
                type = PromotionType.REGULAR,
                priority = 10,
            )
            val highPriority = Promotion.create(
                productId = 1L,
                discountPrice = 7000,
                startAt = now.minusDays(1),
                endAt = now.plusDays(1),
                type = PromotionType.CAMPAIGN,
                priority = 0,
            )

            val result = PromotionPriceResolver.resolve(listOf(lowPriority, highPriority))

            assertThat(result).isEqualTo(7000)
        }
    }
}
