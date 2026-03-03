package com.koosco.orderservice.domain.entity

import com.koosco.orderservice.domain.enums.OrderCancelReason
import com.koosco.orderservice.domain.enums.OrderStatus
import com.koosco.orderservice.domain.exception.InvalidOrderStatus
import com.koosco.orderservice.domain.exception.PaymentMisMatch
import com.koosco.orderservice.domain.vo.Money
import com.koosco.orderservice.domain.vo.OrderAmount
import com.koosco.orderservice.domain.vo.OrderItemSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Order 도메인 테스트")
class OrderTest {

    private fun createItemSpecs(count: Int = 1): List<OrderItemSpec> = (1..count).map { i ->
        OrderItemSpec(
            skuId = i.toLong(),
            productId = i.toLong(),
            brandId = 1L,
            titleSnapshot = "상품 $i",
            optionSnapshot = null,
            quantity = 2,
            unitPrice = Money(10000),
        )
    }

    private fun createOrderAmount(itemSpecs: List<OrderItemSpec>): OrderAmount = OrderAmount.from(
        itemSpecs = itemSpecs,
        discount = Money.ZERO,
        shippingFee = Money.ZERO,
    )

    private fun createOrder(status: OrderStatus = OrderStatus.CREATED): Order {
        val itemSpecs = createItemSpecs()
        val amount = createOrderAmount(itemSpecs)
        val order = Order.create(
            orderNo = "ORD-20260101-TEST",
            userId = 1L,
            itemSpecs = itemSpecs,
            amount = amount,
            shippingAddressSnapshot = "{}",
        )
        // Advance to the desired status
        when (status) {
            OrderStatus.CREATED -> {}
            OrderStatus.RESERVED -> order.markReserved()
            OrderStatus.PAYMENT_CREATED -> {
                order.markReserved()
                order.markPaymentCreated()
            }
            OrderStatus.PAYMENT_PENDING -> {
                order.markPaymentPending()
            }
            OrderStatus.PAID -> {
                order.markPaymentPending()
                order.markPaid(amount.total)
            }
            OrderStatus.CONFIRMED -> {
                order.markPaymentPending()
                order.markPaid(amount.total)
                order.confirmStock()
            }
            else -> {}
        }
        return order
    }

    @Nested
    @DisplayName("Order.create")
    inner class Create {

        @Test
        fun `주문 생성 시 CREATED 상태로 생성된다`() {
            val itemSpecs = createItemSpecs(2)
            val amount = createOrderAmount(itemSpecs)

            val order = Order.create(
                orderNo = "ORD-20260101-ABCDEF",
                userId = 1L,
                itemSpecs = itemSpecs,
                amount = amount,
                shippingAddressSnapshot = "{}",
                pricingSnapshot = "{\"test\": true}",
            )

            assertThat(order.status).isEqualTo(OrderStatus.CREATED)
            assertThat(order.orderNo).isEqualTo("ORD-20260101-ABCDEF")
            assertThat(order.userId).isEqualTo(1L)
            assertThat(order.items).hasSize(2)
            assertThat(order.subtotalAmount).isEqualTo(amount.subtotal)
            assertThat(order.totalAmount).isEqualTo(amount.total)
            assertThat(order.placedAt).isNotNull()
            assertThat(order.pricingSnapshot).isNotNull()
        }
    }

    @Nested
    @DisplayName("markReserved - CREATED/PAYMENT_CREATED -> RESERVED")
    inner class MarkReserved {

        @Test
        fun `CREATED 상태에서 RESERVED로 전이한다`() {
            val order = createOrder(OrderStatus.CREATED)

            order.markReserved()

            assertThat(order.status).isEqualTo(OrderStatus.RESERVED)
        }

        @Test
        fun `PAYMENT_CREATED 상태에서 RESERVED로 전이한다`() {
            val order = createOrder(OrderStatus.PAYMENT_CREATED)

            order.markReserved()

            assertThat(order.status).isEqualTo(OrderStatus.RESERVED)
        }

        @Test
        fun `PAID 상태에서 markReserved 호출 시 예외가 발생한다`() {
            val order = createOrder(OrderStatus.PAID)

            assertThatThrownBy { order.markReserved() }
                .isInstanceOf(InvalidOrderStatus::class.java)
        }
    }

