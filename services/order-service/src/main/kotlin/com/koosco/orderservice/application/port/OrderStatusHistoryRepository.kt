package com.koosco.orderservice.application.port

import com.koosco.orderservice.domain.entity.OrderStatusHistory

interface OrderStatusHistoryRepository {

    fun save(history: OrderStatusHistory): OrderStatusHistory

    fun findByOrderId(orderId: Long): List<OrderStatusHistory>
}
