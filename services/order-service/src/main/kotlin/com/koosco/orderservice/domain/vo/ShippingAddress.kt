package com.koosco.orderservice.domain.vo

data class ShippingAddress(
    val recipient: String,
    val phone: String,
    val zipCode: String,
    val address: String,
    val addressDetail: String,
)
