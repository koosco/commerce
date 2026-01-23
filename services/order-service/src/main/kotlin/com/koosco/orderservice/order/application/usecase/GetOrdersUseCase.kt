package com.koosco.orderservice.order.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.orderservice.order.application.port.OrderRepository
import com.koosco.orderservice.order.application.result.OrderListResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.transaction.annotation.Transactional

@UseCase
class GetOrdersUseCase(private val orderRepository: OrderRepository) {

    @Transactional(readOnly = true)
    fun execute(userId: Long): List<OrderListResult> {
        val orders = orderRepository.findByUserId(userId)
        return orders.map { OrderListResult.from(it) }
    }

    @Transactional(readOnly = true)
    fun execute(userId: Long, pageable: Pageable): Page<OrderListResult> {
        val orders = orderRepository.findByUserId(userId, pageable)
        return orders.map { OrderListResult.from(it) }
    }
}
