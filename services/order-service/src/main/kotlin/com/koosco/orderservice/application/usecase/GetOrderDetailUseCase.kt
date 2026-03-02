package com.koosco.orderservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.ForbiddenException
import com.koosco.common.core.exception.NotFoundException
import com.koosco.orderservice.application.port.OrderRepository
import com.koosco.orderservice.application.result.OrderDetailResult
import com.koosco.orderservice.common.error.OrderErrorCode
import org.springframework.transaction.annotation.Transactional

@UseCase
class GetOrderDetailUseCase(private val orderRepository: OrderRepository) {

    @Transactional(readOnly = true)
    fun execute(orderId: Long, userId: Long): OrderDetailResult {
        val order = orderRepository.findById(orderId)
            ?: throw NotFoundException(OrderErrorCode.ORDER_NOT_FOUND)

        if (order.userId != userId) {
            throw ForbiddenException(OrderErrorCode.ORDER_ACCESS_DENIED)
        }

        return OrderDetailResult.from(order)
    }
}
