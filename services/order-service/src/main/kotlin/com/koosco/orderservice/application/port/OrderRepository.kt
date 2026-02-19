package com.koosco.orderservice.application.port

import com.koosco.orderservice.domain.entity.Order
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OrderRepository {

    fun save(order: Order): Order

    fun findById(orderId: Long): Order?

    fun findByUserId(userId: Long): List<Order>

    fun findByUserId(userId: Long, pageable: Pageable): Page<Order>
}
