package com.koosco.orderservice.order.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.common.core.messaging.MessageContext
import com.koosco.orderservice.common.error.OrderErrorCode
import com.koosco.orderservice.order.application.contract.outbound.order.OrderCancelledEvent
import com.koosco.orderservice.order.application.port.IntegrationEventPublisher
import com.koosco.orderservice.order.application.port.OrderRepository
import com.koosco.orderservice.order.domain.OrderStatus
import com.koosco.orderservice.order.domain.enums.OrderCancelReason
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

/**
 * 재고 확정 실패로 인한 주문 취소 UseCase
 *
 * trigger: inventory-service 재고 확정 실패
 *
 * 1) order-service
 * - 주문 상태를 CANCELLED로 변경 (PAID 상태에서만 가능)
 * 2) inventory-service
 * - OrderCancelledEvent를 통해 예약했던 재고를 예약 해제
 * 3) payment-service
 * - OrderCancelledEvent를 통해 환불 처리 (STOCK_CONFIRM_FAILED 사유)
 */
@UseCase
class CancelOrderByStockConfirmFailureUseCase(
    private val orderRepository: OrderRepository,
    private val integrationEventPublisher: IntegrationEventPublisher,
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

        order.cancelByStockConfirmFailure()
        orderRepository.save(order)

        // Integration event 발행 - inventory-service에서 예약 해제, payment-service에서 환불
        integrationEventPublisher.publish(
            OrderCancelledEvent(
                orderId = order.id!!,
                reason = OrderCancelReason.STOCK_CONFIRM_FAILED,
                items = order.items.map {
                    OrderCancelledEvent.CancelledItem(
                        it.skuId,
                        it.quantity,
                    )
                },
                correlationId = context.correlationId,
                causationId = context.causationId,
            ),
        )

        logger.info("재고 확정 실패로 주문 취소 처리 완료: orderId={}", orderId)
    }
}
