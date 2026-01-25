package com.koosco.paymentservice.infra.messaging.kafka.producer

import com.koosco.common.core.event.CloudEvent
import com.koosco.paymentservice.application.contract.PaymentIntegrationEvent
import com.koosco.paymentservice.application.port.IntegrationEventPublisher
import com.koosco.paymentservice.infra.messaging.kafka.KafkaTopicResolver
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

/**
 * fileName       : KafkaIntegrationEventPublisher
 * author         : koo
 * date           : 2025. 12. 24. 오후 9:06
 * description    :
 */
@Component
class KafkaIntegrationEventPublisher(
    private val topicResolver: KafkaTopicResolver,
    private val kafkaTemplate: KafkaTemplate<String, CloudEvent<*>>,

    @Value("\${spring.application.name}")
    private val source: String,
) : IntegrationEventPublisher {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun publish(event: PaymentIntegrationEvent) {
        val cloudEvent = event.toCloudEvent(source)
        val topic = topicResolver.resolve(event)
        val key = event.getPartitionKey()

        kafkaTemplate.send(topic, key, cloudEvent)
            .whenComplete { _, ex ->
                if (ex == null) {
                    logger.info(
                        "IntegrationEvent published: type=${event.getEventType()}, key=$key, topic=$topic",
                    )
                } else {
                    logger.error(
                        "IntegrationEvent publish failed: type=${event.getEventType()}, key=$key, topic=$topic",
                        ex,
                    )
                }
            }
    }
}
