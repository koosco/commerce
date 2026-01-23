package com.koosco.common.core.event

/**
 * Interface for publishing events in MSA architecture.
 * Implementations can use various message brokers (Kafka, RabbitMQ, etc.)
 *
 * Example implementation:
 * ```
 * @Component
 * class KafkaEventPublisher(
 *     private val kafkaTemplate: KafkaTemplate<String, String>,
 *     private val objectMapper: ObjectMapper
 * ) : EventPublisher {
 *     override fun publish(event: CloudEvent<*>) {
 *         val json = objectMapper.writeValueAsString(event)
 *         kafkaTemplate.send("events", event.type, json)
 *     }
 * }
 * ```
 */
interface EventPublisher {
    /**
     * Publish a CloudEvent.
     * Implementation should handle serialization and routing.
     *
     * @param event The CloudEvent to publish
     * @throws EventPublishException if publishing fails
     */
    fun publish(event: CloudEvent<*>)

    /**
     * Publish a domain event by converting it to CloudEvent.
     *
     * @param event The domain event to publish
     * @param source The source identifier (e.g., service name, URI)
     * @param dataSchema Optional schema URI for the event data
     * @param validate Whether to validate the event before publishing (default: true)
     * @throws EventPublishException if publishing fails
     * @throws ValidationException if validation is enabled and fails
     */
    fun publishDomainEvent(
        event: DomainEvent,
        source: String,
        dataSchema: String? = null,
        validate: Boolean = true,
    ) {
        val cloudEvent = event.toCloudEvent(source, dataSchema)

        if (validate) {
            EventValidator.validate(cloudEvent).throwIfInvalid()
        }

        publish(cloudEvent)
    }

    /**
     * Publish multiple events in batch.
     * Default implementation publishes each event individually.
     * Implementations can override for optimized batch processing.
     *
     * @param events The CloudEvents to publish
     * @param validate Whether to validate each event before publishing (default: true)
     * @throws EventPublishException if any publishing fails
     * @throws ValidationException if validation is enabled and any event fails validation
     */
    fun publishBatch(
        events: List<CloudEvent<*>>,
        validate: Boolean = true,
    ) {
        if (validate) {
            events.forEach { event ->
                EventValidator.validate(event).throwIfInvalid()
            }
        }
        events.forEach { publish(it) }
    }

    /**
     * Publish a CloudEvent with validation.
     * Convenience method that always validates before publishing.
     *
     * @param event The CloudEvent to publish
     * @throws EventPublishException if publishing fails
     * @throws ValidationException if validation fails
     */
    fun publishWithValidation(event: CloudEvent<*>) {
        EventValidator.validate(event).throwIfInvalid()
        publish(event)
    }
}

/**
 * Exception thrown when event publishing fails.
 */
class EventPublishException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
