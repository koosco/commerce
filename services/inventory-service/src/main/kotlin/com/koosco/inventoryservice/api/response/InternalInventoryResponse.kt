package com.koosco.inventoryservice.api.response

/**
 * 내부 재고 예약 응답 DTO
 */
data class InternalReserveResponse(val orderId: Long, val reservedItems: List<ReservedItemInfo>) {
    data class ReservedItemInfo(val skuId: String, val quantity: Int)
}

/**
 * 내부 재고 확정 응답 DTO
 */
data class InternalConfirmResponse(val orderId: Long, val confirmedItems: List<ConfirmedItemInfo>) {
    data class ConfirmedItemInfo(val skuId: String, val quantity: Int)
}

/**
 * 내부 재고 해제 응답 DTO
 */
data class InternalReleaseResponse(val orderId: Long, val releasedItems: List<ReleasedItemInfo>) {
    data class ReleasedItemInfo(val skuId: String, val quantity: Int)
}
