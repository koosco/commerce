package com.koosco.orderservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.orderservice.application.command.MarkOrderConfirmedCommand
import com.koosco.orderservice.application.port.OrderRepository
import com.koosco.orderservice.common.error.OrderErrorCode
import org.springframework.transaction.annotation.Transactional

/**
 * fileName       : MarkOrderConfirmedUseCase
 * author         : koo
 * date           : 2025. 12. 23. 오전 12:49
 * description    : 재고 확정 완료 flow (주문 마지막)
 */
@UseCase
class MarkOrderConfirmedUseCase(private val orderRepository: OrderRepository) {

    @Transactional
    fun execute(command: MarkOrderConfirmedCommand) {
        val order = orderRepository.findById(command.orderId)
            ?: throw NotFoundException(
                OrderErrorCode.ORDER_NOT_FOUND,
                "주문을 찾을 수 없습니다. orderId: ${command.orderId}",
            )

        order.confirmStock()

        orderRepository.save(order)
    }
}
