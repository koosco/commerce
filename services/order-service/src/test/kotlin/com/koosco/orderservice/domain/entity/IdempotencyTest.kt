package com.koosco.orderservice.domain.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("멱등성 엔티티 테스트")
class IdempotencyTest {

    @Nested
    @DisplayName("OrderIdempotency")
    inner class OrderIdempotencyTest {

        @Test
        fun `OrderIdempotency 생성`() {
            val idempotency = OrderIdempotency.create(
                userId = 1L,
                idempotencyKey = "key-123",
                orderId = 10L,
            )

            assertThat(idempotency.userId).isEqualTo(1L)
            assertThat(idempotency.idempotencyKey).isEqualTo("key-123")
            assertThat(idempotency.orderId).isEqualTo(10L)
            assertThat(idempotency.createdAt).isNotNull()
        }
    }

    @Nested
    @DisplayName("CartIdempotency")
    inner class CartIdempotencyTest {

        @Test
        fun `CartIdempotency 생성`() {
            val idempotency = CartIdempotency.create(
                userId = 1L,
                idempotencyKey = "cart-key-123",
                cartItemId = 5L,
            )

            assertThat(idempotency.userId).isEqualTo(1L)
            assertThat(idempotency.idempotencyKey).isEqualTo("cart-key-123")
            assertThat(idempotency.cartItemId).isEqualTo(5L)
            assertThat(idempotency.createdAt).isNotNull()
        }
    }

    @Nested
    @DisplayName("OrderEventIdempotency")
    inner class OrderEventIdempotencyTest {

        @Test
        fun `OrderEventIdempotency 생성`() {
            val idempotency = OrderEventIdempotency.create(
                messageId = "msg-123",
                action = OrderEventIdempotency.Companion.Actions.MARK_RESERVED,
                aggregateId = "order-1",
            )

            assertThat(idempotency.messageId).isEqualTo("msg-123")
            assertThat(idempotency.action).isEqualTo("MARK_RESERVED")
            assertThat(idempotency.aggregateId).isEqualTo("order-1")
            assertThat(idempotency.aggregateType).isEqualTo("Order")
            assertThat(idempotency.processedAt).isNotNull()
        }

        @Test
        fun `Actions 상수 확인`() {
            assertThat(OrderEventIdempotency.Companion.Actions.MARK_RESERVED).isEqualTo("MARK_RESERVED")
            assertThat(OrderEventIdempotency.Companion.Actions.MARK_PAYMENT_CREATED).isEqualTo("MARK_PAYMENT_CREATED")
            assertThat(OrderEventIdempotency.Companion.Actions.MARK_PAYMENT_PENDING).isEqualTo("MARK_PAYMENT_PENDING")
            assertThat(OrderEventIdempotency.Companion.Actions.MARK_PAID).isEqualTo("MARK_PAID")
            assertThat(OrderEventIdempotency.Companion.Actions.MARK_CONFIRMED).isEqualTo("MARK_CONFIRMED")
            assertThat(OrderEventIdempotency.Companion.Actions.CANCEL_BY_PAYMENT_FAILURE)
                .isEqualTo("CANCEL_BY_PAYMENT_FAILURE")
            assertThat(OrderEventIdempotency.Companion.Actions.MARK_FAILED_BY_STOCK_RESERVATION)
                .isEqualTo("MARK_FAILED_BY_STOCK_RESERVATION")
            assertThat(OrderEventIdempotency.Companion.Actions.CANCEL_BY_STOCK_CONFIRM_FAILURE)
                .isEqualTo("CANCEL_BY_STOCK_CONFIRM_FAILURE")
            assertThat(OrderEventIdempotency.Companion.Actions.MARK_REFUND_COMPLETED)
                .isEqualTo("MARK_REFUND_COMPLETED")
        }
    }
}
