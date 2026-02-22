package com.koosco.catalogservice.common.config

import com.koosco.catalogservice.contract.CatalogIntegrationEvent
import com.koosco.catalogservice.infra.messaging.TopicResolver
import org.springframework.stereotype.Component

@Component
class KafkaTopicResolver(private val props: KafkaTopicProperties) : TopicResolver {

    override fun resolve(event: CatalogIntegrationEvent): String = props.mappings[event.getEventType()]
        ?: props.default
}
