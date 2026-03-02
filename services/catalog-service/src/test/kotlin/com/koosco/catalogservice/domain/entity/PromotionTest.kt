package com.koosco.catalogservice.domain.entity

import com.koosco.catalogservice.domain.enums.PromotionType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("Promotion 도메인 테스트")
class PromotionTest {

    private val now = LocalDateTime.of(2025, 6, 15, 12, 0)

    @Nested
    @DisplayName("isActiveAt 메서드는")
    inner class IsActiveAtTest {

        @Test
        fun `기간 내이면 true를 반환한다`() {
            val promotion = Promotion.create(
                productId = 1L,
                discountPrice = 8000,
                startAt = now.minusDays(1),
                endAt = now.plusDays(1),
                type = PromotionType.CAMPAIGN,
                priority = 0,
            )

            assertThat(promotion.isActiveAt(now)).isTrue()
        }

        @Test
        fun `기간이 지나면 false를 반환한다`() {
            val promotion = Promotion.create(
                productId = 1L,
                discountPrice = 8000,
                startAt = now.minusDays(10),
                endAt = now.minusDays(1),
                type = PromotionType.CAMPAIGN,
                priority = 0,
            )

            assertThat(promotion.isActiveAt(now)).isFalse()
        }

        @Test
        fun `시작 시간과 같으면 true를 반환한다`() {
            val promotion = Promotion.create(
                productId = 1L,
                discountPrice = 8000,
                startAt = now,
                endAt = now.plusDays(1),
                type = PromotionType.CAMPAIGN,
                priority = 0,
            )

            assertThat(promotion.isActiveAt(now)).isTrue()
        }

        @Test
        fun `종료 시간과 같으면 true를 반환한다`() {
            val promotion = Promotion.create(
                productId = 1L,
                discountPrice = 8000,
                startAt = now.minusDays(1),
                endAt = now,
                type = PromotionType.CAMPAIGN,
                priority = 0,
            )

            assertThat(promotion.isActiveAt(now)).isTrue()
        }
    }

    @Nested
    @DisplayName("생성 검증은")
    inner class ValidationTest {

        @Test
        fun `할인가가 음수이면 예외를 던진다`() {
            assertThatThrownBy {
                Promotion.create(
                    productId = 1L,
                    discountPrice = -1,
                    startAt = now,
                    endAt = now.plusDays(1),
                    type = PromotionType.CAMPAIGN,
                    priority = 0,
                )
            }.isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `종료 시간이 시작 시간보다 이전이면 예외를 던진다`() {
            assertThatThrownBy {
                Promotion.create(
                    productId = 1L,
                    discountPrice = 8000,
                    startAt = now.plusDays(1),
                    endAt = now,
                    type = PromotionType.CAMPAIGN,
                    priority = 0,
                )
            }.isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `우선순위가 음수이면 예외를 던진다`() {
            assertThatThrownBy {
                Promotion.create(
                    productId = 1L,
                    discountPrice = 8000,
                    startAt = now,
                    endAt = now.plusDays(1),
                    type = PromotionType.CAMPAIGN,
                    priority = -1,
                )
            }.isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `할인가가 0이면 정상 생성된다`() {
            val promotion = Promotion.create(
                productId = 1L,
                discountPrice = 0,
                startAt = now,
                endAt = now.plusDays(1),
                type = PromotionType.REGULAR,
                priority = 0,
            )

            assertThat(promotion.discountPrice).isEqualTo(0)
        }
    }
}
