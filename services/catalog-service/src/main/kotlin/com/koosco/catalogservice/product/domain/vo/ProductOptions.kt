package com.koosco.catalogservice.product.domain.vo

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

/**
 * 상품 옵션 조합을 나타내는 Value Object
 * - 옵션의 순서와 무관하게 동일한 키-값 쌍을 가지면 동일한 것으로 간주
 * - 내부적으로 정규화된 형태로 저장하여 일관된 비교 보장
 */
data class ProductOptions(private val options: Map<String, String>) {

    companion object {
        private val objectMapper = jacksonObjectMapper()

        /**
         * JSON 문자열로부터 ProductOptions 생성
         */
        fun fromJson(json: String): ProductOptions {
            val map = objectMapper.readValue(json, Map::class.java) as Map<String, String>
            return ProductOptions(map)
        }

        /**
         * Map으로부터 ProductOptions 생성
         */
        fun from(options: Map<String, String>): ProductOptions = ProductOptions(options)
    }

    /**
     * 정규화된 옵션 (알파벳 순 정렬)
     * - 비교 시 순서와 무관하게 일관된 결과 보장
     */
    private val normalized: Map<String, String> = options.entries
        .sortedBy { it.key }
        .associate { it.key to it.value }

    /**
     * JSON 문자열로 변환 (정렬된 순서 유지)
     */
    fun toJson(): String = objectMapper.writeValueAsString(
        normalized.entries.associateTo(linkedMapOf()) { it.key to it.value },
    )

    /**
     * 원본 Map 반환
     */
    fun asMap(): Map<String, String> = normalized.toMap()

    /**
     * 옵션이 비어있는지 확인
     */
    fun isEmpty(): Boolean = normalized.isEmpty()

    /**
     * 동일성 비교 - 정규화된 옵션 기준
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProductOptions) return false
        return normalized == other.normalized
    }

    override fun hashCode(): Int = normalized.hashCode()

    override fun toString(): String = normalized.toString()
}
