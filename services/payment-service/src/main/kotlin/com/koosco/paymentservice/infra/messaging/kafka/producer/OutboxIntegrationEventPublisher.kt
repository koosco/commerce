package com.koosco.paymentservice.infra.messaging.kafka.producer

import com.fasterxml.jackson.databind.ObjectMapper
import com.koosco.paymentservice.application.contract.PaymentIntegrationEvent
import com.koosco.paymentservice.application.port.IntegrationEventPublisher
import com.koosco.paymentservice.domain.entity.PaymentOutboxEntry
import com.koosco.paymentservice.infra.messaging.kafka.KafkaTopicResolver
import com.koosco.paymentservice.infra.outbox.PaymentOutboxRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Outbox-based event publisher for payment-service.
 *
 * Instead of publishing events directly to Kafka, this publisher saves events
 * to the outbox table within the same transaction as the domain operation.
 *
 * Debezium CDC then captures these inserts and publishes them to Kafka,
 * ensuring atomicity between database changes and event publishing.
 */
@Component
class OutboxIntegrationEventPublisher(
    private val outboxRepository: PaymentOutboxRepository,
    private val topicResolver: KafkaTopicResolver,
    private val objectMapper: ObjectMapper,

    @Value("\${spring.application.name}")
    private val source: String,
) : IntegrationEventPublisher {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun publish(event: PaymentIntegrationEvent) {
        val cloudEvent = event.toCloudEvent(source)
        val topic = topicResolver.resolve(event)
        val partitionKey = event.getPartitionKey()
        val eventType = event.getEventType()

        val payload = try {
            objectMapper.writeValueAsString(cloudEvent)
        } catch (e: Exception) {
            logger.error(
                "Failed to serialize CloudEvent: type=$eventType, paymentId=${event.paymentId}",
                e,
            )
            throw e
        }

        val outboxEntry = PaymentOutboxEntry.create(
            aggregateId = event.paymentId,
            eventType = eventType,
            payload = payload,
            topic = topic,
            partitionKey = partitionKey,
        )

        outboxRepository.save(outboxEntry)

        logger.info(
            "Outbox entry saved: type=$eventType, paymentId=${event.paymentId}, topic=$topic",
        )
    }
}
