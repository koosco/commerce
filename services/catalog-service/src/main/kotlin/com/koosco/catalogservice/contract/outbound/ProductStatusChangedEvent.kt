package com.koosco.catalogservice.contract.outbound

import com.koosco.catalogservice.domain.enums.ProductStatus
import com.koosco.common.core.event.IntegrationEvent
import java.time.LocalDateTime

data class ProductStatusChangedEvent(
    val productId: Long,
    val productCode: String,
    val previousStatus: ProductStatus,
    val newStatus: ProductStatus,
    val changedAt: LocalDateTime,
) : IntegrationEvent {
    override val aggregateId: String get() = productId.toString()

    override fun getEventType(): String = "product.status.changed"

    override fun getSubject(): String = "product/$productId"
}
