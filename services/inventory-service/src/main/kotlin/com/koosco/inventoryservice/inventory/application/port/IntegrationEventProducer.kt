package com.koosco.inventoryservice.inventory.application.port

import com.koosco.inventoryservice.inventory.application.contract.InventoryIntegrationEvent

/**
 * fileName       : IntegrationEventProducer
 * author         : koo
 * date           : 2025. 12. 19. 오후 1:45
 * description    :
 */
interface IntegrationEventProducer {
    fun publish(event: InventoryIntegrationEvent)
}
