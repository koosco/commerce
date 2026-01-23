package com.koosco.inventoryservice.inventory.application.command

import com.koosco.inventoryservice.inventory.domain.enums.StockCancelReason

/**
 * fileName       : ReserveCommand
 * author         : koo
 * date           : 2025. 12. 24. 오전 3:10
 * description    :
 */
data class ReserveStockCommand(val orderId: Long, val items: List<ReservedSku>, val ttlSeconds: Long? = null) {
    data class ReservedSku(val skuId: String, val quantity: Int)
}

/**
 * 결제 성공 이후 예약 확정(차감 확정)
 */
data class ConfirmStockCommand(val orderId: Long, val items: List<ConfirmedSku>) {
    data class ConfirmedSku(val skuId: String, val quantity: Int)
}

/**
 * 결제 실패/사용자 취소/타임아웃 등으로 예약 취소(보상)
 */
data class CancelStockCommand(val orderId: Long, val items: List<CancelledSku>, val reason: StockCancelReason) {
    data class CancelledSku(val skuId: String, val quantity: Int)
}
