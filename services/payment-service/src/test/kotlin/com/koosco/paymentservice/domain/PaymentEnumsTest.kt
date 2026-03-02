package com.koosco.paymentservice.domain

import com.koosco.paymentservice.domain.entity.PaymentTransactionStatus
import com.koosco.paymentservice.domain.entity.PaymentTransactionType
import com.koosco.paymentservice.domain.enums.PaymentStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Payment 열거형 테스트")
class PaymentEnumsTest {

    @Nested
    @DisplayName("PaymentStatus")
    inner class PaymentStatusTest {

        @Test
        fun `모든 PaymentStatus 값이 존재한다`() {
            val statuses = PaymentStatus.entries

            assertThat(statuses).containsExactlyInAnyOrder(
                PaymentStatus.READY,
                PaymentStatus.APPROVED,
                PaymentStatus.PARTIALLY_CANCELED,
                PaymentStatus.FAILED,
                PaymentStatus.CANCELED,
            )
        }
    }

    @Nested
    @DisplayName("PaymentTransactionType")
    inner class PaymentTransactionTypeTest {

        @Test
        fun `모든 PaymentTransactionType 값이 존재한다`() {
            val types = PaymentTransactionType.entries

            assertThat(types).containsExactlyInAnyOrder(
                PaymentTransactionType.APPROVAL,
                PaymentTransactionType.CANCEL,
            )
        }
    }

    @Nested
    @DisplayName("PaymentTransactionStatus")
    inner class PaymentTransactionStatusTest {

        @Test
        fun `모든 PaymentTransactionStatus 값이 존재한다`() {
            val statuses = PaymentTransactionStatus.entries

            assertThat(statuses).containsExactlyInAnyOrder(
                PaymentTransactionStatus.SUCCESS,
                PaymentTransactionStatus.FAILED,
            )
        }
    }
}
