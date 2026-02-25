package com.koosco.catalogservice.infra.messaging.kafka.producer

import com.fasterxml.jackson.databind.ObjectMapper
import com.koosco.catalogservice.application.port.IntegrationEventProducer
import com.koosco.catalogservice.common.config.KafkaTopicResolver
import com.koosco.catalogservice.contract.CatalogIntegrationEvent
import com.koosco.catalogservice.domain.entity.CatalogOutboxEntry
import com.koosco.catalogservice.infra.outbox.CatalogOutboxRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Outbox-based event producer for catalog-service.
 *
 * Instead of publishing events directly to Kafka, this producer saves events
 * to the outbox table within the same transaction as the domain operation.
 *
 * Debezium CDC then captures these inserts and publishes them to Kafka,
 * ensuring atomicity between database changes and event publishing.
 */
@Component
class OutboxIntegrationEventProducer(
    private val outboxRepository: CatalogOutboxRepository,
    private val topicResolver: KafkaTopicResolver,
    private val objectMapper: ObjectMapper,

    @Value("\${spring.application.name}")
    private val source: String,
) : IntegrationEventProducer {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun publish(event: CatalogIntegrationEvent) {
        val cloudEvent = event.toCloudEvent(source)
        val topic = topicResolver.resolve(event)
        val partitionKey = event.getPartitionKey()
        val eventType = event.getEventType()

        val payload = try {
            objectMapper.writeValueAsString(cloudEvent)
        } catch (e: Exception) {
            logger.error(
                "Failed to serialize CloudEvent: type=$eventType, aggregateId=${event.getAggregateId()}",
                e,
            )
            throw e
        }

        val outboxEntry = CatalogOutboxEntry.create(
            aggregateId = event.getAggregateId(),
            eventType = eventType,
            payload = payload,
            topic = topic,
            partitionKey = partitionKey,
        )

        outboxRepository.save(outboxEntry)

        logger.info(
            "Outbox entry saved: type=$eventType, aggregateId=${event.getAggregateId()}, topic=$topic",
        )
    }
}
