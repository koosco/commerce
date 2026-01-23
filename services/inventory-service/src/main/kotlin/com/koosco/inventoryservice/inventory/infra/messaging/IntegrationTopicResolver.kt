package com.koosco.inventoryservice.inventory.infra.messaging

import com.koosco.inventoryservice.inventory.application.contract.InventoryIntegrationEvent

/**
 * fileName       : IntegrationTopicResolver
 * author         : koo
 * date           : 2025. 12. 19. 오후 3:00
 * description    :
 */
interface IntegrationTopicResolver {
    fun resolve(event: InventoryIntegrationEvent): String
}
