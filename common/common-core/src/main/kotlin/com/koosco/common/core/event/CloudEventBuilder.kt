package com.koosco.common.core.event

import java.time.Instant

/**
 * Builder for creating CloudEvent instances with fluent API.
 *
 * Usage:
 * ```
 * val event = CloudEventBuilder.builder<OrderCreatedData>()
 *     .source("urn:koosco:order-service")
 *     .type("com.koosco.order.created")
 *     .subject("order-123")
 *     .data(orderData)
 *     .build()
 * ```
 */
class CloudEventBuilder<T> private constructor() {
    private var id: String? = null
    private var source: String? = null
    private var type: String? = null
    private var specVersion: String = "1.0"
    private var dataContentType: String? = "application/json"
    private var dataSchema: String? = null
    private var subject: String? = null
    private var time: Instant? = null
    private var data: T? = null

    /**
     * Set the event ID. If not set, a UUID will be generated automatically.
     */
    fun id(id: String) = apply { this.id = id }

    /**
     * Set the event source. REQUIRED.
     */
    fun source(source: String) = apply { this.source = source }

    /**
     * Set the event type. REQUIRED.
     */
    fun type(type: String) = apply { this.type = type }

    /**
     * Set the CloudEvents spec version. Default is "1.0".
     */
    fun specVersion(specVersion: String) = apply { this.specVersion = specVersion }

    /**
     * Set the data content type. Default is "application/json".
     */
    fun dataContentType(dataContentType: String?) = apply { this.dataContentType = dataContentType }

    /**
     * Set the data schema URI.
     */
    fun dataSchema(dataSchema: String?) = apply { this.dataSchema = dataSchema }

    /**
     * Set the event subject.
     */
    fun subject(subject: String?) = apply { this.subject = subject }

    /**
     * Set the event time. If not set, current time will be used.
     */
    fun time(time: Instant?) = apply { this.time = time }

    /**
     * Set the event data payload.
     */
    fun data(data: T?) = apply { this.data = data }

    /**
     * Build the CloudEvent instance.
     * @throws IllegalArgumentException if required fields are not set
     */
    fun build(): CloudEvent<T> = CloudEvent(
        id = id ?: CloudEvent.generateId(),
        source = requireNotNull(source) { "CloudEvent 'source' is required" },
        type = requireNotNull(type) { "CloudEvent 'type' is required" },
        specVersion = specVersion,
        dataContentType = dataContentType,
        dataSchema = dataSchema,
        subject = subject,
        time = time ?: Instant.now(),
        data = data,
    )

    companion object {
        /**
         * Create a new CloudEventBuilder instance.
         */
        fun <T> builder(): CloudEventBuilder<T> = CloudEventBuilder()

        /**
         * Create a CloudEventBuilder with pre-filled source and type.
         * Useful for services that emit events from the same source.
         */
        fun <T> builder(source: String, type: String): CloudEventBuilder<T> = CloudEventBuilder<T>()
            .source(source)
            .type(type)
    }
}

/**
 * DSL-style builder function for creating CloudEvents.
 *
 * Usage:
 * ```
 * val event = cloudEvent<OrderData> {
 *     source = "urn:koosco:order-service"
 *     type = "com.koosco.order.created"
 *     subject = "order-123"
 *     data = orderData
 * }
 * ```
 */
fun <T> cloudEvent(init: CloudEventBuilder<T>.() -> Unit): CloudEvent<T> = CloudEventBuilder.builder<T>().apply(init).build()
