package com.koosco.catalogservice.api.response

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.koosco.catalogservice.application.result.SkuResult

/**
 * SKU 조회 응답
 */
data class SkuResponse(
    val skuId: String,
    val productId: Long,
    val price: Long,
    val optionValues: Map<String, String>,
    val available: Boolean,
) {
    companion object {
        fun from(skuResult: SkuResult): SkuResponse {
            val sku = skuResult.sku
            // JSON 문자열을 Map으로 파싱
            val optionValuesMap = jacksonObjectMapper()
                .readValue(sku.optionValues, Map::class.java) as Map<String, String>

            return SkuResponse(
                skuId = sku.skuId,
                productId = sku.product.id!!,
                price = sku.price,
                optionValues = optionValuesMap,
                available = skuResult.available,
            )
        }
    }
}
