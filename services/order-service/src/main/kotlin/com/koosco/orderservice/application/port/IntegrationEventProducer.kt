package com.koosco.orderservice.application.port

import com.koosco.orderservice.contract.OrderIntegrationEvent

/**
 * fileName       : IntegrationEventProducer
 * author         : koo
 * date           : 2025. 12. 19. 오후 1:45
 * description    :
 */
interface IntegrationEventProducer {
    fun publish(event: OrderIntegrationEvent)
}
