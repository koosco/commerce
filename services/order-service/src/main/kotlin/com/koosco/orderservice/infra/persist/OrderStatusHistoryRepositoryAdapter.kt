package com.koosco.orderservice.infra.persist

import com.koosco.orderservice.application.port.OrderStatusHistoryRepository
import com.koosco.orderservice.domain.entity.OrderStatusHistory
import org.springframework.stereotype.Repository

@Repository
class OrderStatusHistoryRepositoryAdapter(
    private val jpaOrderStatusHistoryRepository: JpaOrderStatusHistoryRepository,
) : OrderStatusHistoryRepository {

    override fun save(history: OrderStatusHistory): OrderStatusHistory = jpaOrderStatusHistoryRepository.save(history)

    override fun findByOrderId(orderId: Long): List<OrderStatusHistory> =
        jpaOrderStatusHistoryRepository.findByOrderIdOrderByCreatedAtAsc(orderId)
}
