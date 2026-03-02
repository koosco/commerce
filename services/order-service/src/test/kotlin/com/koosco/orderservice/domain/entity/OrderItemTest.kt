package com.koosco.orderservice.domain.entity

import com.koosco.orderservice.domain.enums.OrderItemStatus
import com.koosco.orderservice.domain.vo.Money
import com.koosco.orderservice.domain.vo.OrderAmount
import com.koosco.orderservice.domain.vo.OrderItemSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("OrderItem 도메인 테스트")
class OrderItemTest {

    private fun createOrderWithItem(): Order {
        val spec = OrderItemSpec(
            skuId = 1L,
            productId = 10L,
            brandId = 100L,
            titleSnapshot = "테스트 상품",
            optionSnapshot = "옵션 정보",
            quantity = 3,
            unitPrice = Money(5000),
        )
        val amount = OrderAmount.from(listOf(spec), Money.ZERO)
        return Order.create(
            orderNo = "ORD-ITEM-TEST",
            userId = 1L,
            itemSpecs = listOf(spec),
            amount = amount,
            shippingAddressSnapshot = "{}",
        )
    }

    @Nested
    @DisplayName("OrderItem.create")
    inner class Create {

        @Test
        fun `주문 아이템 생성 시 ORDERED 상태로 생성된다`() {
            val order = createOrderWithItem()
            val item = order.items[0]

            assertThat(item.status).isEqualTo(OrderItemStatus.ORDERED)
            assertThat(item.skuId).isEqualTo(1L)
            assertThat(item.productId).isEqualTo(10L)
            assertThat(item.brandId).isEqualTo(100L)
            assertThat(item.titleSnapshot).isEqualTo("테스트 상품")
            assertThat(item.optionSnapshot).isEqualTo("옵션 정보")
            assertThat(item.qty).isEqualTo(3)
            assertThat(item.unitPrice).isEqualTo(Money(5000))
            assertThat(item.lineAmount).isEqualTo(Money(15000))
            assertThat(item.refundableAmount).isEqualTo(Money(15000))
        }
    }

    @Nested
    @DisplayName("refund")
    inner class Refund {

        @Test
        fun `ORDERED 상태의 아이템을 환불하면 REFUNDED 상태로 전이하고 환불 금액을 반환한다`() {
            val order = createOrderWithItem()
            val item = order.items[0]

            val refundAmount = item.refund()

            assertThat(refundAmount).isEqualTo(Money(15000))
            assertThat(item.status).isEqualTo(OrderItemStatus.REFUNDED)
        }

        @Test
        fun `이미 환불된 아이템을 재환불하면 예외가 발생한다`() {
            val order = createOrderWithItem()
            val item = order.items[0]
            item.refund()

            assertThatThrownBy { item.refund() }
                .isInstanceOf(IllegalStateException::class.java)
        }
    }
}
