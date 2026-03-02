package com.koosco.paymentservice.domain

import com.koosco.paymentservice.domain.entity.Payment
import com.koosco.paymentservice.domain.entity.PaymentTransaction
import com.koosco.paymentservice.domain.entity.PaymentTransactionStatus
import com.koosco.paymentservice.domain.entity.PaymentTransactionType
import com.koosco.paymentservice.domain.vo.Money
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("PaymentTransaction 엔티티 테스트")
class PaymentTransactionTest {

    private val payment = Payment(orderId = 1L, userId = 1L, amount = Money(10000L))

    @Nested
    @DisplayName("PaymentTransaction 생성")
    inner class Create {

        @Test
        fun `승인 성공 트랜잭션을 생성할 수 있다`() {
            val transaction = PaymentTransaction(
                payment = payment,
                type = PaymentTransactionType.APPROVAL,
                status = PaymentTransactionStatus.SUCCESS,
                pgTransactionId = "pg-123",
                amount = Money(10000L),
            )

            assertThat(transaction.type).isEqualTo(PaymentTransactionType.APPROVAL)
            assertThat(transaction.status).isEqualTo(PaymentTransactionStatus.SUCCESS)
            assertThat(transaction.pgTransactionId).isEqualTo("pg-123")
            assertThat(transaction.amount).isEqualTo(Money(10000L))
            assertThat(transaction.occurredAt).isNotNull()
        }

        @Test
        fun `취소 트랜잭션을 생성할 수 있다`() {
            val transaction = PaymentTransaction(
                payment = payment,
                type = PaymentTransactionType.CANCEL,
                status = PaymentTransactionStatus.SUCCESS,
                pgTransactionId = "pg-cancel-456",
                amount = Money(5000L),
            )

            assertThat(transaction.type).isEqualTo(PaymentTransactionType.CANCEL)
            assertThat(transaction.status).isEqualTo(PaymentTransactionStatus.SUCCESS)
        }

        @Test
        fun `pgTransactionId가 null일 수 있다`() {
            val transaction = PaymentTransaction(
                payment = payment,
                type = PaymentTransactionType.APPROVAL,
                status = PaymentTransactionStatus.FAILED,
                pgTransactionId = null,
                amount = Money(10000L),
            )

            assertThat(transaction.pgTransactionId).isNull()
        }
    }
}
