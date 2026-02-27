package com.koosco.catalogservice.application.result

import com.koosco.catalogservice.domain.entity.DiscountPolicy
import com.koosco.catalogservice.domain.enums.DiscountType
import java.time.LocalDateTime

data class DiscountPolicyResult(
    val id: Long,
    val productId: Long,
    val name: String,
    val discountType: DiscountType,
    val discountValue: Long,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val active: Boolean,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(policy: DiscountPolicy, now: LocalDateTime = LocalDateTime.now()): DiscountPolicyResult =
            DiscountPolicyResult(
                id = policy.id!!,
                productId = policy.product.id!!,
                name = policy.name,
                discountType = policy.discountType,
                discountValue = policy.discountValue,
                startAt = policy.startAt,
                endAt = policy.endAt,
                active = policy.isActiveAt(now),
                createdAt = policy.createdAt,
            )
    }
}
