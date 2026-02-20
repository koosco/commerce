package com.koosco.orderservice.infra.persist

import com.koosco.orderservice.domain.entity.Cart
import org.springframework.data.jpa.repository.JpaRepository

interface JpaCartRepository : JpaRepository<Cart, Long> {

    fun findByUserId(userId: Long): Cart?
}
