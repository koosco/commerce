package com.koosco.orderservice.order.domain.exception

import com.koosco.orderservice.common.error.OrderErrorCode

/**
 * fileName       : OrderExceptions
 * author         : koo
 * date           : 2025. 12. 22. 오전 5:15
 * description    :
 */
class InvalidOrderStatus(message: String = "Invalid order status") :
    BusinessException(
        OrderErrorCode.INVALID_ORDER_STATUS,
        message,
    )

class PaymentMisMatch(message: String = "Payment amount mismatched") :
    BusinessException(
        OrderErrorCode.INVALID_PAYMENT_STATUS,
        message,
    )
