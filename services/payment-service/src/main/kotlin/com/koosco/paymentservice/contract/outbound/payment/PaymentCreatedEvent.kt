package com.koosco.paymentservice.contract.outbound.payment

import com.koosco.common.core.event.IntegrationEvent

data class PaymentCreatedEvent(val paymentId: String, val orderId: Long) : IntegrationEvent {
    override val aggregateId: String get() = paymentId

    override fun getEventType(): String = "payment.created"

    override fun getSubject(): String = "payment/$paymentId"
}
