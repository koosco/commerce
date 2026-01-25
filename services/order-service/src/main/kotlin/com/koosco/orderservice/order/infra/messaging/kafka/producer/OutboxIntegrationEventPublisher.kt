package com.koosco.orderservice.order.infra.messaging.kafka.producer

import com.fasterxml.jackson.databind.ObjectMapper
import com.koosco.orderservice.order.application.contract.OrderIntegrationEvent
import com.koosco.orderservice.order.application.port.IntegrationEventPublisher
import com.koosco.orderservice.order.domain.entity.OrderOutboxEntry
import com.koosco.orderservice.order.infra.messaging.kafka.KafkaTopicResolver
import com.koosco.orderservice.order.infra.outbox.OrderOutboxRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Outbox-based event publisher.
 *
 * Instead of publishing events directly to Kafka, this publisher saves events
 * to the outbox table within the same transaction as the domain operation.
 *
 * Debezium CDC then captures these inserts and publishes them to Kafka,
 * ensuring atomicity between database changes and event publishing.
 *
 * ## Benefits
 * - Guaranteed consistency: Events are only published if the transaction commits
 * - No distributed transactions: Single database transaction
 * - Retry handled by CDC: Debezium handles failures and retries
 *
 * ## Flow
 * 1. UseCase saves domain entity and calls publish()
 * 2. This publisher saves OutboxEntry in same transaction
 * 3. Transaction commits (atomic)
 * 4. Debezium captures INSERT via binlog
 * 5. Debezium publishes to Kafka topic
 */
@Component
class OutboxIntegrationEventPublisher(
    private val outboxRepository: OrderOutboxRepository,
    private val topicResolver: KafkaTopicResolver,
    private val objectMapper: ObjectMapper,

    @Value("\${spring.application.name}")
    private val source: String,
) : IntegrationEventPublisher {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun publish(event: OrderIntegrationEvent) {
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

        val outboxEntry = OrderOutboxEntry.create(
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
