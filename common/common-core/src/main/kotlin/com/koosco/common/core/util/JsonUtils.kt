package com.koosco.common.core.util

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory

/**
 * JSON 직렬화/역직렬화를 위한 유틸리티 클래스.
 * Kotlin Module 및 Java Time Module이 포함된 Jackson ObjectMapper를 사용합니다.
 */
object JsonUtils {

    @PublishedApi
    internal val log = LoggerFactory.getLogger(javaClass)

    /**
     * 기본 설정이 적용된 ObjectMapper 인스턴스.
     */
    val objectMapper: ObjectMapper = jacksonObjectMapper().apply {
        // Java 8 time support
        registerModule(JavaTimeModule())

        // Serialization settings
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)

        // Deserialization settings
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    }

    /**
     * 객체를 JSON 문자열로 직렬화합니다.
     *
     * @param obj 직렬화할 객체
     * @return JSON 문자열 (실패 시 null 반환)
     */
    fun toJson(obj: Any?): String? {
        if (obj == null) return null
        return try {
            objectMapper.writeValueAsString(obj)
        } catch (e: JsonProcessingException) {
            log.warn("Failed to serialize object to JSON: {}", e.message)
            null
        }
    }

    /**
     * 객체를 JSON 문자열로 직렬화합니다. (실패 시 예외 발생)
     *
     * @param obj 직렬화할 객체
     * @return JSON 문자열
     * @throws JsonProcessingException 직렬화 실패 시 발생
     */
    @Throws(JsonProcessingException::class)
    fun toJsonOrThrow(obj: Any): String = objectMapper.writeValueAsString(obj)

    /**
     * 객체를 보기 좋은 형태의 JSON 문자열로 직렬화합니다.
     *
     * @param obj 직렬화할 객체
     * @return Pretty JSON 문자열 (실패 시 null 반환)
     */
    fun toPrettyJson(obj: Any?): String? {
        if (obj == null) return null
        return try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj)
        } catch (e: JsonProcessingException) {
            log.warn("Failed to serialize object to pretty JSON: {}", e.message)
            null
        }
    }

    /**
     * JSON 문자열을 객체로 역직렬화합니다.
     *
     * @param json JSON 문자열
     * @return 역직렬화된 객체 (실패 시 null)
     */
    inline fun <reified T> fromJson(json: String?): T? {
        if (json.isNullOrBlank()) return null
        return try {
            objectMapper.readValue(json)
        } catch (e: JsonProcessingException) {
            log.warn("Failed to deserialize JSON to object: {}", e.message)
            null
        }
    }

    /**
     * JSON 문자열을 객체로 역직렬화합니다. (실패 시 예외 발생)
     *
     * @param json JSON 문자열
     * @return 역직렬화된 객체
     * @throws JsonProcessingException 역직렬화 실패 시 발생
     */
    @Throws(JsonProcessingException::class)
    inline fun <reified T> fromJsonOrThrow(json: String): T = objectMapper.readValue(json)

    /**
     * Class 타입을 사용한 JSON 역직렬화.
     *
     * @param json JSON 문자열
     * @param clazz 대상 클래스 타입
     * @return 역직렬화된 객체 (실패 시 null)
     */
    fun <T> fromJson(json: String?, typeReference: TypeReference<T>): T? {
        if (json.isNullOrBlank()) return null
        return try {
            objectMapper.readValue(json, typeReference)
        } catch (e: JsonProcessingException) {
            log.warn("Failed to deserialize JSON to object: {}", e.message)
            null
        }
    }

    /**
     * JSON 문자열을 JsonNode로 파싱합니다.
     *
     * @param json JSON 문자열
     * @return JsonNode (실패 시 null)
     */
    fun <T> fromJson(json: String?, clazz: Class<T>): T? {
        if (json.isNullOrBlank()) return null
        return try {
            objectMapper.readValue(json, clazz)
        } catch (e: JsonProcessingException) {
            log.warn("Failed to deserialize JSON to object: {}", e.message)
            null
        }
    }

    /**
     * 객체를 다른 타입으로 변환합니다. (주로 Map → DTO 변환에 사용)
     *
     * @param obj 원본 객체
     * @return 변환된 객체 (실패 시 null)
     */
    fun parseJson(json: String?): JsonNode? {
        if (json.isNullOrBlank()) return null
        return try {
            objectMapper.readTree(json)
        } catch (e: JsonProcessingException) {
            log.warn("Failed to parse JSON: {}", e.message)
            null
        }
    }

    /**
     * 객체를 다른 타입(TypeReference 기반)으로 변환합니다.
     *
     * @param obj 원본 객체
     * @param typeReference 타입 참조
     * @return 변환된 객체 (실패 시 null)
     */
    inline fun <reified T> convertValue(obj: Any?): T? {
        if (obj == null) return null
        return try {
            objectMapper.convertValue(obj, T::class.java)
        } catch (e: IllegalArgumentException) {
            log.warn("Failed to convert value: {}", e.message)
            null
        }
    }

    /**
     * 문자열이 유효한 JSON인지 확인합니다.
     *
     * @param json 검사할 문자열
     * @return JSON 포맷이 유효하면 true, 아니면 false
     */
    fun <T> convertValue(obj: Any?, typeReference: TypeReference<T>): T? {
        if (obj == null) return null
        return try {
            objectMapper.convertValue(obj, typeReference)
        } catch (e: IllegalArgumentException) {
            log.warn("Failed to convert value: {}", e.message)
            null
        }
    }

    /**
     * Check if a string is valid JSON.
     *
     * @param json String to validate
     * @return true if valid JSON, false otherwise
     */
    fun isValidJson(json: String?): Boolean {
        if (json.isNullOrBlank()) return false
        return try {
            objectMapper.readTree(json)
            true
        } catch (e: JsonProcessingException) {
            false
        }
    }
}
