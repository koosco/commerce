package com.koosco.inventoryservice.inventory.application.port

import com.koosco.inventoryservice.inventory.application.contract.InventoryIntegrationEvent

/**
 * fileName       : IntegrationEventPublisher
 * author         : koo
 * date           : 2025. 12. 19. 오후 1:45
 * description    :
 */
interface IntegrationEventPublisher {
    fun publish(event: InventoryIntegrationEvent)
}
