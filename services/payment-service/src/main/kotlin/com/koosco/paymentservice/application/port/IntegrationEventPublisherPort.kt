package com.koosco.paymentservice.application.port

import com.koosco.paymentservice.application.contract.PaymentIntegrationEvent

/**
 * fileName       : IntegrationEventPublisher
 * author         : koo
 * date           : 2025. 12. 24. 오후 9:05
 * description    :
 */
interface IntegrationEventPublisherPort {
    fun publish(event: PaymentIntegrationEvent)
}
