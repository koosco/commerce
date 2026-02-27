package com.koosco.catalogservice.application.result

import com.koosco.catalogservice.domain.entity.Product
import com.koosco.catalogservice.domain.entity.ProductOption
import com.koosco.catalogservice.domain.entity.ProductOptionGroup
import com.koosco.catalogservice.domain.enums.ProductStatus

data class ProductInfo(
    val id: Long,
    val name: String,
    val description: String?,
    val price: Long,
    val sellingPrice: Long,
    val discountRate: Int,
    val status: ProductStatus,
    val categoryId: Long?,
    val thumbnailImageUrl: String?,
    val brandId: Long?,
    val brandName: String? = null,
    val optionGroups: List<ProductOptionGroupInfo> = emptyList(),
) {
    data class ProductOptionGroupInfo(
        val id: Long,
        val name: String,
        val ordering: Int,
        val options: List<ProductOptionInfo>,
    ) {
        companion object {
            fun from(group: ProductOptionGroup): ProductOptionGroupInfo = ProductOptionGroupInfo(
                id = group.id!!,
                name = group.name,
                ordering = group.ordering,
                options = group.options.map { ProductOptionInfo.from(it) },
            )
        }
    }

    data class ProductOptionInfo(val id: Long, val name: String, val additionalPrice: Long, val ordering: Int) {
        companion object {
            fun from(option: ProductOption): ProductOptionInfo = ProductOptionInfo(
                id = option.id!!,
                name = option.name,
                additionalPrice = option.additionalPrice,
                ordering = option.ordering,
            )
        }
    }

    companion object {
        fun from(product: Product, brandName: String? = null): ProductInfo {
            val sellingPrice = product.calculateSellingPrice()
            val discountRate = product.calculateDiscountRate()

            return ProductInfo(
                id = product.id!!,
                name = product.name,
                description = product.description,
                price = product.price,
                sellingPrice = sellingPrice,
                discountRate = discountRate,
                status = product.status,
                categoryId = product.categoryId,
                thumbnailImageUrl = product.thumbnailImageUrl,
                brandId = product.brandId,
                brandName = brandName,
                optionGroups = product.optionGroups.map { ProductOptionGroupInfo.from(it) },
            )
        }
    }
}
