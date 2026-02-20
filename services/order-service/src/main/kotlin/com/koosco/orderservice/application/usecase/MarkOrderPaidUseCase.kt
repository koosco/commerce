package com.koosco.orderservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import com.koosco.common.core.messaging.MessageContext
import com.koosco.orderservice.application.command.MarkOrderPaidCommand
import com.koosco.orderservice.application.port.IntegrationEventProducer
import com.koosco.orderservice.application.port.OrderRepository
import com.koosco.orderservice.application.port.OrderStatusHistoryRepository
import com.koosco.orderservice.common.error.OrderErrorCode
import com.koosco.orderservice.contract.outbound.order.OrderConfirmedEvent
import com.koosco.orderservice.domain.entity.OrderStatusHistory
import com.koosco.orderservice.domain.enums.OrderStatus
import com.koosco.orderservice.domain.vo.Money
import org.springframework.transaction.annotation.Transactional

@UseCase
class MarkOrderPaidUseCase(
    private val orderRepository: OrderRepository,
    private val orderStatusHistoryRepository: OrderStatusHistoryRepository,
    private val integrationEventProducer: IntegrationEventProducer,
) {
    @Transactional
    fun execute(command: MarkOrderPaidCommand, context: MessageContext) {
        val order = orderRepository.findById(command.orderId)
            ?: throw NotFoundException(
                OrderErrorCode.ORDER_NOT_FOUND,
                "주문을 찾을 수 없습니다. orderId: ${command.orderId}",
            )

        val previousStatus = order.status

        order.markPaid(Money(command.paidAmount))

        orderRepository.save(order)

        orderStatusHistoryRepository.save(
            OrderStatusHistory.create(
                orderId = order.id!!,
                fromStatus = previousStatus,
                toStatus = OrderStatus.PAID,
            ),
        )

        integrationEventProducer.publish(
            OrderConfirmedEvent(
                orderId = order.id!!,
                items = order.items.map { OrderConfirmedEvent.ConfirmedItem(it.skuId, it.qty) },
                correlationId = context.correlationId,
                causationId = context.causationId,
            ),
        )
    }
}
