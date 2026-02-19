package com.koosco.orderservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.common.core.messaging.MessageContext
import com.koosco.orderservice.application.command.MarkOrderPaidCommand
import com.koosco.orderservice.application.contract.outbound.order.OrderConfirmedEvent
import com.koosco.orderservice.application.port.IntegrationEventProducer
import com.koosco.orderservice.application.port.OrderRepository
import com.koosco.orderservice.common.error.OrderErrorCode
import com.koosco.orderservice.domain.vo.Money
import org.springframework.transaction.annotation.Transactional

/**
 * fileName       : ConfirmOrderPaymentUseCase
 * author         : koo
 * date           : 2025. 12. 22. 오전 5:47
 * description    : 결제 완료 상태 변경 flow
 */
/**
 * trigger = payment service 결제 성공
 *
 * 1) inventory service
 * - success =
 */
@UseCase
class MarkOrderPaidUseCase(
    private val orderRepository: OrderRepository,
    private val integrationEventProducer: IntegrationEventProducer,
) {
    @Transactional
    fun execute(command: MarkOrderPaidCommand, context: MessageContext) {
        val order = orderRepository.findById(command.orderId)
            ?: throw NotFoundException(
                OrderErrorCode.ORDER_NOT_FOUND,
                "주문을 찾을 수 없습니다. orderId: ${command.orderId}",
            )

        // 결제 금액이 동일하다면 paid 처리
        order.markPaid(Money(command.paidAmount))

        orderRepository.save(order)

        // Integration event 직접 생성 및 발행
        integrationEventProducer.publish(
            OrderConfirmedEvent(
                orderId = order.id!!,
                items = order.items.map { OrderConfirmedEvent.ConfirmedItem(it.skuId, it.quantity) },
                correlationId = context.correlationId,
                causationId = context.causationId,
            ),
        )
    }
}
