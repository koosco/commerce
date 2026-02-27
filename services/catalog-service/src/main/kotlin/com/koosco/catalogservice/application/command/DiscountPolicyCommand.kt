package com.koosco.catalogservice.application.command

import com.koosco.catalogservice.domain.enums.DiscountType
import java.time.LocalDateTime

data class CreateDiscountPolicyCommand(
    val productId: Long,
    val name: String,
    val discountType: DiscountType,
    val discountValue: Long,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
)

data class UpdateDiscountPolicyCommand(val productId: Long, val discountPolicyId: Long, val name: String?)

data class DeleteDiscountPolicyCommand(val productId: Long, val discountPolicyId: Long)

data class GetDiscountPoliciesCommand(val productId: Long)
