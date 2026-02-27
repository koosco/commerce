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
    val averageRating: Double = 0.0,
    val reviewCount: Int = 0,
    val viewCount: Long = 0,
    val orderCount: Long = 0,
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
        fun from(product: Product, brandName: String? = null, promotionDiscountPrice: Long? = null): ProductInfo {
            val sellingPrice = promotionDiscountPrice
                ?: product.calculateSellingPrice()
            val discountRate = if (promotionDiscountPrice != null && product.price > 0) {
                ((product.price - promotionDiscountPrice) * 100 / product.price).toInt()
            } else {
                product.calculateDiscountRate()
            }

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
                averageRating = product.averageRating,
                reviewCount = product.reviewCount,
                viewCount = product.viewCount,
                orderCount = product.orderCount,
                optionGroups = product.optionGroups.map { ProductOptionGroupInfo.from(it) },
            )
        }
    }
}
