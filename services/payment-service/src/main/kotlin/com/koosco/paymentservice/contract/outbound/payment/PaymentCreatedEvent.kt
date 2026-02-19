package com.koosco.paymentservice.contract.outbound.payment

import com.koosco.paymentservice.contract.PaymentIntegrationEvent

/**
 * fileName       : PaymentCreatedEvent
 * author         : koo
 * date           : 2025. 12. 24. 오후 9:20
 * description    :
 */
data class PaymentCreatedEvent(override val paymentId: String, val orderId: Long) : PaymentIntegrationEvent {
    override fun getEventType(): String = "payment.created"
}
