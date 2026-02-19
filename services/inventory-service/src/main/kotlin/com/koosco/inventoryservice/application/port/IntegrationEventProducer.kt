package com.koosco.inventoryservice.application.port

import com.koosco.inventoryservice.contract.InventoryIntegrationEvent

/**
 * fileName       : IntegrationEventProducer
 * author         : koo
 * date           : 2025. 12. 19. 오후 1:45
 * description    :
 */
interface IntegrationEventProducer {
    fun publish(event: InventoryIntegrationEvent)
}
