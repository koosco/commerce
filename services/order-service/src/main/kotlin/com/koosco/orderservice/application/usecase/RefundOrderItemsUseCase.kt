package com.koosco.orderservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.event.IntegrationEventProducer
import com.koosco.common.core.exception.NotFoundException
import com.koosco.orderservice.application.command.RefundOrderItemsCommand
import com.koosco.orderservice.application.port.OrderRepository
import com.koosco.orderservice.application.port.OrderStatusHistoryRepository
import com.koosco.orderservice.application.result.RefundOrderItemsResult
import com.koosco.orderservice.common.error.OrderErrorCode
import com.koosco.orderservice.contract.outbound.order.OrderRefundRequestedEvent
import com.koosco.orderservice.domain.entity.OrderStatusHistory
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@UseCase
class RefundOrderItemsUseCase(
    private val orderRepository: OrderRepository,
    private val orderStatusHistoryRepository: OrderStatusHistoryRepository,
    private val integrationEventProducer: IntegrationEventProducer,
) {

    @Transactional
    fun execute(command: RefundOrderItemsCommand): RefundOrderItemsResult {
        val order = orderRepository.findById(command.orderId)
            ?: throw NotFoundException(OrderErrorCode.ORDER_NOT_FOUND)

        val previousStatus = order.status
        val refundAmount = order.refundAll(command.itemIds)

        orderRepository.save(order)

        orderStatusHistoryRepository.save(
            OrderStatusHistory.create(
                orderId = order.id!!,
                fromStatus = previousStatus,
                toStatus = order.status,
                reason = "환불 요청: itemIds=${command.itemIds}",
            ),
        )

        integrationEventProducer.publish(
            OrderRefundRequestedEvent(
                orderId = order.id!!,
                userId = order.userId,
                refundAmount = refundAmount.amount,
                refundedItemIds = command.itemIds,
                correlationId = order.id.toString(),
                causationId = UUID.randomUUID().toString(),
            ),
        )

        return RefundOrderItemsResult(
            orderId = order.id!!,
            refundAmount = refundAmount.amount,
            refundedItemIds = command.itemIds,
            orderStatus = order.status,
        )
    }
}
