package com.koosco.paymentservice.application

import com.koosco.common.core.event.IntegrationEventProducer
import com.koosco.common.core.exception.NotFoundException
import com.koosco.paymentservice.application.command.PaymentApproveCommand
import com.koosco.paymentservice.application.command.PaymentApproveResult
import com.koosco.paymentservice.application.port.IdempotencyRepository
import com.koosco.paymentservice.application.port.PaymentGateway
import com.koosco.paymentservice.application.port.PaymentRepository
import com.koosco.paymentservice.application.usecase.ApprovePaymentUseCase
import com.koosco.paymentservice.contract.outbound.payment.PaymentCompletedEvent
import com.koosco.paymentservice.contract.outbound.payment.PaymentFailedEvent
import com.koosco.paymentservice.domain.entity.Payment
import com.koosco.paymentservice.domain.entity.PaymentTransaction
import com.koosco.paymentservice.domain.entity.PaymentTransactionStatus
import com.koosco.paymentservice.domain.entity.PaymentTransactionType
import com.koosco.paymentservice.domain.enums.PaymentStatus
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
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID

@ExtendWith(MockitoExtension::class)
@DisplayName("ApprovePaymentUseCase 테스트")
class ApprovePaymentUseCaseTest {

    @Mock
    private lateinit var idempotencyRepository: IdempotencyRepository

    @Mock
    private lateinit var paymentRepository: PaymentRepository

    @Mock
    private lateinit var paymentGateway: PaymentGateway

    @Mock
    private lateinit var integrationEventProducer: IntegrationEventProducer

    @InjectMocks
    private lateinit var approvePaymentUseCase: ApprovePaymentUseCase

    private val paymentId = UUID.randomUUID()
    private val idempotencyKey = "idem-key-123"

    private fun createPayment(status: PaymentStatus = PaymentStatus.READY): Payment {
        val payment = Payment(paymentId = paymentId, orderId = 1L, userId = 100L, amount = Money(50000L))
        if (status == PaymentStatus.APPROVED) {
            val txn = PaymentTransaction(
                payment = payment,
                type = PaymentTransactionType.APPROVAL,
                status = PaymentTransactionStatus.SUCCESS,
                pgTransactionId = "pg-old",
                amount = payment.amount,
            )
            payment.approve(txn)
        }
        return payment
    }

    private fun createCommand(): PaymentApproveCommand = PaymentApproveCommand(
        paymentId = paymentId,
        orderId = 1L,
        amount = Money(50000L),
    )

    @Nested
    @DisplayName("결제 승인 성공")
    inner class ApproveSuccess {

        @Test
        fun `PG 승인 성공 시 상태를 APPROVED로 변경하고 PaymentCompletedEvent를 발행한다`() {
            val payment = createPayment()
            whenever(paymentRepository.findByPaymentId(paymentId)).thenReturn(payment)
            whenever(paymentGateway.approve(any())).thenReturn(
                PaymentApproveResult(success = true, pgTransactionId = "pg-txn-456"),
            )
            whenever(paymentRepository.save(any())).thenReturn(payment)

            approvePaymentUseCase.execute(paymentId, createCommand(), idempotencyKey)

            verify(paymentRepository).save(any())
            verify(integrationEventProducer).publish(
                argThat<PaymentCompletedEvent> {
                    this is PaymentCompletedEvent &&
                        this.orderId == 1L &&
                        this.pgTransactionId == "pg-txn-456" &&
                        this.paidAmount == 50000L
                },
            )
        }
    }

    @Nested
    @DisplayName("결제 승인 실패")
    inner class ApproveFail {

        @Test
        fun `PG 승인 실패 시 상태를 FAILED로 변경하고 PaymentFailedEvent를 발행한다`() {
            val payment = createPayment()
            whenever(paymentRepository.findByPaymentId(paymentId)).thenReturn(payment)
            whenever(paymentGateway.approve(any())).thenReturn(
                PaymentApproveResult(
                    success = false,
                    pgTransactionId = "pg-txn-789",
                    failureReason = "INSUFFICIENT_BALANCE",
                ),
            )
            whenever(paymentRepository.save(any())).thenReturn(payment)

            approvePaymentUseCase.execute(paymentId, createCommand(), idempotencyKey)

            verify(paymentRepository).save(any())
            verify(integrationEventProducer).publish(
                argThat<PaymentFailedEvent> {
                    this is PaymentFailedEvent &&
                        this.orderId == 1L &&
                        this.reason == "INSUFFICIENT_BALANCE"
                },
            )
        }

        @Test
        fun `PG 승인 실패 시 failureReason이 null이면 기본 메시지를 사용한다`() {
            val payment = createPayment()
            whenever(paymentRepository.findByPaymentId(paymentId)).thenReturn(payment)
            whenever(paymentGateway.approve(any())).thenReturn(
                PaymentApproveResult(success = false, pgTransactionId = null, failureReason = null),
            )
            whenever(paymentRepository.save(any())).thenReturn(payment)

            approvePaymentUseCase.execute(paymentId, createCommand(), idempotencyKey)

            verify(integrationEventProducer).publish(
                argThat<PaymentFailedEvent> {
                    this is PaymentFailedEvent && this.reason == "PAYMENT_FAILED"
                },
            )
        }
    }

    @Nested
    @DisplayName("결제 조회 실패")
    inner class PaymentNotFound {

        @Test
        fun `결제 정보가 없으면 NotFoundException이 발생한다`() {
            whenever(paymentRepository.findByPaymentId(paymentId)).thenReturn(null)

            assertThatThrownBy {
                approvePaymentUseCase.execute(paymentId, createCommand(), idempotencyKey)
            }.isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("멱등성 처리")
    inner class Idempotency {

        @Test
        fun `이미 승인된 결제는 PG 호출 없이 반환한다`() {
            val payment = createPayment(PaymentStatus.APPROVED)
            whenever(paymentRepository.findByPaymentId(paymentId)).thenReturn(payment)

            approvePaymentUseCase.execute(paymentId, createCommand(), idempotencyKey)

            verify(paymentGateway, never()).approve(any())
            verify(integrationEventProducer, never()).publish(any())
        }
    }
}
