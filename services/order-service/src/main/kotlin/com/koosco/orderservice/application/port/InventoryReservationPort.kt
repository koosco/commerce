package com.koosco.orderservice.application.port

interface InventoryReservationPort {

    fun reserve(command: ReserveCommand)

    data class ReserveCommand(
        val orderId: Long,
        val items: List<ReserveItem>,
        val idempotencyKey: String? = null,
        val correlationId: String,
    ) {
        data class ReserveItem(val skuId: Long, val quantity: Int)
    }
}
