package com.koosco.orderservice.infra.messaging.kafka

import com.koosco.orderservice.contract.OrderIntegrationEvent
import com.koosco.orderservice.infra.messaging.TopicResolver
import org.springframework.stereotype.Component

/**
 * fileName       : KafkaTopicResolver
 * author         : koo
 * date           : 2025. 12. 22. 오전 4:42
 * description    :
 */
@Component
class KafkaTopicResolver(private val props: KafkaTopicProperties) : TopicResolver {

    override fun resolve(event: OrderIntegrationEvent): String = props.mappings[event.getEventType()]
        ?: props.default
}
