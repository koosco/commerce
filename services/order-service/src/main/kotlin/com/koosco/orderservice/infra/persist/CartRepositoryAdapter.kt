package com.koosco.orderservice.infra.persist

import com.koosco.orderservice.application.port.CartRepository
import com.koosco.orderservice.domain.entity.Cart
import org.springframework.stereotype.Repository

@Repository
class CartRepositoryAdapter(private val jpaCartRepository: JpaCartRepository) : CartRepository {

    override fun findByUserId(userId: Long): Cart? = jpaCartRepository.findByUserId(userId)

    override fun save(cart: Cart): Cart = jpaCartRepository.save(cart)
}
