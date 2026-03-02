package com.koosco.paymentservice.contract.outbound.payment

import com.koosco.common.core.event.IntegrationEvent

data class PaymentCanceledEvent(
    val paymentId: String,
    val orderId: Long,
    val pgTransactionId: String?,
    val canceledAmount: Long,
    val totalCanceledAmount: Long,
    val remainingAmount: Long,
    val isFullyCanceled: Boolean,
    val currency: String = "KRW",
    val canceledAt: Long,
) : IntegrationEvent {
    override val aggregateId: String get() = paymentId

    override fun getEventType(): String = "payment.canceled"

    override fun getSubject(): String = "payment/$paymentId"
}
