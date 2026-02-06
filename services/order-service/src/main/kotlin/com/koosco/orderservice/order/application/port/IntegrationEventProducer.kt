package com.koosco.orderservice.order.application.port

import com.koosco.orderservice.order.application.contract.OrderIntegrationEvent

/**
 * fileName       : IntegrationEventProducer
 * author         : koo
 * date           : 2025. 12. 19. 오후 1:45
 * description    :
 */
interface IntegrationEventProducer {
    fun publish(event: OrderIntegrationEvent)
}
