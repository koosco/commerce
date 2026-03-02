package com.koosco.orderservice.domain.vo

import com.koosco.common.core.exception.BadRequestException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Value Object 테스트")
class ValueObjectsTest {

    @Nested
    @DisplayName("Money")
    inner class MoneyTest {

        @Test
        fun `Money 생성 - 양수 금액`() {
            val money = Money(10000)
            assertThat(money.amount).isEqualTo(10000)
        }

        @Test
        fun `Money 생성 - 0원`() {
            val money = Money(0)
            assertThat(money.isZero()).isTrue()
        }

        @Test
        fun `Money 생성 - 음수 금액이면 예외가 발생한다`() {
            assertThatThrownBy { Money(-1) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `Money ZERO 상수`() {
            assertThat(Money.ZERO.amount).isEqualTo(0)
            assertThat(Money.ZERO.isZero()).isTrue()
        }

        @Test
        fun `Money 더하기`() {
            val result = Money(1000) + Money(2000)
            assertThat(result).isEqualTo(Money(3000))
        }

        @Test
        fun `Money 빼기`() {
            val result = Money(3000) - Money(1000)
            assertThat(result).isEqualTo(Money(2000))
        }

        @Test
        fun `Money 빼기 - 금액이 부족하면 예외가 발생한다`() {
            assertThatThrownBy { Money(1000) - Money(2000) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `Money 곱하기`() {
            val result = Money(1000) * 3
            assertThat(result).isEqualTo(Money(3000))
        }

        @Test
        fun `Money 곱하기 - 수량이 음수이면 예외가 발생한다`() {
            assertThatThrownBy { Money(1000) * -1 }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `Money 곱하기 - 수량이 0이면 ZERO를 반환한다`() {
            val result = Money(1000) * 0
            assertThat(result).isEqualTo(Money.ZERO)
        }
    }

    @Nested
    @DisplayName("OrderAmount")
    inner class OrderAmountTest {

        private fun createItemSpecs(): List<OrderItemSpec> = listOf(
            OrderItemSpec(1L, 1L, 1L, "상품1", null, 2, Money(10000)),
            OrderItemSpec(2L, 2L, 1L, "상품2", null, 1, Money(5000)),
        )

        @Test
        fun `OrderAmount 생성 - 정상 케이스`() {
            val specs = createItemSpecs()
            val amount = OrderAmount.from(specs, discount = Money(1000), shippingFee = Money(3000))

            // subtotal = 10000*2 + 5000*1 = 25000
            assertThat(amount.subtotal).isEqualTo(Money(25000))
            assertThat(amount.discount).isEqualTo(Money(1000))
            assertThat(amount.shippingFee).isEqualTo(Money(3000))
            // total = 25000 - 1000 + 3000 = 27000
            assertThat(amount.total).isEqualTo(Money(27000))
        }

        @Test
        fun `OrderAmount 생성 - 할인 없이`() {
            val specs = createItemSpecs()
            val amount = OrderAmount.from(specs, discount = Money.ZERO)

            assertThat(amount.total).isEqualTo(Money(25000))
        }

        @Test
        fun `OrderAmount 생성 - 할인이 소계를 초과하면 예외가 발생한다`() {
            val specs = createItemSpecs()

            assertThatThrownBy { OrderAmount.from(specs, discount = Money(30000)) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }
    }

    @Nested
    @DisplayName("OrderItemSpec")
    inner class OrderItemSpecTest {

        @Test
        fun `OrderItemSpec 생성 - 정상 케이스`() {
            val spec = OrderItemSpec(
                skuId = 1L,
                productId = 1L,
                brandId = 1L,
                titleSnapshot = "상품",
                optionSnapshot = "옵션",
                quantity = 3,
                unitPrice = Money(10000),
            )

            assertThat(spec.totalPrice()).isEqualTo(Money(30000))
        }

        @Test
        fun `OrderItemSpec 생성 - 수량이 0이면 예외가 발생한다`() {
            assertThatThrownBy {
                OrderItemSpec(1L, 1L, 1L, "상품", null, 0, Money(10000))
            }.isInstanceOf(BadRequestException::class.java)
        }

        @Test
        fun `OrderItemSpec 생성 - 수량이 음수이면 예외가 발생한다`() {
            assertThatThrownBy {
                OrderItemSpec(1L, 1L, 1L, "상품", null, -1, Money(10000))
            }.isInstanceOf(BadRequestException::class.java)
        }

        @Test
        fun `OrderItemSpec totalPrice 계산`() {
            val spec = OrderItemSpec(1L, 1L, 1L, "상품", null, 5, Money(2000))

            assertThat(spec.totalPrice()).isEqualTo(Money(10000))
        }
    }

    @Nested
    @DisplayName("ShippingAddress")
    inner class ShippingAddressTest {

        @Test
        fun `ShippingAddress 생성`() {
            val address = ShippingAddress(
                recipient = "홍길동",
                phone = "010-1234-5678",
                zipCode = "12345",
                address = "서울시 강남구",
                addressDetail = "101호",
            )

            assertThat(address.recipient).isEqualTo("홍길동")
            assertThat(address.phone).isEqualTo("010-1234-5678")
            assertThat(address.zipCode).isEqualTo("12345")
            assertThat(address.address).isEqualTo("서울시 강남구")
            assertThat(address.addressDetail).isEqualTo("101호")
        }
    }

    @Nested
    @DisplayName("PricingSnapshot")
    inner class PricingSnapshotTest {

        @Test
        fun `PricingSnapshot 생성`() {
            val snapshot = PricingSnapshot(
                subtotal = 25000,
                discount = 1000,
                shippingFee = 3000,
                total = 27000,
                items = listOf(
                    PricingSnapshot.PricingSnapshotItem(1L, 10000, 2, 20000),
                    PricingSnapshot.PricingSnapshotItem(2L, 5000, 1, 5000),
                ),
            )

            assertThat(snapshot.subtotal).isEqualTo(25000)
            assertThat(snapshot.currency).isEqualTo("KRW")
            assertThat(snapshot.items).hasSize(2)
        }
    }
}
