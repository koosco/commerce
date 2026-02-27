package com.koosco.catalogservice.application.port

import com.koosco.catalogservice.domain.entity.DiscountPolicy

interface DiscountPolicyRepository {
    fun save(discountPolicy: DiscountPolicy): DiscountPolicy

    fun findOrNull(id: Long): DiscountPolicy?

    fun findByProductId(productId: Long): List<DiscountPolicy>

    fun delete(discountPolicy: DiscountPolicy)
}
