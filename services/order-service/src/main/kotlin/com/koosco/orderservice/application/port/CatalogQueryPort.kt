package com.koosco.orderservice.application.port

/**
 * catalog-service 동기 조회 포트.
 * 주문 생성 시 SKU 가격/상태 검증에 사용한다.
 */
interface CatalogQueryPort {

    fun getSkuInfos(skuIds: List<Long>): Map<Long, SkuInfo>

    data class SkuInfo(
        val skuPkId: Long,
        val skuId: String,
        val productId: Long,
        val productName: String,
        val price: Long,
        val status: String,
    )
}
