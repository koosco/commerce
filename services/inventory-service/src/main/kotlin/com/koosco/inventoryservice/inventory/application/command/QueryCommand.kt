package com.koosco.inventoryservice.inventory.application.command

/**
 * fileName       : QueryCommand
 * author         : koo
 * date           : 2025. 12. 24. 오전 3:11
 * description    :
 */
data class GetInventoryCommand(val skuId: String)

data class GetInventoriesCommand(val skuIds: List<String>)
