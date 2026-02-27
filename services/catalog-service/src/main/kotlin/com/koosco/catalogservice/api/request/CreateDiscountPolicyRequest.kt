package com.koosco.catalogservice.api.request

import com.koosco.catalogservice.application.command.CreateDiscountPolicyCommand
import com.koosco.catalogservice.domain.enums.DiscountType
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class CreateDiscountPolicyRequest(
    @field:NotBlank(message = "할인 정책 이름은 필수입니다.")
    val name: String,

    @field:NotNull(message = "할인 유형은 필수입니다.")
    val discountType: DiscountType,

    @field:NotNull(message = "할인 값은 필수입니다.")
    @field:Min(value = 1, message = "할인 값은 1 이상이어야 합니다.")
    val discountValue: Long,

    @field:NotNull(message = "할인 시작일은 필수입니다.")
    val startAt: LocalDateTime,

    @field:NotNull(message = "할인 종료일은 필수입니다.")
    val endAt: LocalDateTime,
) {
    fun toCommand(productId: Long): CreateDiscountPolicyCommand = CreateDiscountPolicyCommand(
        productId = productId,
        name = name,
        discountType = discountType,
        discountValue = discountValue,
        startAt = startAt,
        endAt = endAt,
    )
}
