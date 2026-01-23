package com.koosco.orderservice.order.infra.messaging

import com.koosco.orderservice.order.application.contract.OrderIntegrationEvent

/**
 * fileName       : IntegrationTopicResolver
 * author         : koo
 * date           : 2025. 12. 19. 오후 3:00
 * description    :
 */
interface TopicResolver {
    fun resolve(event: OrderIntegrationEvent): String
}
