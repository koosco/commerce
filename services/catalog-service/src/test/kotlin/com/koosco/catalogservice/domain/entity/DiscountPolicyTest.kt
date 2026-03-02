package com.koosco.catalogservice.domain.entity

import com.koosco.catalogservice.domain.enums.DiscountType
import com.koosco.catalogservice.domain.enums.ProductStatus
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("DiscountPolicy 도메인 테스트")
class DiscountPolicyTest {

    private val product = Product(
        id = 1L,
        productCode = "TEST-001",
        name = "테스트 상품",
        price = 10000,
        status = ProductStatus.ACTIVE,
    )

    private val now = LocalDateTime.of(2025, 6, 15, 12, 0)

    @Nested
    @DisplayName("isActiveAt 메서드는")
    inner class IsActiveAtTest {

        @Test
        fun `현재 시각이 기간 내이면 true를 반환한다`() {
            val policy = DiscountPolicy.create(
                product = product,
                name = "할인",
                discountType = DiscountType.RATE,
                discountValue = 10,
                startAt = now.minusDays(1),
                endAt = now.plusDays(1),
            )

            assertThat(policy.isActiveAt(now)).isTrue()
        }

        @Test
        fun `시작 시각과 같으면 true를 반환한다`() {
            val policy = DiscountPolicy.create(
                product = product,
                name = "할인",
                discountType = DiscountType.RATE,
                discountValue = 10,
                startAt = now,
                endAt = now.plusDays(1),
            )

            assertThat(policy.isActiveAt(now)).isTrue()
        }

        @Test
        fun `종료 시각과 같으면 true를 반환한다`() {
            val policy = DiscountPolicy.create(
                product = product,
                name = "할인",
                discountType = DiscountType.RATE,
                discountValue = 10,
                startAt = now.minusDays(1),
                endAt = now,
            )

            assertThat(policy.isActiveAt(now)).isTrue()
        }

        @Test
        fun `기간이 지났으면 false를 반환한다`() {
            val policy = DiscountPolicy.create(
                product = product,
                name = "할인",
                discountType = DiscountType.RATE,
                discountValue = 10,
                startAt = now.minusDays(10),
                endAt = now.minusDays(1),
            )

            assertThat(policy.isActiveAt(now)).isFalse()
        }

        @Test
        fun `아직 시작하지 않았으면 false를 반환한다`() {
            val policy = DiscountPolicy.create(
                product = product,
                name = "할인",
                discountType = DiscountType.RATE,
                discountValue = 10,
                startAt = now.plusDays(1),
                endAt = now.plusDays(10),
            )

            assertThat(policy.isActiveAt(now)).isFalse()
        }
    }

    @Nested
    @DisplayName("calculateDiscountAmount 메서드는")
    inner class CalculateDiscountAmountTest {

        @Test
        fun `정률 할인 금액을 계산한다`() {
            val policy = DiscountPolicy.create(
                product = product,
                name = "10% 할인",
                discountType = DiscountType.RATE,
                discountValue = 10,
                startAt = now.minusDays(1),
                endAt = now.plusDays(1),
            )

            assertThat(policy.calculateDiscountAmount(10000)).isEqualTo(1000)
        }

        @Test
        fun `정률 할인에서 원래 가격을 초과하지 않는다`() {
            val policy = DiscountPolicy.create(
                product = product,
                name = "100% 할인",
                discountType = DiscountType.RATE,
                discountValue = 100,
                startAt = now.minusDays(1),
                endAt = now.plusDays(1),
            )

            assertThat(policy.calculateDiscountAmount(10000)).isEqualTo(10000)
        }

        @Test
        fun `정액 할인 금액을 계산한다`() {
            val policy = DiscountPolicy.create(
                product = product,
                name = "3000원 할인",
                discountType = DiscountType.AMOUNT,
                discountValue = 3000,
                startAt = now.minusDays(1),
                endAt = now.plusDays(1),
            )

            assertThat(policy.calculateDiscountAmount(10000)).isEqualTo(3000)
        }

        @Test
        fun `정액 할인이 원래 가격을 초과하면 원래 가격을 반환한다`() {
            val policy = DiscountPolicy.create(
                product = product,
                name = "15000원 할인",
                discountType = DiscountType.AMOUNT,
                discountValue = 15000,
                startAt = now.minusDays(1),
                endAt = now.plusDays(1),
            )

            assertThat(policy.calculateDiscountAmount(10000)).isEqualTo(10000)
        }
    }

    @Nested
    @DisplayName("calculateSellingPrice 메서드는")
    inner class CalculateSellingPriceTest {

        @Test
        fun `정률 할인 후 판매가를 계산한다`() {
            val policy = DiscountPolicy.create(
                product = product,
                name = "20% 할인",
                discountType = DiscountType.RATE,
                discountValue = 20,
                startAt = now.minusDays(1),
                endAt = now.plusDays(1),
            )

            assertThat(policy.calculateSellingPrice(10000)).isEqualTo(8000)
        }

        @Test
        fun `정액 할인 후 판매가를 계산한다`() {
            val policy = DiscountPolicy.create(
                product = product,
                name = "2000원 할인",
                discountType = DiscountType.AMOUNT,
                discountValue = 2000,
                startAt = now.minusDays(1),
                endAt = now.plusDays(1),
            )

            assertThat(policy.calculateSellingPrice(10000)).isEqualTo(8000)
        }

        @Test
        fun `판매가가 0 미만이 되지 않는다`() {
            val policy = DiscountPolicy.create(
                product = product,
                name = "100% 할인",
                discountType = DiscountType.RATE,
                discountValue = 100,
                startAt = now.minusDays(1),
                endAt = now.plusDays(1),
            )

            assertThat(policy.calculateSellingPrice(10000)).isEqualTo(0)
        }
    }

    @Nested
    @DisplayName("생성 검증은")
    inner class ValidationTest {

        @Test
        fun `할인 값이 0이면 예외를 던진다`() {
            assertThatThrownBy {
                DiscountPolicy.create(
                    product = product,
                    name = "잘못된 할인",
                    discountType = DiscountType.RATE,
                    discountValue = 0,
                    startAt = now,
                    endAt = now.plusDays(1),
                )
            }.isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `시작일이 종료일보다 이후이면 예외를 던진다`() {
            assertThatThrownBy {
                DiscountPolicy.create(
                    product = product,
                    name = "잘못된 할인",
                    discountType = DiscountType.RATE,
                    discountValue = 10,
                    startAt = now.plusDays(1),
                    endAt = now,
                )
            }.isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `정률 할인이 100을 초과하면 예외를 던진다`() {
            assertThatThrownBy {
                DiscountPolicy.create(
                    product = product,
                    name = "잘못된 할인",
                    discountType = DiscountType.RATE,
                    discountValue = 101,
                    startAt = now,
                    endAt = now.plusDays(1),
                )
            }.isInstanceOf(IllegalArgumentException::class.java)
        }
    }
}
