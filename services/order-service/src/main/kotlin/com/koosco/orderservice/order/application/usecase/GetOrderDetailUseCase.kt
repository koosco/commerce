package com.koosco.orderservice.order.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.orderservice.order.application.port.OrderRepository
import com.koosco.orderservice.order.application.result.OrderDetailResult
import org.springframework.transaction.annotation.Transactional

@UseCase
class GetOrderDetailUseCase(private val orderRepository: OrderRepository) {

    @Transactional(readOnly = true)
    fun execute(orderId: Long): OrderDetailResult {
        val order = orderRepository.findById(orderId)
            ?: throw IllegalArgumentException("주문을 찾을 수 없습니다. orderId: $orderId")

        return OrderDetailResult.from(order)
    }
}
