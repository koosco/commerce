package com.koosco.orderservice.domain.vo

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class ShippingAddress(
    @Column(name = "shipping_recipient", nullable = false)
    val recipient: String,

    @Column(name = "shipping_phone", nullable = false)
    val phone: String,

    @Column(name = "shipping_zip_code", nullable = false)
    val zipCode: String,

    @Column(name = "shipping_address", nullable = false)
    val address: String,

    @Column(name = "shipping_address_detail", nullable = false)
    val addressDetail: String,
)
