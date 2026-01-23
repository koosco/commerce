package com.koosco.paymentservice.application.command

/**
 * fileName       : CreatePaymentCommand
 * author         : koo
 * date           : 2025. 12. 24. 오전 6:04
 * description    :
 */
data class CreatePaymentCommand(val orderId: Long, val userId: Long, val amount: Long)
