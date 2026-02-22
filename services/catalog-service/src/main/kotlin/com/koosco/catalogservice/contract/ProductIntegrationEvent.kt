package com.koosco.catalogservice.contract

interface ProductIntegrationEvent : CatalogIntegrationEvent {
    val skuId: String

    override fun getAggregateId(): String = skuId

    override fun getEventType(): String

    override fun getSubject(): String = "sku/$skuId"
}
