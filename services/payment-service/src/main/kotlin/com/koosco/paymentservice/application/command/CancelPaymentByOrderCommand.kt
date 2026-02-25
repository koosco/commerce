package com.koosco.paymentservice.application.command

data class CancelPaymentByOrderCommand(val orderId: Long, val cancelAmount: Long)
