package com.koosco.catalogservice.api.response

import com.koosco.catalogservice.application.result.ProductInfo
import com.koosco.catalogservice.application.result.ProductInfo.ProductOptionGroupInfo
import com.koosco.catalogservice.application.result.ProductInfo.ProductOptionInfo
import com.koosco.catalogservice.domain.entity.ProductOption
import com.koosco.catalogservice.domain.entity.ProductOptionGroup
import com.koosco.catalogservice.domain.enums.ProductStatus

data class ProductListResponse(
    val id: Long,
    val name: String,
    val originalPrice: Long,
    val sellingPrice: Long,
    val discountRate: Int,
    val status: ProductStatus,
    val categoryId: Long?,
    val thumbnailImageUrl: String?,
    val brandId: Long?,
    val brandName: String?,
    val averageRating: Double,
    val reviewCount: Int,
    val viewCount: Long,
    val orderCount: Long,
) {
    companion object {
        fun from(productInfo: ProductInfo): ProductListResponse = ProductListResponse(
            id = productInfo.id,
            name = productInfo.name,
            originalPrice = productInfo.price,
            sellingPrice = productInfo.sellingPrice,
            discountRate = productInfo.discountRate,
            status = productInfo.status,
            categoryId = productInfo.categoryId,
            thumbnailImageUrl = productInfo.thumbnailImageUrl,
            brandId = productInfo.brandId,
            brandName = productInfo.brandName,
            averageRating = productInfo.averageRating,
            reviewCount = productInfo.reviewCount,
            viewCount = productInfo.viewCount,
            orderCount = productInfo.orderCount,
        )
    }
}

data class ProductDetailResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val originalPrice: Long,
    val sellingPrice: Long,
    val discountRate: Int,
    val status: ProductStatus,
    val categoryId: Long?,
    val thumbnailImageUrl: String?,
    val brandId: Long?,
    val brandName: String?,
    val averageRating: Double,
    val reviewCount: Int,
    val optionGroups: List<ProductOptionGroupResponse>,
) {
    companion object {
        fun from(productInfo: ProductInfo): ProductDetailResponse = ProductDetailResponse(
            id = productInfo.id,
            name = productInfo.name,
            description = productInfo.description,
            originalPrice = productInfo.price,
            sellingPrice = productInfo.sellingPrice,
            discountRate = productInfo.discountRate,
            status = productInfo.status,
            categoryId = productInfo.categoryId,
            thumbnailImageUrl = productInfo.thumbnailImageUrl,
            brandId = productInfo.brandId,
            brandName = productInfo.brandName,
            averageRating = productInfo.averageRating,
            reviewCount = productInfo.reviewCount,
            optionGroups = productInfo.optionGroups.map { ProductOptionGroupResponse.from(it) },
        )
    }
}

data class ProductOptionGroupResponse(val id: Long, val name: String, val options: List<ProductOptionResponse>) {
    companion object {
        fun from(group: ProductOptionGroup): ProductOptionGroupResponse = ProductOptionGroupResponse(
            id = group.id!!,
            name = group.name,
            options = group.options.map { ProductOptionResponse.from(it) },
        )

        fun from(groupInfo: ProductOptionGroupInfo): ProductOptionGroupResponse = ProductOptionGroupResponse(
            id = groupInfo.id,
            name = groupInfo.name,
            options = groupInfo.options.map { ProductOptionResponse.from(it) },
        )
    }
}

data class ProductOptionResponse(val id: Long, val name: String, val additionalPrice: Long) {
    companion object {
        fun from(option: ProductOption): ProductOptionResponse = ProductOptionResponse(
            id = option.id!!,
            name = option.name,
            additionalPrice = option.additionalPrice,
        )

        fun from(optionInfo: ProductOptionInfo): ProductOptionResponse = ProductOptionResponse(
            id = optionInfo.id,
            name = optionInfo.name,
            additionalPrice = optionInfo.additionalPrice,
        )
    }
}
