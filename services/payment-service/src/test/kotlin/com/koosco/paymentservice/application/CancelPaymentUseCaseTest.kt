package com.koosco.paymentservice.application

import com.koosco.common.core.event.IntegrationEventProducer
import com.koosco.common.core.exception.BadRequestException
import com.koosco.common.core.exception.NotFoundException
import com.koosco.paymentservice.application.command.CancelPaymentCommand
import com.koosco.paymentservice.application.command.PaymentApproveResult
import com.koosco.paymentservice.application.port.PaymentGateway
import com.koosco.paymentservice.application.port.PaymentRepository
import com.koosco.paymentservice.application.usecase.CancelPaymentUseCase
import com.koosco.paymentservice.contract.outbound.payment.PaymentCanceledEvent
import com.koosco.paymentservice.domain.entity.Payment
import com.koosco.paymentservice.domain.entity.PaymentTransaction
import com.koosco.paymentservice.domain.entity.PaymentTransactionStatus
import com.koosco.paymentservice.domain.entity.PaymentTransactionType
import com.koosco.paymentservice.domain.vo.Money
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID

@ExtendWith(MockitoExtension::class)
@DisplayName("CancelPaymentUseCase 테스트")
class CancelPaymentUseCaseTest {

    @Mock
    private lateinit var paymentRepository: PaymentRepository

    @Mock
    private lateinit var paymentGateway: PaymentGateway

    @Mock
    private lateinit var integrationEventProducer: IntegrationEventProducer

    @InjectMocks
    private lateinit var cancelPaymentUseCase: CancelPaymentUseCase

    private val paymentId = UUID.randomUUID()

    private fun createApprovedPayment(amount: Long = 50000L): Payment {
        val payment = Payment(paymentId = paymentId, orderId = 1L, userId = 100L, amount = Money(amount))
        val txn = PaymentTransaction(
            payment = payment,
            type = PaymentTransactionType.APPROVAL,
            status = PaymentTransactionStatus.SUCCESS,
            pgTransactionId = "pg-approval-123",
            amount = Money(amount),
        )
        payment.approve(txn)
        return payment
    }

    @Nested
    @DisplayName("전액 취소 성공")
    inner class FullCancelSuccess {

        @Test
        fun `전액 취소 시 PaymentCanceledEvent를 발행한다`() {
            val payment = createApprovedPayment()
            val command = CancelPaymentCommand(paymentId = paymentId, cancelAmount = 50000L)

            whenever(paymentRepository.findByPaymentId(paymentId)).thenReturn(payment)
            whenever(paymentGateway.cancel(eq(paymentId), eq("pg-approval-123"), any())).thenReturn(
                PaymentApproveResult(success = true, pgTransactionId = "pg-cancel-456"),
            )
            whenever(paymentRepository.save(any())).thenReturn(payment)

            cancelPaymentUseCase.execute(command)

            verify(paymentRepository).save(any())
            verify(integrationEventProducer).publish(
                argThat<PaymentCanceledEvent> {
                    this is PaymentCanceledEvent &&
                        this.orderId == 1L &&
                        this.canceledAmount == 50000L &&
                        this.isFullyCanceled
                },
            )
        }
    }

    @Nested
    @DisplayName("부분 취소 성공")
    inner class PartialCancelSuccess {

        @Test
        fun `부분 취소 시 isFullyCanceled가 false인 이벤트를 발행한다`() {
            val payment = createApprovedPayment()
            val command = CancelPaymentCommand(paymentId = paymentId, cancelAmount = 20000L)

            whenever(paymentRepository.findByPaymentId(paymentId)).thenReturn(payment)
            whenever(paymentGateway.cancel(eq(paymentId), eq("pg-approval-123"), any())).thenReturn(
                PaymentApproveResult(success = true, pgTransactionId = "pg-cancel-789"),
            )
            whenever(paymentRepository.save(any())).thenReturn(payment)

            cancelPaymentUseCase.execute(command)

            verify(integrationEventProducer).publish(
                argThat<PaymentCanceledEvent> {
                    this is PaymentCanceledEvent &&
                        this.canceledAmount == 20000L &&
                        this.totalCanceledAmount == 20000L &&
                        this.remainingAmount == 30000L &&
                        !this.isFullyCanceled
                },
            )
        }
    }

    @Nested
    @DisplayName("취소 실패")
    inner class CancelFail {

        @Test
        fun `결제 정보가 없으면 NotFoundException이 발생한다`() {
            val command = CancelPaymentCommand(paymentId = paymentId, cancelAmount = 50000L)
            whenever(paymentRepository.findByPaymentId(paymentId)).thenReturn(null)

            assertThatThrownBy { cancelPaymentUseCase.execute(command) }
                .isInstanceOf(NotFoundException::class.java)
        }

        @Test
        fun `PG 취소 실패 시 BadRequestException이 발생한다`() {
            val payment = createApprovedPayment()
            val command = CancelPaymentCommand(paymentId = paymentId, cancelAmount = 50000L)

            whenever(paymentRepository.findByPaymentId(paymentId)).thenReturn(payment)
            whenever(paymentGateway.cancel(eq(paymentId), eq("pg-approval-123"), any())).thenReturn(
                PaymentApproveResult(success = false, pgTransactionId = null),
            )

            assertThatThrownBy { cancelPaymentUseCase.execute(command) }
                .isInstanceOf(BadRequestException::class.java)

            verify(integrationEventProducer, never()).publish(any())
        }
    }
}
