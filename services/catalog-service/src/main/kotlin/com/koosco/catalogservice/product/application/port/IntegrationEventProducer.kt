package com.koosco.catalogservice.product.application.port

import com.koosco.catalogservice.product.application.contract.ProductIntegrationEvent

/**
 * fileName       : IntegrationEventProducer
 * author         : koo
 * date           : 2025. 12. 19. 오후 1:45
 * description    :
 */
interface IntegrationEventProducer {
    fun publish(event: ProductIntegrationEvent)
}
