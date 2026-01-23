package com.koosco.catalogservice.product.infra.messaging.kafka.producer

import com.koosco.catalogservice.common.config.KafkaTopicResolver
import com.koosco.catalogservice.product.application.contract.ProductIntegrationEvent
import com.koosco.catalogservice.product.application.port.IntegrationEventPublisher
import com.koosco.common.core.event.CloudEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

/**
 * fileName       : KafkaIntegrationEventPublisher
 * author         : koo
 * date           : 2025. 12. 19. 오후 1:47
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

    override fun publish(event: ProductIntegrationEvent) {
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
