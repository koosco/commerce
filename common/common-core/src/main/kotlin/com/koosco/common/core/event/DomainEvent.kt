package com.koosco.common.core.event

import java.time.Instant

/**
 * Base interface for all domain events in MSA architecture.
 * Domain events represent something that happened in the domain that domain experts care about.
 *
 * Implementation classes should be immutable and contain all the data needed to process the event.
 *
 * Example:
 * ```
 * data class OrderCreatedEvent(
 *     val orderId: String,
 *     val userId: String,
 *     val totalAmount: BigDecimal,
 *     override val eventId: String = UUID.randomUUID().toString(),
 *     override val occurredAt: Instant = Instant.now(),
 * ) : DomainEvent {
 *     override fun getEventType(): String = "com.koosco.order.created"
 *     override fun getAggregateId(): String = orderId
 * }
 * ```
 */
interface DomainEvent {
    /**
     * Unique identifier for this event instance.
     * Should be globally unique (UUID recommended).
     */
    val eventId: String

    /**
     * Timestamp when the event occurred.
     */
    val occurredAt: Instant

    /**
     * The type of this event.
     * Should use reverse domain name notation.
     * Example: "com.koosco.order.created", "com.koosco.payment.completed"
     */
    fun getEventType(): String

    /**
     * The ID of the aggregate that this event is related to.
     * This is typically the entity ID that the event describes a change to.
     */
    fun getAggregateId(): String

    /**
     * Optional: Get the event version for event evolution.
     * Default is "1.0"
     */
    fun getEventVersion(): String = "1.0"

    /**
     * Convert this domain event to CloudEvent format.
     *
     * @param source The source URI for the CloudEvent (e.g., service name)
     * @param dataSchema Optional schema URI for the event data
     */
    fun toCloudEvent(
        source: String,
        dataSchema: String? = null,
    ): CloudEvent<DomainEvent> = CloudEvent(
        id = eventId,
        source = source,
        type = getEventType(),
        subject = getAggregateId(),
        time = occurredAt,
        data = this,
        dataSchema = dataSchema,
    )
}

/**
 * Abstract base class for domain events with common implementation.
 * Provides default implementations for eventId and occurredAt.
 *
 * Usage:
 * ```
 * data class OrderCreatedEvent(
 *     val orderId: String,
 *     val userId: String,
 *     val totalAmount: BigDecimal,
 * ) : AbstractDomainEvent() {
 *     override fun getEventType(): String = "com.koosco.order.created"
 *     override fun getAggregateId(): String = orderId
 * }
 * ```
 */
abstract class AbstractDomainEvent(
    override val eventId: String = CloudEvent.generateId(),
    override val occurredAt: Instant = Instant.now(),
) : DomainEvent

/**
 * Marker interface for events that should be published to external systems.
 * Some domain events are internal-only and should not be published externally.
 */
interface PublishableDomainEvent : DomainEvent

/**
 * Helper extension function to convert any DomainEvent to CloudEvent with automatic source formatting.
 * If sourcePrefix doesn't contain a scheme (://  or urn:), it will be prefixed with "urn:".
 *
 * @param sourcePrefix The source prefix. Will be auto-formatted to URN if needed.
 * @return CloudEvent with properly formatted source
 */
fun DomainEvent.toCloudEventWithPrefix(sourcePrefix: String = "urn:koosco"): CloudEvent<DomainEvent> {
    val source = when {
        sourcePrefix.contains("://") -> sourcePrefix // Already has HTTP/HTTPS scheme
        sourcePrefix.startsWith("urn:") -> sourcePrefix // Already has URN scheme
        else -> "urn:$sourcePrefix" // Add URN prefix
    }
    return toCloudEvent(source = source, dataSchema = null)
}
