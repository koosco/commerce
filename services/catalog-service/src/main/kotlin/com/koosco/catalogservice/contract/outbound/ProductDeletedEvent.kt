package com.koosco.catalogservice.contract.outbound

import com.koosco.common.core.event.IntegrationEvent

data class ProductDeletedEvent(val productId: Long) : IntegrationEvent {
    override val aggregateId: String get() = productId.toString()

    override fun getEventType(): String = "product.deleted"

    override fun getSubject(): String = "product/$productId"
}
