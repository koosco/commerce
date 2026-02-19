package com.koosco.paymentservice.application.port

import com.koosco.paymentservice.contract.PaymentIntegrationEvent

/**
 * fileName       : IntegrationEventProducer
 * author         : koo
 * date           : 2025. 12. 24. 오후 9:05
 * description    :
 */
interface IntegrationEventProducer {
    fun publish(event: PaymentIntegrationEvent)
}
