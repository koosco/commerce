package com.koosco.paymentservice.infra.client

import com.koosco.paymentservice.application.command.PaymentApproveCommand
import com.koosco.paymentservice.application.command.PaymentApproveResult
import com.koosco.paymentservice.application.port.PaymentGateway
import com.koosco.paymentservice.domain.vo.Money
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class TossPaymentGateway : PaymentGateway {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun approve(command: PaymentApproveCommand): PaymentApproveResult {
        logger.info(
            "Approving payment: paymentId={}, orderId={}, amount={}",
            command.paymentId,
            command.orderId,
            command.amount,
        )

        // Mock implementation - always succeeds
        // TODO: Implement actual Toss Payments API call
        return PaymentApproveResult(
            success = true,
            pgTransactionId = "toss_txn_${UUID.randomUUID()}",
        )
    }

    override fun cancel(paymentId: UUID, pgTransactionId: String, amount: Money): PaymentApproveResult {
        logger.info(
            "Cancelling payment: paymentId={}, pgTransactionId={}, amount={}",
            paymentId,
            pgTransactionId,
            amount,
        )

        // Mock implementation
        return PaymentApproveResult(
            success = true,
            pgTransactionId = pgTransactionId,
        )
    }
}
