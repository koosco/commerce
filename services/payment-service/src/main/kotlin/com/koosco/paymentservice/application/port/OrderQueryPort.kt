package com.koosco.paymentservice.application.port

interface OrderQueryPort {

    fun getOrder(orderId: Long): OrderInfo?

    data class OrderInfo(val orderId: Long, val status: String, val totalAmount: Long, val userId: Long)
}
