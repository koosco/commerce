package com.koosco.orderservice.order.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.orderservice.common.MessageContext
import com.koosco.orderservice.common.error.OrderErrorCode
import com.koosco.orderservice.order.application.command.MarkOrderPaidCommand
import com.koosco.orderservice.order.application.contract.outbound.order.OrderConfirmedEvent
import com.koosco.orderservice.order.application.port.IntegrationEventPublisher
import com.koosco.orderservice.order.application.port.OrderRepository
import com.koosco.orderservice.order.domain.event.OrderPaid
import com.koosco.orderservice.order.domain.vo.Money
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
    private val integrationEventPublisher: IntegrationEventPublisher,
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

        val paid = order.pullDomainEvents().filterIsInstance<OrderPaid>().singleOrNull()
            ?: throw IllegalStateException("No OrderPaidEvent in this UoW")

        integrationEventPublisher.publish(
            OrderConfirmedEvent(
                orderId = paid.orderId,
                items = paid.items.map { OrderConfirmedEvent.ConfirmedItem(it.skuId, it.quantity) },
                correlationId = context.correlationId,
                causationId = context.causationId,
            ),
        )
    }
}
