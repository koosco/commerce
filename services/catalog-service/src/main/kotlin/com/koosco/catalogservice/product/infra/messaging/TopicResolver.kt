package com.koosco.catalogservice.product.infra.messaging

import com.koosco.catalogservice.product.application.contract.ProductIntegrationEvent

/**
 * fileName       : IntegrationTopicResolver
 * author         : koo
 * date           : 2025. 12. 19. 오후 3:00
 * description    :
 */
interface TopicResolver {
    fun resolve(event: ProductIntegrationEvent): String
}
