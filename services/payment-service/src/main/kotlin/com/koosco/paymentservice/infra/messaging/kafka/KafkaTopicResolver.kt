package com.koosco.paymentservice.infra.messaging.kafka

import com.koosco.paymentservice.contract.PaymentIntegrationEvent
import com.koosco.paymentservice.infra.messaging.TopicResolver
import org.springframework.stereotype.Component

/**
 * fileName       : KafkaTopicProperties
 * author         : koo
 * date           : 2025. 12. 24. 오후 9:07
 * description    :
 */
@Component
class KafkaTopicResolver(private val props: KafkaTopicProperties) : TopicResolver {

    override fun resolve(event: PaymentIntegrationEvent): String = props.mappings[event.getEventType()]
        ?: props.default
}
