package com.koosco.catalogservice.common.config

import com.koosco.common.core.event.IntegrationEvent
import com.koosco.common.core.event.TopicResolver
import org.springframework.stereotype.Component

@Component
class KafkaTopicResolver(private val props: KafkaTopicProperties) : TopicResolver {

    override fun resolve(event: IntegrationEvent): String = props.mappings[event.getEventType()]
        ?: props.default
}
