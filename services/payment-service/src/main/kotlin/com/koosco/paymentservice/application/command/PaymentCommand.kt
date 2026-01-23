package com.koosco.paymentservice.application.command

import com.koosco.paymentservice.domain.vo.Money
import java.util.UUID

data class PaymentApproveCommand(val paymentId: UUID, val orderId: Long, val amount: Money)

data class PaymentApproveResult(val success: Boolean, val pgTransactionId: String?, val failureReason: String? = null)
