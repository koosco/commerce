package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.domain.entity.DiscountPolicy
import org.springframework.data.jpa.repository.JpaRepository

interface JpaDiscountPolicyRepository : JpaRepository<DiscountPolicy, Long> {
    fun findByProductId(productId: Long): List<DiscountPolicy>
}
