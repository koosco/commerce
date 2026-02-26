package com.koosco.catalogservice.contract.outbound

import com.koosco.catalogservice.contract.ProductIntegrationEvent
import java.time.LocalDateTime

data class ProductSkuDeactivatedEvent(
    override val skuId: String,
    val productId: Long,
    val productCode: String,
    val optionValues: String,
    val deactivatedAt: LocalDateTime,
) : ProductIntegrationEvent {
    override fun getEventType(): String = "product.sku.deactivated"
}
