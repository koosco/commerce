package com.koosco.paymentservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.paymentservice.application.command.CancelPaymentByOrderCommand
import com.koosco.paymentservice.application.command.CancelPaymentCommand
import com.koosco.paymentservice.application.port.PaymentRepository
import com.koosco.paymentservice.common.PaymentErrorCode

@UseCase
class CancelPaymentByOrderUseCase(
    private val paymentRepository: PaymentRepository,
    private val cancelPaymentUseCase: CancelPaymentUseCase,
) {

    fun execute(command: CancelPaymentByOrderCommand) {
        val payment = paymentRepository.findByOrderId(command.orderId)
            ?: throw NotFoundException(PaymentErrorCode.PAYMENT_NOT_FOUND)

        cancelPaymentUseCase.execute(
            CancelPaymentCommand(
                paymentId = payment.paymentId,
                cancelAmount = command.cancelAmount,
            ),
        )
    }
}
