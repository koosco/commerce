package com.koosco.catalogservice.api.request

import com.koosco.catalogservice.application.command.CreateBrandCommand
import com.koosco.catalogservice.application.command.UpdateBrandCommand
import jakarta.validation.constraints.NotBlank

data class BrandCreateRequest(
    @field:NotBlank(message = "Brand name is required")
    val name: String,
    val logoImageUrl: String?,
) {
    fun toCommand(): CreateBrandCommand = CreateBrandCommand(
        name = name,
        logoImageUrl = logoImageUrl,
    )
}

data class BrandUpdateRequest(val name: String?, val logoImageUrl: String?) {
    fun toCommand(brandId: Long): UpdateBrandCommand = UpdateBrandCommand(
        brandId = brandId,
        name = name,
        logoImageUrl = logoImageUrl,
    )
}
