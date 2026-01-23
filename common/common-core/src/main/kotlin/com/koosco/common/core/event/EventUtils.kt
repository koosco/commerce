package com.koosco.common.core.event

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

/**
 * Utility functions for working with CloudEvents.
 */
object EventUtils {

    @PublishedApi
    internal val objectMapper: ObjectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
    }

    /**
     * Serialize a CloudEvent to JSON string.
     */
    fun <T> toJson(event: CloudEvent<T>): String = try {
        objectMapper.writeValueAsString(event)
    } catch (e: Exception) {
        throw EventSerializationException("Failed to serialize CloudEvent to JSON", e)
    }

    /**
     * Deserialize JSON → CloudEvent<T>
     *
     * 사용 예:
     * val event = EventUtils.fromJson<StockReserveEvent>(json)
     */
    inline fun <reified T> fromJson(json: String): CloudEvent<T> = try {
        val typeRef = object : TypeReference<CloudEvent<T>>() {}
        objectMapper.readValue(json, typeRef)
    } catch (e: Exception) {
        throw EventSerializationException("Failed to deserialize CloudEvent from JSON", e)
    }

    /**
     * Deserialize + Validate
     *
     * EventValidator.validate(event).throwIfInvalid() 자동 실행
     */
    inline fun <reified T> deserializeAndValidate(json: String): CloudEvent<T> {
        val event = fromJson<T>(json)
        EventValidator.validate(event).throwIfInvalid()
        return event
    }

    /**
     * Convert CloudEvent → Map
     */
    fun <T> toMap(event: CloudEvent<T>): Map<String, Any?> = try {
        @Suppress("UNCHECKED_CAST")
        objectMapper.convertValue(event, Map::class.java) as Map<String, Any?>
    } catch (e: Exception) {
        throw EventSerializationException("Failed to convert CloudEvent to Map", e)
    }

    /**
     * Convert Map → CloudEvent<T>
     */
    inline fun <reified T> fromMap(map: Map<String, Any?>): CloudEvent<T> = try {
        val typeRef = object : TypeReference<CloudEvent<T>>() {}
        objectMapper.convertValue(map, typeRef)
    } catch (e: Exception) {
        throw EventSerializationException("Failed to convert Map to CloudEvent", e)
    }

    /**
     * Extract & convert CloudEvent.data → R
     */
    fun <T, R> extractData(event: CloudEvent<T>, dataType: Class<R>): R? = try {
        event.data?.let { objectMapper.convertValue(it, dataType) }
    } catch (e: Exception) {
        throw EventSerializationException("Failed to extract data from CloudEvent", e)
    }

    /**
     * Convert CloudEvent<T> → CloudEvent<R>
     */
    fun <T, R> convertData(event: CloudEvent<T>, dataType: Class<R>): CloudEvent<R?> {
        val convertedData = extractData(event, dataType)
        return CloudEvent(
            id = event.id,
            source = event.source,
            specVersion = event.specVersion,
            type = event.type,
            dataContentType = event.dataContentType,
            dataSchema = event.dataSchema,
            subject = event.subject,
            time = event.time,
            data = convertedData,
        )
    }
}

/**
 * Exception thrown when event serialization/deserialization fails.
 */
class EventSerializationException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
