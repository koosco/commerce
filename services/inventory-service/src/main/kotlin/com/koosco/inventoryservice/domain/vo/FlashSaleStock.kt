package com.koosco.inventoryservice.domain.vo

/**
 * Flash Sale 재고 표현 모델
 *
 * 일반 구매는 RDB의 quantity 기반 차감을 사용하지만,
 * Flash Sale은 Redis/RDB projection에서 available, reserved, sold를 분리하여 관리합니다.
 *
 * @param available 구매 가능한 재고 수량
 * @param reserved 예약 중인 재고 수량 (아직 주문 확정 전)
 * @param sold 판매 확정된 수량
 */
data class FlashSaleStock(val available: Int, val reserved: Int, val sold: Int) {
    val total: Int
        get() = available + reserved + sold

    init {
        require(available >= 0) { "available must be non-negative: $available" }
        require(reserved >= 0) { "reserved must be non-negative: $reserved" }
        require(sold >= 0) { "sold must be non-negative: $sold" }
    }

    fun reserve(quantity: Int): FlashSaleStock {
        require(quantity > 0) { "Reserve quantity must be positive" }
        require(available >= quantity) { "Not enough available stock: available=$available, requested=$quantity" }
        return copy(
            available = available - quantity,
            reserved = reserved + quantity,
        )
    }

    fun confirm(quantity: Int): FlashSaleStock {
        require(quantity > 0) { "Confirm quantity must be positive" }
        require(reserved >= quantity) { "Not enough reserved stock: reserved=$reserved, requested=$quantity" }
        return copy(
            reserved = reserved - quantity,
            sold = sold + quantity,
        )
    }

    fun release(quantity: Int): FlashSaleStock {
        require(quantity > 0) { "Release quantity must be positive" }
        require(reserved >= quantity) { "Not enough reserved stock: reserved=$reserved, requested=$quantity" }
        return copy(
            available = available + quantity,
            reserved = reserved - quantity,
        )
    }
}
