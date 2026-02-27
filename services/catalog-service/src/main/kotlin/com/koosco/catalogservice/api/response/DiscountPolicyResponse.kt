package com.koosco.catalogservice.api.response

import com.koosco.catalogservice.application.result.DiscountPolicyResult
import com.koosco.catalogservice.domain.enums.DiscountType
import java.time.LocalDateTime

data class DiscountPolicyResponse(
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
        fun from(result: DiscountPolicyResult): DiscountPolicyResponse = DiscountPolicyResponse(
            id = result.id,
            productId = result.productId,
            name = result.name,
            discountType = result.discountType,
            discountValue = result.discountValue,
            startAt = result.startAt,
            endAt = result.endAt,
            active = result.active,
            createdAt = result.createdAt,
        )
    }
}
