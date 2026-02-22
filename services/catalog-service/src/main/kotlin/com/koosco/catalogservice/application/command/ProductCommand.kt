package com.koosco.catalogservice.application.command

import com.koosco.catalogservice.domain.enums.ProductStatus

data class CreateProductCommand(
    val name: String,
    val description: String?,
    val price: Long,
    val status: ProductStatus,
    val categoryId: Long?,
    val thumbnailImageUrl: String?,
    val brandId: Long?,
    val optionGroups: List<ProductOptionGroup>,
) {
    data class ProductOptionGroup(val name: String, val ordering: Int = 0, val options: List<ProductOption>)

    data class ProductOption(val name: String, val additionalPrice: Long = 0, val ordering: Int = 0)
}

data class UpdateProductCommand(
    val productId: Long,
    val name: String?,
    val description: String?,
    val price: Long?,
    val categoryId: Long?,
    val thumbnailImageUrl: String?,
    val brandId: Long?,
)

data class DeleteProductCommand(val productId: Long)

data class ChangeProductStatusCommand(val productId: Long, val status: ProductStatus)
