package com.koosco.inventoryservice.inventory.application.command

/**
 * fileName       : StockCommands
 * author         : koo
 * date           : 2025. 12. 22.
 * description    :
 *  - 주문 단위(여러 SKU) 원자적 재고 예약/확정/취소를 지원하도록 커맨드 모델을 정리
 *  - 멱등성/추적/만료(TTL) 처리를 위한 필드 보강
 */

data class InitStockCommand(val skuId: String, val initialQuantity: Int)

data class AddStockCommand(val skuId: String, val addingQuantity: Int)

data class BulkAddStockCommand(val items: List<AddingStockInfo>) {
    data class AddingStockInfo(val skuId: String, val addingQuantity: Int)
}

data class ReduceStockCommand(val skuId: String, val reducingQuantity: Int)

data class BulkReduceStockCommand(val items: List<ReducingStockInfo>) {
    data class ReducingStockInfo(val skuId: String, val reducingQuantity: Int)
}
