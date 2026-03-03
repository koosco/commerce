package com.koosco.catalogservice.api.response

import com.koosco.catalogservice.domain.entity.Product
import com.koosco.catalogservice.domain.entity.ProductSku

/**
 * 내부 API용 SKU 정보 응답
 * 주문 서비스에서 상품/가격 검증 시 사용
 */
data class SkuInfoResponse(
    val skuPkId: Long,
    val skuId: String,
    val productId: Long,
    val productName: String,
    val price: Long,
    val status: String,
) {
    companion object {
        fun from(sku: ProductSku, product: Product): SkuInfoResponse = SkuInfoResponse(
            skuPkId = sku.id!!,
            skuId = sku.skuId,
            productId = product.id!!,
            productName = product.name,
            price = sku.price,
            status = sku.status.name,
        )
    }
}
