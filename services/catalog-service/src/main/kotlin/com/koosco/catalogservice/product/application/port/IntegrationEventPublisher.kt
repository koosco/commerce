package com.koosco.catalogservice.product.application.port

import com.koosco.catalogservice.product.application.contract.ProductIntegrationEvent

/**
 * fileName       : IntegrationEventPublisher
 * author         : koo
 * date           : 2025. 12. 19. 오후 1:45
 * description    :
 */
interface IntegrationEventPublisher {
    fun publish(event: ProductIntegrationEvent)
}
