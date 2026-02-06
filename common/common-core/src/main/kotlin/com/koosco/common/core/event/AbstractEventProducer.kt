package com.koosco.common.core.event

import com.fasterxml.jackson.databind.ObjectMapper

/**
 * Abstract base class for event producers with automatic validation.
 * This class provides a template for implementing event producers that always validate events before publishing.
 *
 * Subclasses must implement:
 * - [publishRaw]: Low-level publishing method that sends serialized events to the message broker
 *
 * Subclasses can optionally override:
 * - [resolveTopic]: Custom topic resolution strategy
 * - [resolveKey]: Custom message key resolution strategy
 *
 * Features:
 * - Automatic validation for all CloudEvents and DomainEvents
 * - Support for both single and batch event publishing
 * - Consistent error handling and logging
 * - Extensible topic and key resolution strategies
 *
 * Example implementation:
 * ```
 * @Component
 * class KafkaEventProducer(
 *     objectMapper: ObjectMapper,
 *     private val kafkaTemplate: KafkaTemplate<String, String>
 * ) : AbstractEventProducer(objectMapper) {
 *
 *     override fun publishRaw(topic: String, key: String?, payload: String) {
 *         kafkaTemplate.send(topic, key, payload).get()
 *     }
 *
 *     override fun resolveTopic(event: CloudEvent<*>): String {
 *         return "my-app-${event.type.replace(".", "-")}"
 *     }
 * }
 * ```
 *
 * @param objectMapper Jackson ObjectMapper for JSON serialization
 */
abstract class AbstractEventProducer(
    private val objectMapper: ObjectMapper,
) : EventProducer {

    /**
     * Low-level method to publish raw serialized event payload.
     * Implementations should send the payload to the actual message broker.
     *
     * @param topic The destination topic/queue
     * @param key Optional message key for partitioning (can be null)
     * @param payload The serialized event payload (JSON string)
     * @throws EventProduceException if publishing fails
     */
    protected abstract fun publishRaw(topic: String, key: String?, payload: String)

    /**
     * Resolve the topic name for a given CloudEvent.
     * Default strategy converts event type to topic name (e.g., "com.koosco.order.created" → "com-koosco-order-created")
     *
     * Override this method to implement custom topic resolution logic.
     *
     * @param event The CloudEvent to resolve topic for
     * @return The topic name to publish to
     */
    protected open fun resolveTopic(event: CloudEvent<*>): String = event.type.replace(".", "-")

    /**
     * Resolve the message key for a given CloudEvent.
     * Default strategy uses event type as the key.
     *
     * Override this method to implement custom key resolution logic.
     * The key is typically used for message partitioning in message brokers.
     *
     * @param event The CloudEvent to resolve key for
     * @return The message key (can be null)
     */
    protected open fun resolveKey(event: CloudEvent<*>): String? = event.type

    /**
     * Publish a CloudEvent with automatic validation.
     * This method always validates the event before publishing.
     *
     * Flow:
     * 1. Validate CloudEvent spec compliance
     * 2. Serialize to JSON
     * 3. Resolve topic and key
     * 4. Publish via [publishRaw]
     *
     * @param event The CloudEvent to publish
     * @throws ValidationException if event validation fails
     * @throws EventProduceException if publishing fails
     */
    override fun publish(event: CloudEvent<*>) {
        try {
            // 1. Validate CloudEvent spec compliance
            EventValidator.validate(event).throwIfInvalid()

            // 2. Serialize to JSON
            val json = objectMapper.writeValueAsString(event)

            // 3. Resolve topic and key
            val topic = resolveTopic(event)
            val key = resolveKey(event)

            // 4. Publish via concrete implementation
            publishRaw(topic, key, json)
        } catch (e: ValidationException) {
            // Re-throw validation exceptions as-is
            throw e
        } catch (e: EventProduceException) {
            // Re-throw produce exceptions as-is
            throw e
        } catch (e: Exception) {
            // Wrap other exceptions
            throw EventProduceException("Failed to publish CloudEvent: ${event.type}", e)
        }
    }

    /**
     * Publish a domain event with automatic validation.
     * This method always validates both the DomainEvent and resulting CloudEvent before publishing.
     *
     * Flow:
     * 1. Validate DomainEvent
     * 2. Convert to CloudEvent
     * 3. Validate CloudEvent spec compliance
     * 4. Publish via [publish]
     *
     * @param event The domain event to publish
     * @param source The source identifier (e.g., "urn:koosco:order-service")
     * @param dataSchema Optional schema URI for the event data
     * @throws ValidationException if event validation fails
     * @throws EventProduceException if publishing fails
     */
    override fun publishDomainEvent(
        event: DomainEvent,
        source: String,
        dataSchema: String?,
        validate: Boolean,
    ) {
        try {
            // 1. Validate DomainEvent (always, ignoring validate parameter)
            EventValidator.validate(event).throwIfInvalid()

            // 2. Convert to CloudEvent
            val cloudEvent = event.toCloudEvent(source, dataSchema)

            // 3. Validate and publish CloudEvent (validation happens in publish method)
            publish(cloudEvent)
        } catch (e: ValidationException) {
            throw e
        } catch (e: EventProduceException) {
            throw e
        } catch (e: Exception) {
            throw EventProduceException("Failed to publish DomainEvent: ${event.getEventType()}", e)
        }
    }

    /**
     * Publish multiple CloudEvents in batch with automatic validation.
     * This method always validates all events before publishing any.
     *
     * Flow:
     * 1. Validate all CloudEvents
     * 2. Publish each event individually via [publish]
     *
     * Note: Default implementation publishes events sequentially.
     * Override this method for optimized batch processing if your message broker supports it.
     *
     * @param events The CloudEvents to publish
     * @param validate Ignored - validation always happens
     * @throws ValidationException if any event validation fails
     * @throws EventProduceException if any publishing fails
     */
    override fun publishBatch(
        events: List<CloudEvent<*>>,
        validate: Boolean,
    ) {
        if (events.isEmpty()) return

        try {
            // 1. Validate all events first (always, ignoring validate parameter)
            events.forEach { event ->
                EventValidator.validate(event).throwIfInvalid()
            }

            // 2. Publish each event
            events.forEach { event ->
                publish(event)
            }
        } catch (e: ValidationException) {
            throw e
        } catch (e: EventProduceException) {
            throw e
        } catch (e: Exception) {
            throw EventProduceException("Failed to publish batch of ${events.size} events", e)
        }
    }

    /**
     * Publish a CloudEvent with validation.
     * This is an alias for [publish] since validation is always performed.
     *
     * @param event The CloudEvent to publish
     * @throws ValidationException if validation fails
     * @throws EventProduceException if publishing fails
     */
    override fun publishWithValidation(event: CloudEvent<*>) {
        // Validation is always performed in publish()
        publish(event)
    }
}
