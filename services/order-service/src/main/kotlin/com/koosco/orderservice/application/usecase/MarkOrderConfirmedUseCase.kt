package com.koosco.orderservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.event.BehaviorType
import com.koosco.common.core.event.UserBehaviorEvent
import com.koosco.common.core.exception.NotFoundException
import com.koosco.orderservice.application.command.MarkOrderConfirmedCommand
import com.koosco.orderservice.application.port.OrderRepository
import com.koosco.orderservice.application.port.OrderStatusHistoryRepository
import com.koosco.orderservice.application.port.UserBehaviorEventProducer
import com.koosco.orderservice.common.error.OrderErrorCode
import com.koosco.orderservice.domain.entity.OrderStatusHistory
import com.koosco.orderservice.domain.enums.OrderStatus
import org.springframework.transaction.annotation.Transactional

@UseCase
class MarkOrderConfirmedUseCase(
    private val orderRepository: OrderRepository,
    private val orderStatusHistoryRepository: OrderStatusHistoryRepository,
    private val userBehaviorEventProducer: UserBehaviorEventProducer,
) {

    @Transactional
    fun execute(command: MarkOrderConfirmedCommand) {
        val order = orderRepository.findById(command.orderId)
            ?: throw NotFoundException(
                OrderErrorCode.ORDER_NOT_FOUND,
                "주문을 찾을 수 없습니다. orderId: ${command.orderId}",
            )

        val previousStatus = order.status

        order.confirmStock()

        orderRepository.save(order)

        orderStatusHistoryRepository.save(
            OrderStatusHistory.create(
                orderId = order.id!!,
                fromStatus = previousStatus,
                toStatus = OrderStatus.CONFIRMED,
            ),
        )

        publishPurchaseEvents(order.userId, order.items)
    }

    private fun publishPurchaseEvents(userId: Long, items: List<com.koosco.orderservice.domain.entity.OrderItem>) {
        items.forEach { item ->
            userBehaviorEventProducer.publish(
                UserBehaviorEvent(
                    userId = userId,
                    behaviorType = BehaviorType.PURCHASE,
                    productId = item.skuId,
                    searchQuery = null,
                    metadata = mapOf("quantity" to item.qty.toString()),
                ),
            )
        }
    }
}
