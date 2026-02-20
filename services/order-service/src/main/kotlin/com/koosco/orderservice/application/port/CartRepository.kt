package com.koosco.orderservice.application.port

import com.koosco.orderservice.domain.entity.Cart

interface CartRepository {

    fun findByUserId(userId: Long): Cart?

    fun save(cart: Cart): Cart
}
