package com.koosco.common.core.event

interface IntegrationEvent {
    val aggregateId: String

    fun getEventType(): String

    fun getPartitionKey(): String = aggregateId

    fun getSubject(): String

    fun toCloudEvent(source: String): CloudEvent<out IntegrationEvent> = CloudEvent.of(
        source = source,
        type = getEventType(),
        subject = getSubject(),
        data = this,
    )
}
