package com.koosco.inventoryservice.common.config.kafka

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * fileName       : KafkaTopicProperties
 * author         : koo
 * date           : 2025. 12. 19. 오후 3:00
 * description    :
 */
@Component
@ConfigurationProperties(prefix = "inventory.topic")
class KafkaTopicProperties {

    lateinit var mappings: Map<String, String>

    /**
     * fallback topic
     */
    lateinit var default: String
}
