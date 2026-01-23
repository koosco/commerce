package com.koosco.inventoryservice.inventory.domain.enums

/**
 * fileName       : CancelStockReason
 * author         : koo
 * date           : 2025. 12. 24. 오전 4:27
 * description    :
 */
enum class StockCancelReason {
    PAYMENT_FAILED,
    USER_CANCELLED,
    PAYMENT_TIMEOUT,
    ;

    companion object {
        fun mapCancelReason(reason: String?): StockCancelReason = when (reason?.uppercase()) {
            "PAYMENT_FAILED" -> PAYMENT_FAILED
            "USER_CANCELLED" -> USER_CANCELLED
            "PAYMENT_TIMEOUT" -> PAYMENT_TIMEOUT
            else -> USER_CANCELLED
        }
    }
}
