package com.koosco.userservice.api

import com.koosco.userservice.application.command.CreateAddressCommand
import jakarta.validation.constraints.NotBlank

data class CreateAddressRequest(
    @field:NotBlank(message = "배송지명은 공백일 수 없습니다.")
    val label: String,

    @field:NotBlank(message = "수령인은 공백일 수 없습니다.")
    val recipient: String,

    @field:NotBlank(message = "연락처는 공백일 수 없습니다.")
    val phone: String,

    @field:NotBlank(message = "우편번호는 공백일 수 없습니다.")
    val zipCode: String,

    @field:NotBlank(message = "주소는 공백일 수 없습니다.")
    val address: String,

    @field:NotBlank(message = "상세주소는 공백일 수 없습니다.")
    val addressDetail: String,

    val isDefault: Boolean = false,

    val idempotencyKey: String? = null,
) {
    fun toCommand(userId: Long): CreateAddressCommand = CreateAddressCommand(
        userId = userId,
        label = label,
        recipient = recipient,
        phone = phone,
        zipCode = zipCode,
        address = address,
        addressDetail = addressDetail,
        isDefault = isDefault,
        idempotencyKey = idempotencyKey,
    )
}
