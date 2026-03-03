package com.koosco.catalogservice.contract.outbound

import com.koosco.common.core.event.IntegrationEvent

data class ProductChangedEvent(
    val productId: Long,
    val name: String,
    val description: String?,
    val price: Long,
    val sellingPrice: Long,
    val categoryId: Long?,
    val categoryName: String?,
    val brandId: Long?,
    val brandName: String?,
    val thumbnailImageUrl: String?,
    val status: String,
    val averageRating: Double,
    val reviewCount: Int,
    val salesCount: Long,
    val viewCount: Long,
    val likeCount: Long,
) : IntegrationEvent {
    override val aggregateId: String get() = productId.toString()

    override fun getEventType(): String = "product.changed"

    override fun getSubject(): String = "product/$productId"
}
