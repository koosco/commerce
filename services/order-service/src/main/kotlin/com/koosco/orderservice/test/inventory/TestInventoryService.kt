package com.koosco.orderservice.test.inventory

import com.koosco.common.core.event.CloudEvent
import com.koosco.orderservice.contract.inbound.inventory.StockConfirmFailedEvent
import com.koosco.orderservice.contract.inbound.inventory.StockConfirmedEvent
import com.koosco.orderservice.contract.inbound.inventory.StockReserveFailedEvent
import com.koosco.orderservice.contract.inbound.inventory.StockReservedEvent
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

/**
 * fileName       : TestInventoryService
 * author         : koo
 * date           : 2025. 12. 26. 오전 7:11
 * description    : 재고 이벤트 발행 테스트를 위한 Service, local profile only
 */
@Profile("local")
@Service
class TestInventoryService(
    private val kafkaTemplate: KafkaTemplate<String, CloudEvent<*>>,
    @Value("\${order.topic.mappings.stock.reserved}") private val stockReservedTopic: String,
    @Value("\${order.topic.mappings.stock.reservation.failed}") private val stockReservationFailedTopic: String,
    @Value("\${order.topic.mappings.stock.confirmed}") private val stockConfirmedTopic: String,
    @Value("\${order.topic.mappings.stock.confirm.failed}") private val stockConfirmFailedTopic: String,
) {
    companion object {
        const val ORDER_ID = 11111111111L
        const val RESERVATION_ID = "test-reservation-id"
        const val FIRST_SKU_ID = "00001f4c-a36c-4a70-9347-413ce52d5d61"
        const val SECOND_SKU_ID = "0000298f-0c73-4df1-8576-ac232687c290"
    }

    fun stockReserved(): StockReservedEvent {
        val stockReservedEvent = StockReservedEvent(
            orderId = ORDER_ID,
            items = listOf(
                StockReservedEvent.ReservedItem(FIRST_SKU_ID, 10),
                StockReservedEvent.ReservedItem(SECOND_SKU_ID, 10),
            ),
            correlationId = "test-correlation-${ORDER_ID}",
            causationId = "test-causation-${System.currentTimeMillis()}",
        )

        kafkaTemplate.send(
            stockReservedTopic,
            CloudEvent.of(
                source = "inventory-service.test-inventory",
                type = "stock.reserved",
                data = stockReservedEvent,
            ),
        )
        return stockReservedEvent
    }

    fun stockReservationFailed(): StockReserveFailedEvent {
        val stockReservationFailedEvent = StockReserveFailedEvent(
            orderId = ORDER_ID,
            reason = "NOT_ENOUGH_STOCK",
            failedItems = listOf(
                StockReserveFailedEvent.ReserveFailedItem(FIRST_SKU_ID, 10, 5),
                StockReserveFailedEvent.ReserveFailedItem(SECOND_SKU_ID, 10, 0),
            ),
            occurredAt = System.currentTimeMillis(),
            correlationId = "test-correlation-$ORDER_ID",
            causationId = "test-causation-${System.currentTimeMillis()}",
        )

        kafkaTemplate.send(
            stockReservationFailedTopic,
            CloudEvent.of(
                source = "inventory-service.test-inventory",
                type = "stock.reservation.failed",
                data = stockReservationFailedEvent,
            ),
        )
        return stockReservationFailedEvent
    }

    fun stockConfirmed(): StockConfirmedEvent {
        val stockConfirmedEvent = StockConfirmedEvent(
            orderId = ORDER_ID,
            reservationId = RESERVATION_ID,
            items = listOf(
                StockConfirmedEvent.ConfirmedItem(FIRST_SKU_ID, 10),
                StockConfirmedEvent.ConfirmedItem(SECOND_SKU_ID, 10),
            ),
            correlationId = "test-correlation-${ORDER_ID}",
            causationId = "test-causation-${System.currentTimeMillis()}",
        )

        kafkaTemplate.send(
            stockConfirmedTopic,
            CloudEvent.of(
                source = "inventory-service.test-inventory",
                type = "stock.confirmed",
                data = stockConfirmedEvent,
            ),
        )
        return stockConfirmedEvent
    }

    fun stockConfirmFailed(): StockConfirmFailedEvent {
        val stockConfirmFailedEvent = StockConfirmFailedEvent(
            orderId = ORDER_ID,
            reservationId = RESERVATION_ID,
            reason = "RESERVATION_NOT_FOUND",
            correlationId = "test-correlation-${ORDER_ID}",
            causationId = "test-causation-${System.currentTimeMillis()}",
        )

        kafkaTemplate.send(
            stockConfirmFailedTopic,
            CloudEvent.of(
                source = "inventory-service.test-inventory",
                type = "stock.confirm.failed",
                data = stockConfirmFailedEvent,
            ),
        )
        return stockConfirmFailedEvent
    }
}
