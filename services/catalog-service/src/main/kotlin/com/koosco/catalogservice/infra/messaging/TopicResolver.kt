package com.koosco.catalogservice.infra.messaging

import com.koosco.catalogservice.contract.CatalogIntegrationEvent

interface TopicResolver {
    fun resolve(event: CatalogIntegrationEvent): String
}
