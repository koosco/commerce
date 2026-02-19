package com.koosco.userservice.application.command

data class CreateAddressCommand(
    val userId: Long,
    val label: String,
    val recipient: String,
    val phone: String,
    val zipCode: String,
    val address: String,
    val addressDetail: String,
    val isDefault: Boolean,
)

data class GetAddressesCommand(val userId: Long)

data class DeleteAddressCommand(val userId: Long, val addressId: Long)
