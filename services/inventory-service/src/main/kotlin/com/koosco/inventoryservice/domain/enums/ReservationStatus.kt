package com.koosco.inventoryservice.domain.enums

/**
 * Flash Sale 예약 상태
 *
 * - RESERVED: 예약 완료 (Redis에서 재고 차감됨)
 * - CONFIRMED: 주문 확정으로 예약이 확정됨
 * - RELEASED: 사용자 취소 또는 타임아웃으로 예약 해제됨
 * - EXPIRED: TTL 만료로 자동 해제됨
 */
enum class ReservationStatus {
    RESERVED,
    CONFIRMED,
    RELEASED,
    EXPIRED,
}
