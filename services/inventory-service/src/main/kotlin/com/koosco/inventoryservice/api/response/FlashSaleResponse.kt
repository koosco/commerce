package com.koosco.inventoryservice.api.response

import java.time.LocalDateTime

/**
 * Flash Sale 예약 응답 DTO
 */
data class FlashSaleReservationResponse(
    val reservationId: String,
    val flashSaleId: Long,
    val skuId: String,
    val quantity: Int,
    val status: String,
    val expiresAt: LocalDateTime,
)

/**
 * Flash Sale 재고 현황 응답 DTO
 */
data class FlashSaleStockResponse(
    val flashSaleId: Long,
    val skuId: String,
    val available: Int,
    val reserved: Int,
    val sold: Int,
)
