package com.koosco.orderservice.domain.vo

data class PricingSnapshot(
    val subtotal: Long,
    val discount: Long,
    val shippingFee: Long,
    val total: Long,
    val currency: String = "KRW",
    val items: List<PricingSnapshotItem>,
) {
    data class PricingSnapshotItem(val skuId: Long, val unitPrice: Long, val qty: Int, val lineAmount: Long)
}
