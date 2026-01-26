package com.koosco.orderservice.order.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.common.core.messaging.MessageContext
import com.koosco.orderservice.common.error.OrderErrorCode
import com.koosco.orderservice.order.application.command.MarkOrderFailedCommand
import com.koosco.orderservice.order.application.port.OrderRepository
import com.koosco.orderservice.order.domain.OrderStatus
import com.koosco.orderservice.order.domain.enums.OrderCancelReason
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

/**
 * 재고 예약 실패로 인한 주문 실패 처리
 *
 * trigger: inventory-service 재고 예약 실패
 *
 * - order-service: 주문 상태를 FAILED로 변경
 *
 * Note: 재고가 예약되지 않았으므로 보상 트랜잭션(Integration Event 발행)이 필요하지 않음
 */
@UseCase
class CancelOrderByStockFailureUseCase(private val orderRepository: OrderRepository) {
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

        order.markFailed(OrderCancelReason.STOCK_RESERVATION_FAILED)
        orderRepository.save(order)

        logger.info(
            "주문 재고 예약 실패로 실패 처리 완료: orderId={}, reason={}",
            command.orderId,
            command.reason,
        )
    }
}
