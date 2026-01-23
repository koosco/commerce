package com.koosco.catalogservice.product.application.contract.outbound

import com.koosco.catalogservice.product.application.contract.ProductIntegrationEvent
import java.time.LocalDateTime

/**
 * fileName       : ProductSkuCreatedEvent
 * author         : koo
 * date           : 2025. 12. 23. 오전 3:19
 * description    :
 */
data class ProductSkuCreatedEvent(
    override val skuId: String,
    val productId: Long,
    val productCode: String,
    val price: Long,
    val optionValues: String,
    val initialQuantity: Int = 0,
    val createdAt: LocalDateTime,
) : ProductIntegrationEvent {
    override fun getEventType(): String = "product.sku.created"
}
