package com.koosco.catalogservice.contract.outbound

import com.koosco.catalogservice.contract.CatalogIntegrationEvent
import com.koosco.catalogservice.domain.enums.ProductStatus
import java.time.LocalDateTime

data class ProductStatusChangedEvent(
    val productId: Long,
    val productCode: String,
    val previousStatus: ProductStatus,
    val newStatus: ProductStatus,
    val changedAt: LocalDateTime,
) : CatalogIntegrationEvent {
    override fun getAggregateId(): String = productId.toString()

    override fun getEventType(): String = "product.status.changed"

    override fun getSubject(): String = "product/$productId"
}
