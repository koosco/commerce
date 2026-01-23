package com.koosco.catalogservice.product.application.command

import com.koosco.catalogservice.product.domain.enums.ProductStatus

data class CreateProductCommand(
    val name: String,
    val description: String?,
    val price: Long,
    val status: ProductStatus,
    val categoryId: Long?,
    val thumbnailImageUrl: String?,
    val brand: String?,
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
    val status: ProductStatus?,
    val categoryId: Long?,
    val thumbnailImageUrl: String?,
    val brand: String?,
)

data class DeleteProductCommand(val productId: Long)
