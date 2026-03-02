package com.koosco.inventoryservice.application.command

/**
 * 일반 구매 재고 차감 커맨드
 * RDB 조건부 UPDATE로 원자적 재고 차감을 수행
 */
data class DeductStockCommand(val orderId: Long, val items: List<DeductItem>, val idempotencyKey: String? = null) {
    data class DeductItem(val skuId: String, val quantity: Int)
}
