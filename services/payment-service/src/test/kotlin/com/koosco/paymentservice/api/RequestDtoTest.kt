package com.koosco.paymentservice.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("API Request DTO 테스트")
class RequestDtoTest {

    @Nested
    @DisplayName("PaymentConfirmRequest")
    inner class PaymentConfirmRequestTest {

        @Test
        fun `생성 및 필드 접근이 가능하다`() {
            val request = PaymentConfirmRequest(
                orderId = 1L,
                amount = "50000",
                paymentKey = "pk-123",
            )

            assertThat(request.orderId).isEqualTo(1L)
            assertThat(request.amount).isEqualTo("50000")
            assertThat(request.paymentKey).isEqualTo("pk-123")
        }

        @Test
        fun `동일한 값의 요청은 동일하다`() {
            val req1 = PaymentConfirmRequest(orderId = 1L, amount = "50000", paymentKey = "pk-123")
            val req2 = PaymentConfirmRequest(orderId = 1L, amount = "50000", paymentKey = "pk-123")

            assertThat(req1).isEqualTo(req2)
            assertThat(req1.hashCode()).isEqualTo(req2.hashCode())
        }

        @Test
        fun `toString이 올바르게 동작한다`() {
            val request = PaymentConfirmRequest(orderId = 1L, amount = "50000", paymentKey = "pk-123")

            assertThat(request.toString()).contains("orderId=1")
            assertThat(request.toString()).contains("amount=50000")
            assertThat(request.toString()).contains("paymentKey=pk-123")
        }

        @Test
        fun `copy를 사용하여 새 인스턴스를 생성할 수 있다`() {
            val original = PaymentConfirmRequest(orderId = 1L, amount = "50000", paymentKey = "pk-123")
            val copied = original.copy(orderId = 2L)

            assertThat(copied.orderId).isEqualTo(2L)
            assertThat(copied.amount).isEqualTo("50000")
        }
    }

    @Nested
    @DisplayName("PaymentCancelRequest")
    inner class PaymentCancelRequestTest {

        @Test
        fun `생성 및 필드 접근이 가능하다`() {
            val request = PaymentCancelRequest(cancelAmount = 30000L)

            assertThat(request.cancelAmount).isEqualTo(30000L)
        }

        @Test
        fun `동일한 값의 요청은 동일하다`() {
            val req1 = PaymentCancelRequest(cancelAmount = 30000L)
            val req2 = PaymentCancelRequest(cancelAmount = 30000L)

            assertThat(req1).isEqualTo(req2)
            assertThat(req1.hashCode()).isEqualTo(req2.hashCode())
        }

        @Test
        fun `toString이 올바르게 동작한다`() {
            val request = PaymentCancelRequest(cancelAmount = 30000L)

            assertThat(request.toString()).contains("cancelAmount=30000")
        }

        @Test
        fun `copy를 사용하여 새 인스턴스를 생성할 수 있다`() {
            val original = PaymentCancelRequest(cancelAmount = 30000L)
            val copied = original.copy(cancelAmount = 50000L)

            assertThat(copied.cancelAmount).isEqualTo(50000L)
        }
    }
}
