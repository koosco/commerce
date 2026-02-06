package com.koosco.orderservice.order.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.orderservice.order.application.command.RefundOrderItemsCommand
import com.koosco.orderservice.order.application.port.IntegrationEventProducer
import com.koosco.orderservice.order.application.port.OrderRepository
import com.koosco.orderservice.order.application.result.RefundResult
import org.springframework.transaction.annotation.Transactional

@UseCase
class RefundOrderItemsUseCase(
    private val orderRepository: OrderRepository,
    private val integrationEventProducer: IntegrationEventProducer,
) {

    @Transactional
    fun execute(command: RefundOrderItemsCommand): RefundResult {
        // 주문이 존재하는지 확인
        val order = orderRepository.findById(command.orderId)
            ?: throw IllegalArgumentException("주문을 찾을 수 없습니다. orderId: ${command.orderId}")

        // 환불 진행
        val totalRefundAmount = order.refundAll(command.refundItemIds)

        // 도메인 이벤트 발행
        val savedOrder = orderRepository.save(order)

        // TODO : 환불 flow 진행

        return RefundResult(
            orderId = savedOrder.id!!,
            refundedItemIds = command.refundItemIds,
            totalRefundAmount = totalRefundAmount.amount,
            newStatus = savedOrder.status,
        )
    }
}
