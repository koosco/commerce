package com.koosco.paymentservice.application.port

import com.koosco.paymentservice.application.command.PaymentApproveCommand
import com.koosco.paymentservice.application.command.PaymentApproveResult
import com.koosco.paymentservice.domain.vo.Money
import java.util.UUID

interface PaymentGateway {

    fun approve(command: PaymentApproveCommand): PaymentApproveResult

    fun cancel(paymentId: UUID, pgTransactionId: String, amount: Money): PaymentApproveResult
}
