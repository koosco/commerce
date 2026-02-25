package com.koosco.paymentservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.paymentservice.application.command.CancelPaymentCommand
import com.koosco.paymentservice.application.port.IntegrationEventProducer
import com.koosco.paymentservice.application.port.PaymentGateway
import com.koosco.paymentservice.application.port.PaymentRepository
import com.koosco.paymentservice.common.PaymentErrorCode
import com.koosco.paymentservice.contract.outbound.payment.PaymentCanceledEvent
import com.koosco.paymentservice.domain.entity.PaymentTransaction
import com.koosco.paymentservice.domain.entity.PaymentTransactionStatus
import com.koosco.paymentservice.domain.entity.PaymentTransactionType
import com.koosco.paymentservice.domain.enums.PaymentStatus
import com.koosco.paymentservice.domain.vo.Money
import org.springframework.transaction.annotation.Transactional

@UseCase
class CancelPaymentUseCase(
    private val paymentRepository: PaymentRepository,
    private val paymentGateway: PaymentGateway,
    private val integrationEventProducer: IntegrationEventProducer,
) {

    @Transactional
    fun execute(command: CancelPaymentCommand) {
        val payment = paymentRepository.findByPaymentId(command.paymentId)
            ?: throw NotFoundException(PaymentErrorCode.PAYMENT_NOT_FOUND)

        val cancelAmount = Money(command.cancelAmount)

        val approvalTransaction = payment.transactions()
            .first { it.type == PaymentTransactionType.APPROVAL && it.status == PaymentTransactionStatus.SUCCESS }

        val result = paymentGateway.cancel(
            paymentId = payment.paymentId,
            pgTransactionId = approvalTransaction.pgTransactionId!!,
            amount = cancelAmount,
        )

        val transaction = PaymentTransaction(
            payment = payment,
            type = PaymentTransactionType.CANCEL,
            status = if (result.success) PaymentTransactionStatus.SUCCESS else PaymentTransactionStatus.FAILED,
            pgTransactionId = result.pgTransactionId,
            amount = cancelAmount,
        )

        if (result.success) {
            payment.cancel(transaction)
            paymentRepository.save(payment)

            val totalCanceled = payment.totalCanceledAmount()
            val remaining = payment.amount - totalCanceled

            integrationEventProducer.publish(
                PaymentCanceledEvent(
                    paymentId = payment.paymentId.toString(),
                    orderId = payment.orderId,
                    pgTransactionId = result.pgTransactionId,
                    canceledAmount = cancelAmount.value,
                    totalCanceledAmount = totalCanceled.value,
                    remainingAmount = remaining.value,
                    isFullyCanceled = payment.status == PaymentStatus.CANCELED,
                    canceledAt = System.currentTimeMillis(),
                ),
            )
        } else {
            throw com.koosco.common.core.exception.BadRequestException(
                PaymentErrorCode.PAYMENT_CANCEL_FAILED,
            )
        }
    }
}
