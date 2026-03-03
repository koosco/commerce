package com.koosco.searchservice.contract.inbound.catalog

data class ProductChangedEvent(
    val productId: Long,
    val name: String,
    val description: String? = null,
    val price: Long,
    val sellingPrice: Long,
    val categoryId: Long? = null,
    val categoryName: String? = null,
    val brandId: Long? = null,
    val brandName: String? = null,
    val thumbnailImageUrl: String? = null,
    val status: String,
)
