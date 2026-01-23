package com.koosco.paymentservice.infra.messaging.kafka

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * fileName       : KafkaTopicProperties
 * author         : koo
 * date           : 2025. 12. 24. 오후 9:08
 * description    :
 */
@Component
@ConfigurationProperties(prefix = "payment.topic")
class KafkaTopicProperties {

    lateinit var mappings: Map<String, String>

    /**
     * fallback topic
     */
    lateinit var default: String
}
