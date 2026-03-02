package com.koosco.common.core.event

interface IntegrationEventProducer {
    fun publish(event: IntegrationEvent)
}
