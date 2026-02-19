package com.koosco.inventoryservice.application.result

data class GetInventoryResult(val skuId: String, val totalStock: Int, val reservedStock: Int, val availableStock: Int)
