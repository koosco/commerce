package com.koosco.orderservice.order.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.orderservice.common.MessageContext
import com.koosco.orderservice.common.error.OrderErrorCode
import com.koosco.orderservice.order.application.command.MarkOrderPaymentCreatedCommand
import com.koosco.orderservice.order.application.port.OrderRepository
import com.koosco.orderservice.order.domain.OrderStatus
import com.koosco.orderservice.order.domain.exception.InvalidOrderStatus
import org.springframework.transaction.annotation.Transactional

/**
 * fileName       : MarkOrderPaymentCreatedUseCase
 * author         : koo
 * date           : 2025. 12. 24. 오후 9:27
 * description    :
 */
@UseCase
class MarkOrderPaymentCreatedUseCase(private val orderRepository: OrderRepository) {

    @Transactional
    fun execute(command: MarkOrderPaymentCreatedCommand, message: MessageContext) {
        val order = orderRepository.findById(command.orderId)
            ?: throw NotFoundException(
                OrderErrorCode.ORDER_NOT_FOUND,
            )

        when (order.status) {
            OrderStatus.PAYMENT_PENDING -> return // 이미 처리된 이벤트
            OrderStatus.RESERVED -> {
                // pending으로 상태 변경
            }

            OrderStatus.CREATED -> {
                // 정상 경로: CREATED -> RESERVED -> PAYMENT_PENDING
                order.markReserved()
            }

            else -> {
                // INIT/PAID/CANCELLED 등은 비정상
                throw InvalidOrderStatus("cannot mark payment pending. status=${order.status}")
            }
        }

        order.markPaymentCreated()

        orderRepository.save(order)
    }
}
