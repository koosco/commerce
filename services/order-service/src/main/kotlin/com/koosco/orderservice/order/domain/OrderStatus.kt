package com.koosco.orderservice.order.domain

enum class OrderStatus {
    /**
     * 주문 객체가 생성됨 (초기화 전)
     */
    INIT,

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

    /** 부분 환불 */
    PARTIALLY_REFUNDED,

    /** 전체 환불 */
    REFUNDED,

    /** 결제 취소 */
    CANCELLED,

    /** 실패 */
    FAILED,
}
