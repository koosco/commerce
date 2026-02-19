package com.koosco.inventoryservice.contract.inbound.catalog

import java.time.LocalDateTime

/**
 * fileName       : ProductSkuCreatedEvent
 * author         : koo
 * date           : 2025. 12. 24. 오전 2:34
 * description    :
 */
data class ProductSkuCreatedEvent(
    val skuId: String,
    val productId: Long,
    val productCode: String,
    val price: Long,
    val optionValues: String,
    val initialQuantity: Int = 0,
    val createdAt: LocalDateTime,
)
