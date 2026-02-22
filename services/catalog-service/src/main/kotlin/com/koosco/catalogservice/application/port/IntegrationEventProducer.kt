package com.koosco.catalogservice.application.port

import com.koosco.catalogservice.contract.CatalogIntegrationEvent

interface IntegrationEventProducer {
    fun publish(event: CatalogIntegrationEvent)
}
