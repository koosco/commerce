package com.koosco.catalogservice.infra.messaging

import com.koosco.catalogservice.contract.ProductIntegrationEvent

/**
 * fileName       : IntegrationTopicResolver
 * author         : koo
 * date           : 2025. 12. 19. 오후 3:00
 * description    :
 */
interface TopicResolver {
    fun resolve(event: ProductIntegrationEvent): String
}
