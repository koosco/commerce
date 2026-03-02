package com.koosco.common.core.event

interface TopicResolver {
    fun resolve(event: IntegrationEvent): String
}
