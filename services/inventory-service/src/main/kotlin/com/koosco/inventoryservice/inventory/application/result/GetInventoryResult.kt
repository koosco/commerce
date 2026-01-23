package com.koosco.inventoryservice.inventory.application.result

data class GetInventoryResult(val skuId: String, val totalStock: Int, val reservedStock: Int, val availableStock: Int)
