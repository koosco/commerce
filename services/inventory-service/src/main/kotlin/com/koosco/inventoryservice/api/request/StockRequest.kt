package com.koosco.inventoryservice.api.request

import com.koosco.inventoryservice.application.command.DeductStockCommand
import com.koosco.inventoryservice.application.command.ReserveStockCommand

/**
 * 재고 조회 요청 DTO
 */
data class GetInventoriesRequest(val skuIds: List<String>)

/**
 * 대량 재고 추가 요청 DTO
 */
data class BulkAddStockRequest(val items: List<AddingStockInfo>, val idempotencyKey: String? = null) {

    data class AddingStockInfo(val skuId: String, val quantity: Int)
}

/**
 * 대량 재고 감소 요청 DTO
 */
data class BulkReduceStockRequest(val items: List<ReducingStockInfo>, val idempotencyKey: String? = null) {

    data class ReducingStockInfo(val skuId: String, val quantity: Int)
}

/**
 * 내부 주문 시스템의 재고 예약 요청 DTO
 */
data class ReserveStockRequest(
    val orderId: Long,
    val items: List<ReserveItemInfo>,
    val idempotencyKey: String? = null,
    val correlationId: String? = null,
) {
    data class ReserveItemInfo(val skuId: String, val quantity: Int)

    fun toCommand(): ReserveStockCommand = ReserveStockCommand(
        orderId = orderId,
        items = items.map { ReserveStockCommand.ReservedSku(it.skuId, it.quantity) },
    )
}

/**
 * 일반 구매 재고 차감 요청 DTO
 */
data class DeductStockRequest(val orderId: Long, val items: List<DeductItemInfo>, val idempotencyKey: String? = null) {
    data class DeductItemInfo(val skuId: String, val quantity: Int)

    fun toCommand(): DeductStockCommand = DeductStockCommand(
        orderId = orderId,
        items = items.map { DeductStockCommand.DeductItem(it.skuId, it.quantity) },
        idempotencyKey = idempotencyKey,
    )
}
