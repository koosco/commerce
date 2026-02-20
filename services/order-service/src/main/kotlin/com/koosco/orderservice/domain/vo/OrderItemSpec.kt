package com.koosco.orderservice.domain.vo

import com.koosco.common.core.exception.BadRequestException
import com.koosco.orderservice.common.error.OrderErrorCode

data class OrderItemSpec(
    val skuId: Long,
    val productId: Long,
    val brandId: Long,
    val titleSnapshot: String,
    val optionSnapshot: String?,
    val quantity: Int,
    val unitPrice: Money,
) {

    init {
        if (quantity <= 0) {
            throw BadRequestException(
                OrderErrorCode.INVALID_ORDER_STATUS,
                "수량은 1이상이어야 합니다.",
            )
        }
    }

    fun totalPrice(): Money = unitPrice * quantity
}
