package com.koosco.catalogservice.contract.outbound

import com.koosco.common.core.event.IntegrationEvent
import java.time.LocalDateTime

data class ProductSkuCreatedEvent(
    val skuId: String,
    val productId: Long,
    val productCode: String,
    val price: Long,
    val optionValues: String,
    val initialQuantity: Int = 0,
    val createdAt: LocalDateTime,
) : IntegrationEvent {
    override val aggregateId: String get() = skuId

    override fun getEventType(): String = "product.sku.created"

    override fun getSubject(): String = "sku/$skuId"
}
