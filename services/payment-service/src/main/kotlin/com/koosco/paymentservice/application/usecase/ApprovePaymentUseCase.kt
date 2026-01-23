package com.koosco.paymentservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.paymentservice.application.command.PaymentApproveCommand
import com.koosco.paymentservice.application.port.IdempotencyRepositoryPort
import com.koosco.paymentservice.application.port.PaymentRepositoryPort
import com.koosco.paymentservice.common.PaymentErrorCode
import jakarta.transaction.Transactional
import java.util.UUID

@UseCase
@Transactional
class ApprovePaymentUseCase(
    private val idempotencyRepository: IdempotencyRepositoryPort,
    private val paymentRepository: PaymentRepositoryPort,
) {

    fun execute(paymentId: UUID, command: PaymentApproveCommand, idempotencyKey: String) {
        val payment = (
            paymentRepository.findByPaymentId(paymentId)
                ?: throw NotFoundException(PaymentErrorCode.PAYMENT_NOT_FOUND)
            )

        // TODO : 승인 로직
    }
}
