package com.koosco.catalogservice.contract

import com.koosco.common.core.event.CloudEvent

interface CatalogIntegrationEvent {
    fun getAggregateId(): String

    fun getEventType(): String

    fun getPartitionKey(): String = getAggregateId()

    fun getSubject(): String

    fun toCloudEvent(source: String): CloudEvent<out CatalogIntegrationEvent> = CloudEvent.of(
        source = source,
        type = getEventType(),
        subject = getSubject(),
        data = this,
    )
}
