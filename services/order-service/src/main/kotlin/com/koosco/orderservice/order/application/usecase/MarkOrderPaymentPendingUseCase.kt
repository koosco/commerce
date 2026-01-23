package com.koosco.orderservice.order.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.orderservice.common.error.OrderErrorCode
import com.koosco.orderservice.order.application.command.MarkOrderPaymentPendingCommand
import com.koosco.orderservice.order.application.port.OrderRepository
import com.koosco.orderservice.order.domain.OrderStatus
import com.koosco.orderservice.order.domain.exception.InvalidOrderStatus
import org.springframework.transaction.annotation.Transactional

/**
 * fileName       : MarkOrderPaymentPendingUseCase
 * author         : koo
 * date           : 2025. 12. 22. 오전 6:26
 * description    : 재고 예약 이후 처리
 */
@UseCase
class MarkOrderPaymentPendingUseCase(private val orderRepository: OrderRepository) {

    @Transactional
    fun execute(command: MarkOrderPaymentPendingCommand) {
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

        order.markPaymentPending()

        orderRepository.save(order)
    }
}
