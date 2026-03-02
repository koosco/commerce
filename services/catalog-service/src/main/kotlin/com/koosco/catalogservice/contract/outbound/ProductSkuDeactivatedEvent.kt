package com.koosco.catalogservice.contract.outbound

import com.koosco.common.core.event.IntegrationEvent
import java.time.LocalDateTime

data class ProductSkuDeactivatedEvent(
    val skuId: String,
    val productId: Long,
    val productCode: String,
    val optionValues: String,
    val deactivatedAt: LocalDateTime,
) : IntegrationEvent {
    override val aggregateId: String get() = skuId

    override fun getEventType(): String = "product.sku.deactivated"

    override fun getSubject(): String = "sku/$skuId"
}
