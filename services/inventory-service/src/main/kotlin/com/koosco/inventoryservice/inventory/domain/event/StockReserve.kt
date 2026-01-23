package com.koosco.inventoryservice.inventory.domain.event

import com.koosco.common.core.event.DomainEvent
import java.time.Instant
import java.util.*

/**
 * fileName       : StockReservedEvent
 * author         : koo
 * date           : 2025. 12. 19. 오후 1:07
 * description    :
 */
/**
 * 재고 예약 성공 이벤트
 */
data class StockReserved(
    val skuId: String,
    val quantity: Int,

    override val eventId: String = UUID.randomUUID().toString(),
    override val occurredAt: Instant = Instant.now(),
) : DomainEvent {

    override fun getAggregateId(): String = skuId

    override fun getEventType(): String = "StockReserved"
}

/**
 * 재고 예약 확정 이벤트
 */
data class StockReservationConfirmed(
    val skuId: String,
    val quantity: Int,

    override val eventId: String = UUID.randomUUID().toString(),
    override val occurredAt: Instant = Instant.now(),
) : DomainEvent {

    override fun getAggregateId(): String = skuId

    override fun getEventType(): String = "StockReservationConfirmed"
}

/**
 * 재고 예약 취소 이벤트
 */
data class StockReservationCanceled(
    val skuId: String,
    val quantity: Int,

    override val eventId: String = UUID.randomUUID().toString(),
    override val occurredAt: Instant = Instant.now(),
) : DomainEvent {

    override fun getAggregateId(): String = skuId

    override fun getEventType(): String = "StockReservationCanceled"
}