    @Nested
    @DisplayName("markPaymentCreated - RESERVED/CREATED -> PAYMENT_CREATED")
    inner class MarkPaymentCreated {

        @Test
        fun `RESERVED 상태에서 PAYMENT_CREATED로 전이한다`() {
            val order = createOrder(OrderStatus.RESERVED)

            order.markPaymentCreated()

            assertThat(order.status).isEqualTo(OrderStatus.PAYMENT_CREATED)
        }

        @Test
        fun `CREATED 상태에서 PAYMENT_CREATED로 전이한다`() {
            val order = createOrder(OrderStatus.CREATED)

            order.markPaymentCreated()

            assertThat(order.status).isEqualTo(OrderStatus.PAYMENT_CREATED)
        }

        @Test
        fun `PAID 상태에서 markPaymentCreated 호출 시 예외가 발생한다`() {
            val order = createOrder(OrderStatus.PAID)

            assertThatThrownBy { order.markPaymentCreated() }
                .isInstanceOf(InvalidOrderStatus::class.java)
        }
    }

    @Nested
    @DisplayName("markPaymentPending - CREATED/RESERVED -> PAYMENT_PENDING")
    inner class MarkPaymentPending {

        @Test
        fun `CREATED 상태에서 PAYMENT_PENDING으로 전이한다`() {
            val order = createOrder(OrderStatus.CREATED)

            order.markPaymentPending()

            assertThat(order.status).isEqualTo(OrderStatus.PAYMENT_PENDING)
        }

        @Test
        fun `RESERVED 상태에서 PAYMENT_PENDING으로 전이한다`() {
            val order = createOrder(OrderStatus.RESERVED)

            order.markPaymentPending()

            assertThat(order.status).isEqualTo(OrderStatus.PAYMENT_PENDING)
        }

        @Test
        fun `PAID 상태에서 markPaymentPending 호출 시 예외가 발생한다`() {
            val order = createOrder(OrderStatus.PAID)

            assertThatThrownBy { order.markPaymentPending() }
                .isInstanceOf(InvalidOrderStatus::class.java)
        }
    }

    @Nested
    @DisplayName("markPaid - PAYMENT_PENDING -> PAID")
    inner class MarkPaid {

        @Test
        fun `PAYMENT_PENDING 상태에서 결제 금액이 일치하면 PAID로 전이한다`() {
            val order = createOrder(OrderStatus.PAYMENT_PENDING)

            order.markPaid(order.totalAmount)

            assertThat(order.status).isEqualTo(OrderStatus.PAID)
            assertThat(order.paidAt).isNotNull()
        }

        @Test
        fun `결제 금액이 일치하지 않으면 PaymentMisMatch 예외가 발생한다`() {
            val order = createOrder(OrderStatus.PAYMENT_PENDING)

            assertThatThrownBy { order.markPaid(Money(1)) }
                .isInstanceOf(PaymentMisMatch::class.java)
        }

        @Test
        fun `CREATED 상태에서 markPaid 호출 시 예외가 발생한다`() {
            val order = createOrder(OrderStatus.CREATED)

            assertThatThrownBy { order.markPaid(order.totalAmount) }
                .isInstanceOf(InvalidOrderStatus::class.java)
        }
    }

    @Nested
    @DisplayName("confirmStock - PAID -> CONFIRMED")
    inner class ConfirmStock {

        @Test
        fun `PAID 상태에서 CONFIRMED로 전이한다`() {
            val order = createOrder(OrderStatus.PAID)

            order.confirmStock()

            assertThat(order.status).isEqualTo(OrderStatus.CONFIRMED)
        }

        @Test
        fun `CREATED 상태에서 confirmStock 호출 시 예외가 발생한다`() {
            val order = createOrder(OrderStatus.CREATED)

            assertThatThrownBy { order.confirmStock() }
                .isInstanceOf(InvalidOrderStatus::class.java)
        }
    }

    @Nested
    @DisplayName("cancel - PAYMENT_PENDING -> CANCELLED")
    inner class Cancel {

        @Test
        fun `PAYMENT_PENDING 상태에서 CANCELLED로 전이한다`() {
            val order = createOrder(OrderStatus.PAYMENT_PENDING)

            order.cancel(OrderCancelReason.USER_REQUEST)

            assertThat(order.status).isEqualTo(OrderStatus.CANCELLED)
            assertThat(order.canceledAt).isNotNull()
        }

        @Test
        fun `CREATED 상태에서 cancel 호출 시 예외가 발생한다`() {
            val order = createOrder(OrderStatus.CREATED)

            assertThatThrownBy { order.cancel(OrderCancelReason.USER_REQUEST) }
                .isInstanceOf(InvalidOrderStatus::class.java)
        }
    }

    @Nested
    @DisplayName("markFailed - CREATED -> FAILED")
    inner class MarkFailed {

        @Test
        fun `CREATED 상태에서 FAILED로 전이한다`() {
            val order = createOrder(OrderStatus.CREATED)

            order.markFailed(OrderCancelReason.STOCK_RESERVATION_FAILED)

            assertThat(order.status).isEqualTo(OrderStatus.FAILED)
        }

        @Test
        fun `RESERVED 상태에서 markFailed 호출 시 예외가 발생한다`() {
            val order = createOrder(OrderStatus.RESERVED)

            assertThatThrownBy { order.markFailed(OrderCancelReason.STOCK_RESERVATION_FAILED) }
                .isInstanceOf(InvalidOrderStatus::class.java)
        }
    }

