package com.koosco.catalogservice.api.response

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.koosco.catalogservice.domain.entity.ProductSku

/**
 * SKU 조회 응답
 */
data class SkuResponse(
    val skuId: String,
    val productId: Long,
    val price: Long,
    val optionValues: Map<String, String>,
    val available: Boolean, // 재고 있는지 여부 (향후 inventory-service 연동)
) {
    companion object {
        fun from(sku: ProductSku, available: Boolean = true): SkuResponse {
            // JSON 문자열을 Map으로 파싱
            val optionValuesMap = jacksonObjectMapper()
                .readValue(sku.optionValues, Map::class.java) as Map<String, String>

            return SkuResponse(
                skuId = sku.skuId,
                productId = sku.product.id!!,
                price = sku.price,
                optionValues = optionValuesMap,
                available = available,
            )
        }
    }
}
