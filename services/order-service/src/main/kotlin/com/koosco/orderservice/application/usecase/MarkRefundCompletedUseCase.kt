package com.koosco.orderservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.common.core.messaging.MessageContext
import com.koosco.orderservice.application.command.MarkRefundCompletedCommand
import com.koosco.orderservice.application.port.OrderRepository
import com.koosco.orderservice.application.port.OrderStatusHistoryRepository
import com.koosco.orderservice.common.error.OrderErrorCode
import com.koosco.orderservice.domain.entity.OrderStatusHistory
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

@UseCase
class MarkRefundCompletedUseCase(
    private val orderRepository: OrderRepository,
    private val orderStatusHistoryRepository: OrderStatusHistoryRepository,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun execute(command: MarkRefundCompletedCommand, context: MessageContext) {
        val order = orderRepository.findById(command.orderId)
            ?: throw NotFoundException(OrderErrorCode.ORDER_NOT_FOUND)

        val previousStatus = order.status

        logger.info(
            "Refund completed for order: orderId=${order.id}, " +
                "canceledAmount=${command.canceledAmount}, " +
                "isFullyCanceled=${command.isFullyCanceled}, " +
                "currentStatus=$previousStatus",
        )

        orderStatusHistoryRepository.save(
            OrderStatusHistory.create(
                orderId = order.id!!,
                fromStatus = previousStatus,
                toStatus = order.status,
                reason = "결제 취소 완료: canceledAmount=${command.canceledAmount}, " +
                    "isFullyCanceled=${command.isFullyCanceled}",
            ),
        )

        orderRepository.save(order)
    }
}
