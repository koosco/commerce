package com.koosco.orderservice.domain.enums

enum class OrderStatus {
    /** 주문 생성됨 */
    CREATED,

    /** 재고 예약 완료 */
    RESERVED,

    /** 결제 초기화 완료 */
    PAYMENT_CREATED,

    /** 결제 대기 중 */
    PAYMENT_PENDING,

    /** 결제 완료 */
    PAID,

    /** 재고 확정 차감 완료 */
    CONFIRMED,

    /** 결제 취소 */
    CANCELLED,

    /** 실패 */
    FAILED,
}
