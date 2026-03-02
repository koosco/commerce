package com.koosco.paymentservice.application

import com.koosco.paymentservice.application.command.CancelPaymentByOrderCommand
import com.koosco.paymentservice.application.command.CancelPaymentCommand
import com.koosco.paymentservice.application.command.CreatePaymentCommand
import com.koosco.paymentservice.application.command.PaymentApproveCommand
import com.koosco.paymentservice.application.command.PaymentApproveResult
import com.koosco.paymentservice.domain.vo.Money
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID

@DisplayName("Command 및 Result 테스트")
class CommandTest {

    @Nested
    @DisplayName("CreatePaymentCommand")
    inner class CreatePaymentCommandTest {

        @Test
        fun `생성 및 필드 접근이 가능하다`() {
            val cmd = CreatePaymentCommand(orderId = 1L, userId = 2L, amount = 10000L)

            assertThat(cmd.orderId).isEqualTo(1L)
            assertThat(cmd.userId).isEqualTo(2L)
            assertThat(cmd.amount).isEqualTo(10000L)
        }

        @Test
        fun `동일한 값의 커맨드는 동일하다`() {
            val cmd1 = CreatePaymentCommand(orderId = 1L, userId = 2L, amount = 10000L)
            val cmd2 = CreatePaymentCommand(orderId = 1L, userId = 2L, amount = 10000L)

            assertThat(cmd1).isEqualTo(cmd2)
            assertThat(cmd1.hashCode()).isEqualTo(cmd2.hashCode())
        }
    }

    @Nested
    @DisplayName("PaymentApproveCommand")
    inner class PaymentApproveCommandTest {

        @Test
        fun `생성 및 필드 접근이 가능하다`() {
            val id = UUID.randomUUID()
            val cmd = PaymentApproveCommand(paymentId = id, orderId = 1L, amount = Money(50000L))

            assertThat(cmd.paymentId).isEqualTo(id)
            assertThat(cmd.orderId).isEqualTo(1L)
            assertThat(cmd.amount).isEqualTo(Money(50000L))
        }
    }

    @Nested
    @DisplayName("PaymentApproveResult")
    inner class PaymentApproveResultTest {

        @Test
        fun `성공 결과를 생성할 수 있다`() {
            val result = PaymentApproveResult(success = true, pgTransactionId = "pg-123")

            assertThat(result.success).isTrue()
            assertThat(result.pgTransactionId).isEqualTo("pg-123")
            assertThat(result.failureReason).isNull()
        }

        @Test
        fun `실패 결과를 생성할 수 있다`() {
            val result = PaymentApproveResult(
                success = false,
                pgTransactionId = null,
                failureReason = "CARD_DECLINED",
            )

            assertThat(result.success).isFalse()
            assertThat(result.pgTransactionId).isNull()
            assertThat(result.failureReason).isEqualTo("CARD_DECLINED")
        }
    }

    @Nested
    @DisplayName("CancelPaymentCommand")
    inner class CancelPaymentCommandTest {

        @Test
        fun `생성 및 필드 접근이 가능하다`() {
            val id = UUID.randomUUID()
            val cmd = CancelPaymentCommand(paymentId = id, cancelAmount = 30000L)

            assertThat(cmd.paymentId).isEqualTo(id)
            assertThat(cmd.cancelAmount).isEqualTo(30000L)
        }
    }

    @Nested
    @DisplayName("CancelPaymentByOrderCommand")
    inner class CancelPaymentByOrderCommandTest {

        @Test
        fun `생성 및 필드 접근이 가능하다`() {
            val cmd = CancelPaymentByOrderCommand(orderId = 1L, cancelAmount = 50000L)

            assertThat(cmd.orderId).isEqualTo(1L)
            assertThat(cmd.cancelAmount).isEqualTo(50000L)
        }
    }
}
