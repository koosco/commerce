package com.koosco.orderservice.infra.messaging.kafka

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * fileName       : KafkaTopicProperties
 * author         : koo
 * date           : 2025. 12. 22. 오전 4:43
 * description    :
 */
@Component
@ConfigurationProperties(prefix = "order.topic")
class KafkaTopicProperties {

    lateinit var mappings: Map<String, String>

    /**
     * fallback topic
     */
    lateinit var default: String
}
