package com.koosco.paymentservice.api

/**
 * fileName       : PaymentConfirmRequest
 * author         : koo
 * date           : 2025. 12. 24. 오후 5:28
 * description    :
 */
data class PaymentConfirmRequest(val orderId: Long, val amount: String, val paymentKey: String)
