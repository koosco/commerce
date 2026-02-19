package com.koosco.inventoryservice.common.config.kafka

import com.koosco.inventoryservice.application.contract.InventoryIntegrationEvent
import com.koosco.inventoryservice.infra.messaging.IntegrationTopicResolver
import org.springframework.stereotype.Component

/**
 * fileName       : TopicResolver
 * author         : koo
 * date           : 2025. 12. 19. 오후 1:24
 * description    : domain event와 topic mapping
 */
@Component
class KafkaTopicResolver(private val props: KafkaTopicProperties) : IntegrationTopicResolver {

    override fun resolve(event: InventoryIntegrationEvent): String = props.mappings[event.getEventType()]
        ?: props.default
}
