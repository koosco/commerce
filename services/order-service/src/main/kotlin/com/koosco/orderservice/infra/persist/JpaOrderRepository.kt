package com.koosco.orderservice.infra.persist

import com.koosco.orderservice.domain.entity.Order
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface JpaOrderRepository : JpaRepository<Order, Long> {

    fun findByUserId(userId: Long): List<Order>

    fun findByUserId(userId: Long, pageable: Pageable): Page<Order>
}
