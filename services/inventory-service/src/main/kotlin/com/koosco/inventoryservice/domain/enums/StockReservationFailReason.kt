package com.koosco.inventoryservice.domain.enums

/**
 * fileName       : StockReservationFailReason
 * author         : koo
 * date           : 2025. 12. 24. 오전 3:56
 * description    :
 */
enum class StockReservationFailReason {
    NOT_ENOUGH_STOCK,
    SKU_NOT_FOUND,
    RESERVATION_CONFLICT,
    INVENTORY_LOCKED,
}
