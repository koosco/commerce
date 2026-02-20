package com.koosco.orderservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.common.core.messaging.MessageContext
import com.koosco.orderservice.application.command.MarkOrderFailedCommand
import com.koosco.orderservice.application.port.OrderRepository
import com.koosco.orderservice.application.port.OrderStatusHistoryRepository
import com.koosco.orderservice.common.error.OrderErrorCode
import com.koosco.orderservice.domain.entity.OrderStatusHistory
import com.koosco.orderservice.domain.enums.OrderCancelReason
import com.koosco.orderservice.domain.enums.OrderStatus
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

@UseCase
class CancelOrderByStockFailureUseCase(
    private val orderRepository: OrderRepository,
    private val orderStatusHistoryRepository: OrderStatusHistoryRepository,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun execute(command: MarkOrderFailedCommand, context: MessageContext) {
        val order = orderRepository.findById(command.orderId)
            ?: throw NotFoundException(
                OrderErrorCode.ORDER_NOT_FOUND,
                "주문을 찾을 수 없습니다. orderId: ${command.orderId}",
            )

        if (order.status == OrderStatus.FAILED) {
            logger.info("이미 실패 처리된 주문입니다. orderId={}", command.orderId)
            return
        }

        val previousStatus = order.status

        order.markFailed(OrderCancelReason.STOCK_RESERVATION_FAILED)
        orderRepository.save(order)

        orderStatusHistoryRepository.save(
            OrderStatusHistory.create(
                orderId = order.id!!,
                fromStatus = previousStatus,
                toStatus = OrderStatus.FAILED,
                reason = command.reason,
            ),
        )

        logger.info(
            "주문 재고 예약 실패로 실패 처리 완료: orderId={}, reason={}",
            command.orderId,
            command.reason,
        )
    }
}
