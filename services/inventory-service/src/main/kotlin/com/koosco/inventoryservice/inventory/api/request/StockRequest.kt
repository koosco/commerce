package com.koosco.inventoryservice.inventory.api.request

/**
 * 재고 조회 요청 DTO
 */
data class GetInventoriesRequest(val skuIds: List<String>)

/**
 * 재고 추가 요청 DTO
 */
data class AddStockRequest(val quantity: Int)

/**
 * 대량 재고 추가 요청 DTO
 */
data class BulkAddStockRequest(val items: List<AddingStockInfo>) {

    data class AddingStockInfo(val skuId: String, val quantity: Int)
}

/**
 * 재고 감소 요청 DTO
 */
data class ReduceStockRequest(val quantity: Int)

/**
 * 대량 재고 감소 요청 DTO
 */
data class BulkReduceStockRequest(val items: List<ReducingStockInfo>) {

    data class ReducingStockInfo(val skuId: String, val quantity: Int)
}
