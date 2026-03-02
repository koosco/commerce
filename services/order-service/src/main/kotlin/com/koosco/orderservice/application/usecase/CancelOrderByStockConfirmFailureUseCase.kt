package com.koosco.orderservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.event.IntegrationEventProducer
import com.koosco.common.core.exception.NotFoundException
import com.koosco.common.core.messaging.MessageContext
import com.koosco.orderservice.application.port.OrderRepository
import com.koosco.orderservice.application.port.OrderStatusHistoryRepository
import com.koosco.orderservice.common.error.OrderErrorCode
import com.koosco.orderservice.contract.outbound.order.OrderCancelledEvent
import com.koosco.orderservice.domain.entity.OrderStatusHistory
import com.koosco.orderservice.domain.enums.OrderCancelReason
import com.koosco.orderservice.domain.enums.OrderStatus
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

@UseCase
class CancelOrderByStockConfirmFailureUseCase(
    private val orderRepository: OrderRepository,
    private val orderStatusHistoryRepository: OrderStatusHistoryRepository,
    private val integrationEventProducer: IntegrationEventProducer,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun execute(orderId: Long, context: MessageContext) {
        val order = orderRepository.findById(orderId)
            ?: throw NotFoundException(
                OrderErrorCode.ORDER_NOT_FOUND,
                "주문을 찾을 수 없습니다. orderId: $orderId",
            )

        if (order.status == OrderStatus.CANCELLED) {
            logger.info("이미 취소된 주문입니다. orderId={}", orderId)
            return
        }

        val previousStatus = order.status

        order.cancelByStockConfirmFailure()
        orderRepository.save(order)

        orderStatusHistoryRepository.save(
            OrderStatusHistory.create(
                orderId = order.id!!,
                fromStatus = previousStatus,
                toStatus = OrderStatus.CANCELLED,
                reason = OrderCancelReason.STOCK_CONFIRM_FAILED.name,
            ),
        )

        integrationEventProducer.publish(
            OrderCancelledEvent(
                orderId = order.id!!,
                reason = OrderCancelReason.STOCK_CONFIRM_FAILED,
                items = order.items.map {
                    OrderCancelledEvent.CancelledItem(
                        it.skuId,
                        it.qty,
                    )
                },
                correlationId = context.correlationId,
                causationId = context.causationId,
            ),
        )

        logger.info("재고 확정 실패로 주문 취소 처리 완료: orderId={}", orderId)
    }
}
