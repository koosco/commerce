package com.koosco.paymentservice.contract.outbound.payment

import com.koosco.paymentservice.contract.PaymentIntegrationEvent

data class PaymentCanceledEvent(
    override val paymentId: String,
    val orderId: Long,
    val pgTransactionId: String?,
    val canceledAmount: Long,
    val totalCanceledAmount: Long,
    val remainingAmount: Long,
    val isFullyCanceled: Boolean,
    val currency: String = "KRW",
    val canceledAt: Long,
) : PaymentIntegrationEvent {
    override fun getEventType(): String = "payment.canceled"
}
