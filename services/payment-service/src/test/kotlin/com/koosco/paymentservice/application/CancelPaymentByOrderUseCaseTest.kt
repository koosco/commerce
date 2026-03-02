package com.koosco.paymentservice.application

import com.koosco.common.core.exception.NotFoundException
import com.koosco.paymentservice.application.command.CancelPaymentByOrderCommand
import com.koosco.paymentservice.application.command.CancelPaymentCommand
import com.koosco.paymentservice.application.port.PaymentRepository
import com.koosco.paymentservice.application.usecase.CancelPaymentByOrderUseCase
import com.koosco.paymentservice.application.usecase.CancelPaymentUseCase
import com.koosco.paymentservice.domain.entity.Payment
import com.koosco.paymentservice.domain.vo.Money
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.argThat
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
@DisplayName("CancelPaymentByOrderUseCase 테스트")
class CancelPaymentByOrderUseCaseTest {

    @Mock
    private lateinit var paymentRepository: PaymentRepository

    @Mock
    private lateinit var cancelPaymentUseCase: CancelPaymentUseCase

    @InjectMocks
    private lateinit var cancelPaymentByOrderUseCase: CancelPaymentByOrderUseCase

    @Nested
    @DisplayName("주문 기반 결제 취소 성공")
    inner class Success {

        @Test
        fun `주문 ID로 결제를 찾아서 취소한다`() {
            val payment = Payment(orderId = 1L, userId = 100L, amount = Money(50000L))
            whenever(paymentRepository.findByOrderId(1L)).thenReturn(payment)

            val command = CancelPaymentByOrderCommand(orderId = 1L, cancelAmount = 50000L)
            cancelPaymentByOrderUseCase.execute(command)

            verify(cancelPaymentUseCase).execute(
                argThat<CancelPaymentCommand> {
                    this.paymentId == payment.paymentId && this.cancelAmount == 50000L
                },
            )
        }
    }

    @Nested
    @DisplayName("주문 기반 결제 취소 실패")
    inner class Fail {

        @Test
        fun `주문 ID로 결제를 찾을 수 없으면 NotFoundException이 발생한다`() {
            whenever(paymentRepository.findByOrderId(999L)).thenReturn(null)

            val command = CancelPaymentByOrderCommand(orderId = 999L, cancelAmount = 50000L)

            assertThatThrownBy { cancelPaymentByOrderUseCase.execute(command) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }
}
