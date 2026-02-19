package com.koosco.orderservice.infra.persist

import com.koosco.orderservice.application.port.OrderRepository
import com.koosco.orderservice.domain.entity.Order
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class OrderRepositoryAdapter(private val jpaOrderRepository: JpaOrderRepository) : OrderRepository {

    override fun save(order: Order): Order = jpaOrderRepository.save(order)

    override fun findById(orderId: Long): Order? = jpaOrderRepository.findByIdOrNull(orderId)

    override fun findByUserId(userId: Long): List<Order> = jpaOrderRepository.findByUserId(userId)

    override fun findByUserId(userId: Long, pageable: Pageable): Page<Order> =
        jpaOrderRepository.findByUserId(userId, pageable)
}
