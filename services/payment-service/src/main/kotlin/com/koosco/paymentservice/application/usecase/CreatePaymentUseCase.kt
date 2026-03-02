package com.koosco.paymentservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.event.IntegrationEventProducer
import com.koosco.common.core.messaging.MessageContext
import com.koosco.paymentservice.application.command.CreatePaymentCommand
import com.koosco.paymentservice.application.port.PaymentRepository
import com.koosco.paymentservice.contract.outbound.payment.PaymentCreatedEvent
import com.koosco.paymentservice.domain.entity.Payment
import com.koosco.paymentservice.domain.vo.Money
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@UseCase
class CreatePaymentUseCase(
    private val paymentRepository: PaymentRepository,
    private val integrationEventProducer: IntegrationEventProducer,
) {

    @Transactional
    fun execute(command: CreatePaymentCommand, context: MessageContext) {
        if (paymentRepository.existsByOrderId(command.orderId)) {
            return
        }

        val savedPayment = paymentRepository.save(
            Payment(
                paymentId = UUID.randomUUID(),
                orderId = command.orderId,
                userId = command.userId,
                amount = Money(command.amount),
            ),
        )

        integrationEventProducer.publish(
            PaymentCreatedEvent(savedPayment.paymentId.toString(), savedPayment.orderId),
        )
    }
}
