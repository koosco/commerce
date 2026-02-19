package com.koosco.inventoryservice.infra.messaging.kafka.producer

import com.fasterxml.jackson.databind.ObjectMapper
import com.koosco.inventoryservice.application.contract.InventoryIntegrationEvent
import com.koosco.inventoryservice.application.port.IntegrationEventProducer
import com.koosco.inventoryservice.common.config.kafka.KafkaTopicResolver
import com.koosco.inventoryservice.domain.entity.InventoryOutboxEntry
import com.koosco.inventoryservice.infra.outbox.InventoryOutboxRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Outbox-based event producer for inventory-service.
 *
 * Instead of publishing events directly to Kafka, this producer saves events
 * to the outbox table within the same transaction as the domain operation.
 *
 * Debezium CDC then captures these inserts and publishes them to Kafka,
 * ensuring atomicity between database changes and event publishing.
 *
 * Note: Inventory operations use Redis for atomic stock updates.
 * The Outbox entry is saved after successful Redis operations.
 */
@Component
class OutboxIntegrationEventProducer(
    private val outboxRepository: InventoryOutboxRepository,
    private val topicResolver: KafkaTopicResolver,
    private val objectMapper: ObjectMapper,

    @Value("\${spring.application.name}")
    private val source: String,
) : IntegrationEventProducer {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun publish(event: InventoryIntegrationEvent) {
        val cloudEvent = event.toCloudEvent(source)
        val topic = topicResolver.resolve(event)
        val partitionKey = event.getPartitionKey()
        val eventType = event.getEventType()

        val payload = try {
            objectMapper.writeValueAsString(cloudEvent)
        } catch (e: Exception) {
            logger.error(
                "Failed to serialize CloudEvent: type=$eventType, orderId=${event.orderId}",
                e,
            )
            throw e
        }

        val outboxEntry = InventoryOutboxEntry.create(
            aggregateId = event.orderId.toString(),
            eventType = eventType,
            payload = payload,
            topic = topic,
            partitionKey = partitionKey,
        )

        outboxRepository.save(outboxEntry)

        logger.info(
            "Outbox entry saved: type=$eventType, orderId=${event.orderId}, topic=$topic",
        )
    }
}
