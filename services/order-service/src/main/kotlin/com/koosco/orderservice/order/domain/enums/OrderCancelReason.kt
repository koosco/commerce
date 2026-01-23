package com.koosco.orderservice.order.domain.enums

/**
 * fileName       : OrderCancelReason
 * author         : koo
 * date           : 2025. 12. 24. 오전 3:40
 * description    :
 */
enum class OrderCancelReason {
    USER_REQUEST,
    PAYMENT_TIMEOUT,
    PAYMENT_FAILED,
}
