package com.koosco.catalogservice.contract.outbound

import com.koosco.catalogservice.domain.enums.PromotionType
import com.koosco.common.core.event.IntegrationEvent
import java.time.LocalDateTime

data class PromotionActivatedEvent(
    val promotionId: Long,
    val productId: Long,
    val productCode: String,
    val originalPrice: Long,
    val discountPrice: Long,
    val type: PromotionType,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val activatedAt: LocalDateTime,
) : IntegrationEvent {
    override val aggregateId: String get() = productId.toString()

    override fun getEventType(): String = "promotion.activated"

    override fun getSubject(): String = "product/$productId/promotion/$promotionId"
}
