package com.koosco.orderservice.domain.exception

import com.koosco.orderservice.common.error.OrderErrorCode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Order 예외 테스트")
class OrderExceptionsTest {

    @Nested
    @DisplayName("InvalidOrderStatus")
    inner class InvalidOrderStatusTest {

        @Test
        fun `기본 메시지로 생성`() {
            val exception = InvalidOrderStatus()

            assertThat(exception.message).isEqualTo("Invalid order status")
            assertThat(exception.errorCode).isEqualTo(OrderErrorCode.INVALID_ORDER_STATUS)
        }

        @Test
        fun `커스텀 메시지로 생성`() {
            val exception = InvalidOrderStatus("커스텀 에러 메시지")

            assertThat(exception.message).isEqualTo("커스텀 에러 메시지")
        }
    }

    @Nested
    @DisplayName("PaymentMisMatch")
    inner class PaymentMisMatchTest {

        @Test
        fun `기본 메시지로 생성`() {
            val exception = PaymentMisMatch()

            assertThat(exception.message).isEqualTo("Payment amount mismatched")
            assertThat(exception.errorCode).isEqualTo(OrderErrorCode.INVALID_PAYMENT_STATUS)
        }

        @Test
        fun `커스텀 메시지로 생성`() {
            val exception = PaymentMisMatch("결제 금액 불일치")

            assertThat(exception.message).isEqualTo("결제 금액 불일치")
        }
    }
}
