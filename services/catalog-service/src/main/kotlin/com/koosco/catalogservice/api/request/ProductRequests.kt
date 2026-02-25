package com.koosco.catalogservice.api.request

import com.koosco.catalogservice.application.command.CreateProductCommand
import com.koosco.catalogservice.application.command.UpdateProductCommand
import com.koosco.catalogservice.domain.enums.ProductStatus
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import kotlin.collections.map

/**
 * Create Request
 */
data class ProductCreateRequest(
    @field:NotBlank(message = "Product name is required")
    val name: String,

    val description: String?,

    @field:NotNull(message = "Price is required")
    @field:Min(value = 0, message = "Price must be non-negative")
    val price: Long,

    val status: ProductStatus = ProductStatus.ACTIVE,

    val categoryId: Long?,

    val thumbnailImageUrl: String?,

    val brandId: Long?,

    @field:Valid
    val optionGroups: List<ProductOptionGroup> = emptyList(),

    val idempotencyKey: String? = null,
) {
    data class ProductOptionGroup(
        @field:NotBlank(message = "Option group name is required")
        val name: String,
        val ordering: Int = 0,
        @field:Valid
        val options: List<ProductOption> = emptyList(),
    ) {
        fun toCommand(): CreateProductCommand.ProductOptionGroup = CreateProductCommand.ProductOptionGroup(
            name = name,
            ordering = ordering,
            options = options.map { it.toCommand() },
        )
    }

    data class ProductOption(
        @field:NotBlank(message = "Option name is required")
        val name: String,
        @field:Min(value = 0, message = "Additional price must be non-negative")
        val additionalPrice: Long = 0,
        val ordering: Int = 0,
    ) {
        fun toCommand(): CreateProductCommand.ProductOption = CreateProductCommand.ProductOption(
            name = name,
            additionalPrice = additionalPrice,
            ordering = ordering,
        )
    }

    fun toCommand(): CreateProductCommand = CreateProductCommand(
        name = name,
        description = description,
        price = price,
        status = status,
        categoryId = categoryId,
        thumbnailImageUrl = thumbnailImageUrl,
        brandId = brandId,
        optionGroups = optionGroups.map { it.toCommand() },
        idempotencyKey = idempotencyKey,
    )
}

/**
 * Update Request
 */
data class ProductUpdateRequest(
    val name: String?,
    val description: String?,
    @field:Min(value = 0, message = "Price must be non-negative")
    val price: Long?,
    val status: ProductStatus?,
    val categoryId: Long?,
    val thumbnailImageUrl: String?,
    val brandId: Long?,
) {
    fun toCommand(productId: Long): UpdateProductCommand = UpdateProductCommand(
        productId = productId,
        name = name,
        description = description,
        price = price,
        status = status,
        categoryId = categoryId,
        thumbnailImageUrl = thumbnailImageUrl,
        brandId = brandId,
    )
}
