package com.koosco.orderservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.common.core.messaging.MessageContext
import com.koosco.orderservice.application.command.CancelOrderCommand
import com.koosco.orderservice.application.contract.outbound.order.OrderCancelledEvent
import com.koosco.orderservice.application.port.IntegrationEventProducer
import com.koosco.orderservice.application.port.OrderRepository
import com.koosco.orderservice.common.error.OrderErrorCode
import com.koosco.orderservice.domain.enums.OrderCancelReason
import com.koosco.orderservice.domain.enums.OrderStatus
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

/**
 * fileName       : CancelOrderByPaymentFailureUseCase
 * author         : koo
 * date           : 2025. 12. 22. 오전 5:47
 * description    : 결제 실패로 인한 주문 취소 flow
 */
/**
 * trigger : payment service 결제 실패
 *
 * 1) order-service
 * - 상품 상태를 실패로 변경
 * 2) inventory-service
 * - 예약했던 재고를 예약 해제
 */
@UseCase
class CancelOrderByPaymentFailureUseCase(
    private val orderRepository: OrderRepository,
    private val integrationEventProducer: IntegrationEventProducer,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun execute(command: CancelOrderCommand, context: MessageContext) {
        val order = orderRepository.findById(command.orderId)
            ?: throw NotFoundException(
                OrderErrorCode.ORDER_NOT_FOUND,
                "주문을 찾을 수 없습니다. orderId: ${command.orderId}",
            )

        if (order.status == OrderStatus.CANCELLED) {
            logger.info("이미 취소된 주문입니다. orderId={}", command.orderId)
            return
        }

        order.cancel(OrderCancelReason.PAYMENT_FAILED)

        orderRepository.save(order)

        // Integration event 직접 생성 및 발행
        integrationEventProducer.publish(
            OrderCancelledEvent(
                orderId = order.id!!,
                reason = OrderCancelReason.PAYMENT_FAILED,
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

        logger.info("주문 결제 실패로 취소 처리 완료: orderId={}", command.orderId)
    }
}
