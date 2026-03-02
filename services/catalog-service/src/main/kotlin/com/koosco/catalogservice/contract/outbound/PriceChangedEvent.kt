package com.koosco.catalogservice.contract.outbound

import com.koosco.common.core.event.IntegrationEvent
import java.time.LocalDateTime

data class PriceChangedEvent(
    val skuId: String,
    val productId: Long,
    val previousPrice: Long,
    val newPrice: Long,
    val reason: String,
    val changedAt: LocalDateTime,
) : IntegrationEvent {
    override val aggregateId: String get() = skuId

    override fun getEventType(): String = "product.price.changed"

    override fun getSubject(): String = "sku/$skuId"

    companion object {
        const val REASON_DISCOUNT_APPLIED = "DISCOUNT_APPLIED"
        const val REASON_DISCOUNT_EXPIRED = "DISCOUNT_EXPIRED"
        const val REASON_BASE_PRICE_CHANGED = "BASE_PRICE_CHANGED"
    }
}
