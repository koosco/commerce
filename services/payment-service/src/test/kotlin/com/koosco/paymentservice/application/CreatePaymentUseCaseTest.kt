package com.koosco.paymentservice.application

import com.koosco.common.core.event.IntegrationEventProducer
import com.koosco.common.core.messaging.MessageContext
import com.koosco.paymentservice.application.command.CreatePaymentCommand
import com.koosco.paymentservice.application.port.PaymentRepository
import com.koosco.paymentservice.application.usecase.CreatePaymentUseCase
import com.koosco.paymentservice.contract.outbound.payment.PaymentCreatedEvent
import com.koosco.paymentservice.domain.entity.Payment
import com.koosco.paymentservice.domain.vo.Money
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

@ExtendWith(MockitoExtension::class)
@DisplayName("CreatePaymentUseCase 테스트")
class CreatePaymentUseCaseTest {

    @Mock
    private lateinit var paymentRepository: PaymentRepository

    @Mock
    private lateinit var integrationEventProducer: IntegrationEventProducer

    @InjectMocks
    private lateinit var createPaymentUseCase: CreatePaymentUseCase

    private val command = CreatePaymentCommand(orderId = 1L, userId = 100L, amount = 50000L)
    private val context = MessageContext(correlationId = "corr-1", causationId = "cause-1")

    @Nested
    @DisplayName("결제 생성 성공")
    inner class Success {

        @Test
        fun `새로운 주문에 대해 결제를 생성하고 이벤트를 발행한다`() {
            whenever(paymentRepository.existsByOrderId(1L)).thenReturn(false)
            val savedPayment = Payment(orderId = 1L, userId = 100L, amount = Money(50000L))
            whenever(paymentRepository.save(any())).thenReturn(savedPayment)

            createPaymentUseCase.execute(command, context)

            verify(paymentRepository).save(any())
            verify(integrationEventProducer).publish(
                argThat<PaymentCreatedEvent> {
                    this is PaymentCreatedEvent &&
                        this.orderId == 1L &&
                        this.paymentId == savedPayment.paymentId.toString()
                },
            )
        }
    }

    @Nested
    @DisplayName("멱등성 처리")
    inner class Idempotency {

        @Test
        fun `이미 존재하는 주문에 대해서는 결제를 생성하지 않는다`() {
            whenever(paymentRepository.existsByOrderId(1L)).thenReturn(true)

            createPaymentUseCase.execute(command, context)

            verify(paymentRepository, never()).save(any())
            verify(integrationEventProducer, never()).publish(any())
        }
    }
}
