package com.koosco.orderservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.orderservice.application.command.MarkOrderPaymentPendingCommand
import com.koosco.orderservice.application.port.OrderRepository
import com.koosco.orderservice.application.port.OrderStatusHistoryRepository
import com.koosco.orderservice.common.error.OrderErrorCode
import com.koosco.orderservice.domain.entity.OrderStatusHistory
import com.koosco.orderservice.domain.enums.OrderStatus
import com.koosco.orderservice.domain.exception.InvalidOrderStatus
import org.springframework.transaction.annotation.Transactional

@UseCase
class MarkOrderPaymentPendingUseCase(
    private val orderRepository: OrderRepository,
    private val orderStatusHistoryRepository: OrderStatusHistoryRepository,
) {

    @Transactional
    fun execute(command: MarkOrderPaymentPendingCommand) {
        val order = orderRepository.findById(command.orderId)
            ?: throw NotFoundException(
                OrderErrorCode.ORDER_NOT_FOUND,
            )

        val previousStatus = order.status

        when (order.status) {
            OrderStatus.PAYMENT_PENDING -> return
            OrderStatus.RESERVED -> {}
            OrderStatus.CREATED -> {
                order.markReserved()
                orderStatusHistoryRepository.save(
                    OrderStatusHistory.create(
                        orderId = order.id!!,
                        fromStatus = OrderStatus.CREATED,
                        toStatus = OrderStatus.RESERVED,
                    ),
                )
            }
            else -> {
                throw InvalidOrderStatus("cannot mark payment pending. status=${order.status}")
            }
        }

        order.markPaymentPending()

        orderRepository.save(order)

        orderStatusHistoryRepository.save(
            OrderStatusHistory.create(
                orderId = order.id!!,
                fromStatus = previousStatus,
                toStatus = OrderStatus.PAYMENT_PENDING,
            ),
        )
    }
}
