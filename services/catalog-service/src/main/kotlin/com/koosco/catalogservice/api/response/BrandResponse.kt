package com.koosco.catalogservice.api.response

import com.koosco.catalogservice.application.result.BrandResult

data class BrandResponse(val id: Long, val name: String, val logoImageUrl: String?) {
    companion object {
        fun from(result: BrandResult): BrandResponse = BrandResponse(
            id = result.id,
            name = result.name,
            logoImageUrl = result.logoImageUrl,
        )
    }
}
