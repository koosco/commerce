package com.koosco.paymentservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.paymentservice.application.command.PaymentApproveCommand
import com.koosco.paymentservice.application.contract.outbound.payment.PaymentCompletedEvent
import com.koosco.paymentservice.application.contract.outbound.payment.PaymentFailedEvent
import com.koosco.paymentservice.application.port.IdempotencyRepository
import com.koosco.paymentservice.application.port.IntegrationEventProducer
import com.koosco.paymentservice.application.port.PaymentGateway
import com.koosco.paymentservice.application.port.PaymentRepository
import com.koosco.paymentservice.common.PaymentErrorCode
import com.koosco.paymentservice.domain.entity.PaymentTransaction
import com.koosco.paymentservice.domain.entity.PaymentTransactionStatus
import com.koosco.paymentservice.domain.entity.PaymentTransactionType
import com.koosco.paymentservice.domain.enums.PaymentStatus
import jakarta.transaction.Transactional
import java.util.UUID

@UseCase
@Transactional
class ApprovePaymentUseCase(
    private val idempotencyRepository: IdempotencyRepository,
    private val paymentRepository: PaymentRepository,
    private val paymentGateway: PaymentGateway,
    private val integrationEventProducer: IntegrationEventProducer,
) {

    fun execute(paymentId: UUID, command: PaymentApproveCommand, idempotencyKey: String) {
        val payment = paymentRepository.findByPaymentId(paymentId)
            ?: throw NotFoundException(PaymentErrorCode.PAYMENT_NOT_FOUND)

        if (payment.status != PaymentStatus.READY) {
            // Already processed - idempotent
            return
        }

        val result = paymentGateway.approve(command)
        val now = System.currentTimeMillis()

        if (result.success) {
            val transaction = PaymentTransaction(
                payment = payment,
                type = PaymentTransactionType.APPROVAL,
                status = PaymentTransactionStatus.SUCCESS,
                pgTransactionId = result.pgTransactionId,
                amount = payment.amount,
            )
            payment.approve(transaction)
            paymentRepository.save(payment)

            integrationEventProducer.publish(
                PaymentCompletedEvent(
                    paymentId = payment.paymentId.toString(),
                    orderId = payment.orderId,
                    pgTransactionId = result.pgTransactionId,
                    paidAmount = payment.amount.value,
                    approvedAt = now,
                    correlationId = payment.orderId.toString(),
                    causationId = idempotencyKey,
                ),
            )
        } else {
            val transaction = PaymentTransaction(
                payment = payment,
                type = PaymentTransactionType.APPROVAL,
                status = PaymentTransactionStatus.FAILED,
                pgTransactionId = result.pgTransactionId,
                amount = payment.amount,
            )
            payment.fail(transaction)
            paymentRepository.save(payment)

            integrationEventProducer.publish(
                PaymentFailedEvent(
                    paymentId = payment.paymentId.toString(),
                    orderId = payment.orderId,
                    pgTransactionId = result.pgTransactionId,
                    cancelledAmount = payment.amount.value,
                    reason = result.failureReason ?: "PAYMENT_FAILED",
                    cancelledAt = now,
                    correlationId = payment.orderId.toString(),
                    causationId = idempotencyKey,
                ),
            )
        }
    }
}
