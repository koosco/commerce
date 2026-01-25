package com.koosco.paymentservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.paymentservice.application.command.CreatePaymentCommand
import com.koosco.paymentservice.application.contract.outbound.payment.PaymentCreatedEvent
import com.koosco.paymentservice.application.port.IdempotencyRepositoryPort
import com.koosco.paymentservice.application.port.IntegrationEventPublisher
import com.koosco.paymentservice.application.port.PaymentRepositoryPort
import com.koosco.paymentservice.common.MessageContext
import com.koosco.paymentservice.domain.entity.Payment
import com.koosco.paymentservice.domain.entity.PaymentIdempotency
import com.koosco.paymentservice.domain.enums.PaymentAction
import com.koosco.paymentservice.domain.vo.Money
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * fileName       : CreatePaymentUseCase
 * author         : koo
 * date           : 2025. 12. 22. 오전 4:03
 * description    :
 */
@UseCase
class CreatePaymentUseCase(
    private val idempotencyRepository: IdempotencyRepositoryPort,
    private val paymentRepository: PaymentRepositoryPort,
    private val integrationEventPublisher: IntegrationEventPublisher,
) {

    @Transactional
    fun execute(command: CreatePaymentCommand, context: MessageContext) {
        val idempotencyKey = requireNotNull(context.causationId) {
            "causationId(eventId) must be provided for idempotency"
        }

        try {
            idempotencyRepository.save(
                PaymentIdempotency(
                    orderId = command.orderId,
                    action = PaymentAction.CREATE,
                    idempotencyKey = idempotencyKey,
                ),
            )

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

            integrationEventPublisher.publish(
                PaymentCreatedEvent(savedPayment.paymentId.toString(), savedPayment.orderId),
            )
        } catch (e: DataIntegrityViolationException) {
            // ✅ 동일 이벤트 재처리 → 멱등 성공 처리
            return
        }
    }
}
