package com.koosco.userservice.application.dto

import com.koosco.userservice.domain.entity.Address

data class AddressDto(
    val id: Long,
    val label: String,
    val recipient: String,
    val phone: String,
    val zipCode: String,
    val address: String,
    val addressDetail: String,
    val isDefault: Boolean,
) {
    companion object {
        fun from(address: Address): AddressDto = AddressDto(
            id = address.id!!,
            label = address.label,
            recipient = address.recipient,
            phone = address.phone,
            zipCode = address.zipCode,
            address = address.address,
            addressDetail = address.addressDetail,
            isDefault = address.isDefault,
        )
    }
}
