package com.koosco.catalogservice.common.config

import com.koosco.catalogservice.product.application.contract.ProductIntegrationEvent
import com.koosco.catalogservice.product.infra.messaging.TopicResolver
import org.springframework.stereotype.Component

/**
 * fileName       : KafkaTopicResolver
 * author         : koo
 * date           : 2025. 12. 22. 오전 4:42
 * description    :
 */
@Component
class KafkaTopicResolver(private val props: KafkaTopicProperties) : TopicResolver {

    override fun resolve(event: ProductIntegrationEvent): String = props.mappings[event.getEventType()]
        ?: props.default
}
