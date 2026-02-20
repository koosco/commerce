package com.koosco.catalogservice.application.result

import com.koosco.catalogservice.domain.entity.Brand

data class BrandResult(val id: Long, val name: String, val logoImageUrl: String?) {
    companion object {
        fun from(brand: Brand): BrandResult = BrandResult(
            id = brand.id!!,
            name = brand.name,
            logoImageUrl = brand.logoImageUrl,
        )
    }
}
