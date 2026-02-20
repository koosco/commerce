package com.koosco.orderservice.infra.persist

import com.koosco.orderservice.domain.entity.OrderStatusHistory
import org.springframework.data.jpa.repository.JpaRepository

interface JpaOrderStatusHistoryRepository : JpaRepository<OrderStatusHistory, Long> {

    fun findByOrderIdOrderByCreatedAtAsc(orderId: Long): List<OrderStatusHistory>
}
