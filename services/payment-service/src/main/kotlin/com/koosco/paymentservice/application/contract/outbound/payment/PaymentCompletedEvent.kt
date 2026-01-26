package com.koosco.paymentservice.application.contract.outbound.payment

import com.koosco.paymentservice.application.contract.PaymentIntegrationEvent

data class PaymentCompletedEvent(
    override val paymentId: String,
    val orderId: Long,
    val pgTransactionId: String?,
    val paidAmount: Long,
    val currency: String = "KRW",
    val approvedAt: Long,
    val correlationId: String,
    val causationId: String?,
) : PaymentIntegrationEvent {
    override fun getEventType(): String = "payment.completed"
}
