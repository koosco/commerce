package com.koosco.common.core.event

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import java.time.Instant
import java.util.UUID

/**
 * CloudEvents v1.0 specification implementation.
 * CNCF CloudEvents standard format for event data.
 *
 * @see <a href="https://github.com/cloudevents/spec/blob/v1.0.2/cloudevents/spec.md">CloudEvents Spec v1.0</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class CloudEvent<T>(
    /**
     * Identifies the event. REQUIRED.
     * Producers MUST ensure that source + id is unique for each distinct event.
     */
    @field:NotBlank
    @JsonProperty("id")
    val id: String,

    /**
     * Identifies the context in which an event happened. REQUIRED.
     * Often this will include information such as the type of the event source,
     * the organization publishing the event or the process that produced the event.
     *
     * Format: URI-reference (RFC 3986)
     * Example: "urn:koosco:order-service", "https://api.koosco.com/orders"
     */
    @field:NotBlank
    @JsonProperty("source")
    val source: String,

    /**
     * The version of the CloudEvents specification. REQUIRED.
     * This enables the interpretation of the context.
     */
    @field:NotBlank
    @JsonProperty("specversion")
    val specVersion: String = "1.0",

    /**
     * Describes the type of event related to the originating occurrence. REQUIRED.
     * This attribute is used for routing, observability, and policy enforcement.
     *
     * Format: Reverse domain name notation recommended
     * Example: "com.koosco.order.created", "com.koosco.payment.completed"
     */
    @field:NotBlank
    @JsonProperty("type")
    val type: String,

    /**
     * Content type of the data value. OPTIONAL.
     * This attribute enables data to carry any type of content.
     *
     * Format: RFC 2046
     * Example: "application/json", "application/xml"
     */
    @JsonProperty("datacontenttype")
    val dataContentType: String? = "application/json",

    /**
     * Identifies the schema that data adheres to. OPTIONAL.
     *
     * Format: URI
     * Example: "https://schemas.koosco.com/order/v1"
     */
    @JsonProperty("dataschema")
    val dataSchema: String? = null,

    /**
     * Describes the subject of the event in the context of the event producer. OPTIONAL.
     * In publish-subscribe scenarios, a subscriber will typically subscribe to events
     * emitted by a source, but the source identifier alone might not be sufficient
     * as a qualifier for any specific event.
     *
     * Example: For order events, this might be the order ID
     */
    @JsonProperty("subject")
    val subject: String? = null,

    /**
     * Timestamp of when the occurrence happened. OPTIONAL.
     * If the time of the occurrence cannot be determined then this attribute
     * MAY be set to some other time (such as the current time) by the CloudEvents producer.
     *
     * Format: RFC 3339
     */
    @JsonProperty("time")
    val time: Instant? = null,

    /**
     * The event payload. OPTIONAL.
     * The event payload can be of any type.
     */
    @field:Valid
    @JsonProperty("data")
    val data: T? = null,
) {
    init {
        // Basic validation in constructor
        // Detailed validation should be done using EventValidator
        require(specVersion == "1.0") { "CloudEvent 'specversion' must be '1.0'" }
    }

    companion object {
        /**
         * Generate a new UUID for event ID.
         */
        fun generateId(): String = UUID.randomUUID().toString()

        /**
         * Create a CloudEvent with auto-generated ID and current timestamp.
         */
        fun <T> of(
            source: String,
            type: String,
            data: T? = null,
            subject: String? = null,
            dataSchema: String? = null,
            dataContentType: String? = "application/json",
        ): CloudEvent<T> = CloudEvent(
            id = generateId(),
            source = source,
            type = type,
            data = data,
            subject = subject,
            dataSchema = dataSchema,
            dataContentType = dataContentType,
            time = Instant.now(),
        )
    }

    /**
     * Create a new CloudEvent with modified data.
     */
    fun <R> withData(newData: R): CloudEvent<R> = CloudEvent(
        id = id,
        source = source,
        specVersion = specVersion,
        type = type,
        dataContentType = dataContentType,
        dataSchema = dataSchema,
        subject = subject,
        time = time,
        data = newData,
    )

    /**
     * Create a new CloudEvent without data (metadata only).
     */
    fun withoutData(): CloudEvent<Nothing?> = CloudEvent(
        id = id,
        source = source,
        specVersion = specVersion,
        type = type,
        dataContentType = dataContentType,
        dataSchema = dataSchema,
        subject = subject,
        time = time,
        data = null,
    )
}
