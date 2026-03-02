package com.koosco.paymentservice.api

import com.koosco.common.core.exception.NotFoundException
import com.koosco.paymentservice.application.command.CancelPaymentCommand
import com.koosco.paymentservice.application.command.PaymentApproveCommand
import com.koosco.paymentservice.application.port.PaymentRepository
import com.koosco.paymentservice.application.usecase.ApprovePaymentUseCase
import com.koosco.paymentservice.application.usecase.CancelPaymentUseCase
import com.koosco.paymentservice.domain.entity.Payment
import com.koosco.paymentservice.domain.vo.Money
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.argThat
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID

@ExtendWith(MockitoExtension::class)
@DisplayName("WidgetController 테스트")
class WidgetControllerTest {

    @Mock
    private lateinit var approvePaymentUseCase: ApprovePaymentUseCase

    @Mock
    private lateinit var cancelPaymentUseCase: CancelPaymentUseCase

    @Mock
    private lateinit var paymentRepository: PaymentRepository

    @InjectMocks
    private lateinit var widgetController: WidgetController

    @Nested
    @DisplayName("결제 승인 (confirmPayment)")
    inner class ConfirmPayment {

        @Test
        fun `결제 승인 요청이 성공하면 success true를 반환한다`() {
            val payment = Payment(orderId = 1L, userId = 100L, amount = Money(50000L))
            whenever(paymentRepository.findByOrderId(1L)).thenReturn(payment)

            val request = PaymentConfirmRequest(
                orderId = 1L,
                amount = "50000",
                paymentKey = "pk-123",
            )

            val result = widgetController.confirmPayment(request)

            assertThat(result["success"]).isEqualTo(true)
            verify(approvePaymentUseCase).execute(
                eq(payment.paymentId),
                argThat<PaymentApproveCommand> {
                    this.paymentId == payment.paymentId &&
                        this.orderId == 1L &&
                        this.amount == Money(50000L)
                },
                eq("pk-123"),
            )
        }

        @Test
        fun `결제 정보가 없으면 NotFoundException이 발생한다`() {
            whenever(paymentRepository.findByOrderId(999L)).thenReturn(null)

            val request = PaymentConfirmRequest(
                orderId = 999L,
                amount = "50000",
                paymentKey = "pk-123",
            )

            assertThatThrownBy { widgetController.confirmPayment(request) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("결제 취소 (cancelPayment)")
    inner class CancelPayment {

        @Test
        fun `결제 취소 요청이 성공하면 success true를 반환한다`() {
            val paymentId = UUID.randomUUID()
            val request = PaymentCancelRequest(cancelAmount = 30000L)

            val result = widgetController.cancelPayment(paymentId, request)

            assertThat(result["success"]).isEqualTo(true)
            verify(cancelPaymentUseCase).execute(
                argThat<CancelPaymentCommand> {
                    this.paymentId == paymentId && this.cancelAmount == 30000L
                },
            )
        }
    }
}
