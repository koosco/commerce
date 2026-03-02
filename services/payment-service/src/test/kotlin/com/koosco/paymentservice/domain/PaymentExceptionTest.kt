package com.koosco.paymentservice.domain

import com.koosco.paymentservice.domain.exception.DuplicatePaymentException
import com.koosco.paymentservice.domain.exception.PaymentBusinessException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Payment 예외 테스트")
class PaymentExceptionTest {

    @Nested
    @DisplayName("PaymentBusinessException")
    inner class PaymentBusinessExceptionTest {

        @Test
        fun `PaymentBusinessException을 생성할 수 있다`() {
            val exception = PaymentBusinessException()

            assertThat(exception).isInstanceOf(RuntimeException::class.java)
        }
    }

    @Nested
    @DisplayName("DuplicatePaymentException")
    inner class DuplicatePaymentExceptionTest {

        @Test
        fun `DuplicatePaymentException을 생성할 수 있다`() {
            val exception = DuplicatePaymentException()

            assertThat(exception).isInstanceOf(RuntimeException::class.java)
        }
    }
}
