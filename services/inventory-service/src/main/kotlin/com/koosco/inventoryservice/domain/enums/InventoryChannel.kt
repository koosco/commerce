package com.koosco.inventoryservice.domain.enums

/**
 * 재고 확보 채널
 *
 * - NORMAL: 일반 구매 (RDB quantity 기반 차감)
 * - FLASH_SALE: Flash Sale (Redis/RDB projection에서 available, reserved, sold 사용)
 */
enum class InventoryChannel {
    NORMAL,
    FLASH_SALE,
}
