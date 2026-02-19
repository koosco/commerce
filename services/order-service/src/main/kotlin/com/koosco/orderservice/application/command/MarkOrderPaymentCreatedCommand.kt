package com.koosco.orderservice.application.command

/**
 * fileName       : MarkOrderPaymentCreatedCommand
 * author         : koo
 * date           : 2025. 12. 24. 오후 9:30
 * description    :
 */
data class MarkOrderPaymentCreatedCommand(val orderId: Long, val paymentId: String)
