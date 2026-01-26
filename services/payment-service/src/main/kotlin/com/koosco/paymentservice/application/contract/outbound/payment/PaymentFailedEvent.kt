package com.koosco.paymentservice.application.contract.outbound.payment

import com.koosco.paymentservice.application.contract.PaymentIntegrationEvent

data class PaymentFailedEvent(
    override val paymentId: String,
    val orderId: Long,
    val pgTransactionId: String?,
    val cancelledAmount: Long,
    val currency: String = "KRW",
    val reason: String,
    val cancelledAt: Long,
    val correlationId: String,
    val causationId: String?,
) : PaymentIntegrationEvent {
    override fun getEventType(): String = "payment.failed"
}
