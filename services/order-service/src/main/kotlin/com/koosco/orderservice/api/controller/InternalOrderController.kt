package com.koosco.orderservice.api.controller

import com.koosco.common.core.exception.NotFoundException
import com.koosco.orderservice.application.port.OrderRepository
import com.koosco.orderservice.common.error.OrderErrorCode
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Hidden
@RestController
@RequestMapping("/internal/orders")
class InternalOrderController(private val orderRepository: OrderRepository) {

    @GetMapping("/{orderId}")
    fun getOrder(@PathVariable orderId: Long): InternalOrderResponse {
        val order = orderRepository.findById(orderId)
            ?: throw NotFoundException(OrderErrorCode.ORDER_NOT_FOUND)

        return InternalOrderResponse(
            orderId = order.id!!,
            status = order.status.name,
            totalAmount = order.totalAmount.amount,
            userId = order.userId,
        )
    }

    data class InternalOrderResponse(val orderId: Long, val status: String, val totalAmount: Long, val userId: Long)
}