    @Nested
    @DisplayName("cancelByStockConfirmFailure - PAID -> CANCELLED")
    inner class CancelByStockConfirmFailure {

        @Test
        fun `PAID 상태에서 CANCELLED로 전이한다`() {
            val order = createOrder(OrderStatus.PAID)

            order.cancelByStockConfirmFailure()

            assertThat(order.status).isEqualTo(OrderStatus.CANCELLED)
            assertThat(order.canceledAt).isNotNull()
        }

        @Test
        fun `CREATED 상태에서 cancelByStockConfirmFailure 호출 시 예외가 발생한다`() {
            val order = createOrder(OrderStatus.CREATED)

            assertThatThrownBy { order.cancelByStockConfirmFailure() }
                .isInstanceOf(InvalidOrderStatus::class.java)
        }
    }

    @Nested
    @DisplayName("refundItem - PAID/CONFIRMED/PARTIALLY_REFUNDED -> PARTIALLY_REFUNDED/REFUNDED")
    inner class RefundItem {

        private fun createOrderWithItems(): Order {
            val specs = listOf(
                OrderItemSpec(1L, 1L, 1L, "상품1", null, 1, Money(10000)),
                OrderItemSpec(2L, 2L, 1L, "상품2", null, 1, Money(20000)),
            )
            val amount = OrderAmount.from(specs, Money.ZERO)
            val order = Order.create(
                orderNo = "ORD-REFUND-TEST",
                userId = 1L,
                itemSpecs = specs,
                amount = amount,
                shippingAddressSnapshot = "{}",
            )
            order.markPaymentPending()
            order.markPaid(amount.total)
            return order
        }

        @Test
        fun `PAID 상태에서 일부 아이템 환불 시 PARTIALLY_REFUNDED로 전이한다`() {
            val order = createOrderWithItems()
            val firstItemId = order.items[0].id

            // In test, id is null since not persisted. Use reflection to set id
            setItemId(order.items[0], 1L)
            setItemId(order.items[1], 2L)

            val refundAmount = order.refundItem(1L)

            assertThat(refundAmount).isEqualTo(Money(10000))
            assertThat(order.status).isEqualTo(OrderStatus.PARTIALLY_REFUNDED)
            assertThat(order.refundedAmount).isEqualTo(Money(10000))
        }

        @Test
        fun `전체 아이템 환불 시 REFUNDED로 전이한다`() {
            val order = createOrderWithItems()
            setItemId(order.items[0], 1L)
            setItemId(order.items[1], 2L)

            order.refundItem(1L)
            order.refundItem(2L)

            assertThat(order.status).isEqualTo(OrderStatus.REFUNDED)
            assertThat(order.refundedAmount).isEqualTo(Money(30000))
        }

        @Test
        fun `존재하지 않는 아이템 환불 시 예외가 발생한다`() {
            val order = createOrderWithItems()

            assertThatThrownBy { order.refundItem(999L) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `CREATED 상태에서 refundItem 호출 시 예외가 발생한다`() {
            val order = createOrder(OrderStatus.CREATED)

            assertThatThrownBy { order.refundItem(1L) }
                .isInstanceOf(InvalidOrderStatus::class.java)
        }

        private fun setItemId(item: OrderItem, id: Long) {
            val field = OrderItem::class.java.getDeclaredField("id")
            field.isAccessible = true
            field.set(item, id)
        }
    }

    @Nested
    @DisplayName("refundAll")
    inner class RefundAll {

        @Test
        fun `여러 아이템을 한번에 환불한다`() {
            val specs = listOf(
                OrderItemSpec(1L, 1L, 1L, "상품1", null, 1, Money(10000)),
                OrderItemSpec(2L, 2L, 1L, "상품2", null, 1, Money(20000)),
            )
            val amount = OrderAmount.from(specs, Money.ZERO)
            val order = Order.create(
                orderNo = "ORD-REFUNDALL-TEST",
                userId = 1L,
                itemSpecs = specs,
                amount = amount,
                shippingAddressSnapshot = "{}",
            )
            order.markPaymentPending()
            order.markPaid(amount.total)

            val field = OrderItem::class.java.getDeclaredField("id")
            field.isAccessible = true
            field.set(order.items[0], 1L)
            field.set(order.items[1], 2L)

            val totalRefund = order.refundAll(listOf(1L, 2L))

            assertThat(totalRefund).isEqualTo(Money(30000))
            assertThat(order.status).isEqualTo(OrderStatus.REFUNDED)
        }
    }
}
