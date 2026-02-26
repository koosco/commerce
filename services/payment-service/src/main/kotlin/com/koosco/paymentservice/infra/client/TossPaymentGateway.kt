package com.koosco.paymentservice.infra.client

import com.koosco.paymentservice.application.command.PaymentApproveCommand
import com.koosco.paymentservice.application.command.PaymentApproveResult
import com.koosco.paymentservice.application.port.PaymentGateway
import com.koosco.paymentservice.domain.vo.Money
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class TossPaymentGateway : PaymentGateway {
    private val logger = LoggerFactory.getLogger(javaClass)

    @CircuitBreaker(name = "pg-api", fallbackMethod = "approveFallback")
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

    @CircuitBreaker(name = "pg-api", fallbackMethod = "cancelFallback")
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

    @Suppress("unused")
    private fun approveFallback(command: PaymentApproveCommand, ex: Throwable): PaymentApproveResult {
        logger.error(
            "PG API circuit breaker open for approve. paymentId={}, orderId={}",
            command.paymentId,
            command.orderId,
            ex,
        )
        return PaymentApproveResult(
            success = false,
            pgTransactionId = null,
            failureReason = "PG_SERVICE_UNAVAILABLE",
        )
    }

    @Suppress("unused")
    private fun cancelFallback(
        paymentId: UUID,
        pgTransactionId: String,
        amount: Money,
        ex: Throwable,
    ): PaymentApproveResult {
        logger.error(
            "PG API circuit breaker open for cancel. paymentId={}, pgTransactionId={}",
            paymentId,
            pgTransactionId,
            ex,
        )
        return PaymentApproveResult(
            success = false,
            pgTransactionId = pgTransactionId,
            failureReason = "PG_SERVICE_UNAVAILABLE",
        )
    }
}
