package com.koosco.inventoryservice.api.response

/**
 * 단일 재고 조회 응답 DTO
 */
data class GetInventoryResponse(
    val skuId: String,
    val totalStock: Int,
    val reservedStock: Int,
    val availableStock: Int,
)

/**
 * 대량 재고 조회 응답 DTO
 */
data class GetInventoriesResponse(val inventories: List<InventoryInfo>) {
    data class InventoryInfo(val skuId: String, val totalStock: Int, val reservedStock: Int, val availableStock: Int)
}
