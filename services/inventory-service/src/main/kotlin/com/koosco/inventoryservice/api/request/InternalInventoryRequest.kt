package com.koosco.inventoryservice.api.request

/**
 * POST /internal/inventory/reservations
 * 내부 재고 예약 요청 DTO
 */
data class InternalReserveRequest(
    val orderId: Long,
    val items: List<ReserveItemRequest>,
    val idempotencyKey: String? = null,
    val correlationId: String? = null,
) {
    data class ReserveItemRequest(val skuId: String, val quantity: Int)
}

/**
 * POST /internal/inventory/confirmations
 * 내부 재고 확정 요청 DTO
 */
data class InternalConfirmRequest(
    val orderId: Long,
    val items: List<ConfirmItemRequest>,
    val idempotencyKey: String? = null,
    val correlationId: String? = null,
) {
    data class ConfirmItemRequest(val skuId: String, val quantity: Int)
}

/**
 * POST /internal/inventory/releases
 * 내부 재고 해제 요청 DTO
 */
data class InternalReleaseRequest(
    val orderId: Long,
    val items: List<ReleaseItemRequest>,
    val reason: String? = null,
    val idempotencyKey: String? = null,
    val correlationId: String? = null,
) {
    data class ReleaseItemRequest(val skuId: String, val quantity: Int)
}
