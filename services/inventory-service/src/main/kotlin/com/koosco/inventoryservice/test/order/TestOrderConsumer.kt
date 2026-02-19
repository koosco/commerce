package com.koosco.inventoryservice.test.order

import com.fasterxml.jackson.databind.ObjectMapper
import com.koosco.common.core.event.CloudEvent
import com.koosco.inventoryservice.application.usecase.InventorySeedUseCase
import com.koosco.inventoryservice.contract.outbound.inventory.StockConfirmFailedEvent
import com.koosco.inventoryservice.contract.outbound.inventory.StockConfirmedEvent
import com.koosco.inventoryservice.contract.outbound.inventory.StockReservationFailedEvent
import com.koosco.inventoryservice.contract.outbound.inventory.StockReservedEvent
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

/**
 * fileName       : TestOrderConsumer
 * author         : koo
 * date           : 2025. 12. 26. 오전 5:15
 * description    : Integration Event 발행 테스트를 위한 Consumer, local profile only
 */
@Profile("local")
@Component
class TestOrderConsumer(
    private val objectMapper: ObjectMapper,
    private val inventorySeedUseCase: InventorySeedUseCase,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    // 수신한 이벤트를 메모리에 저장 (테스트 검증용)
    private val receivedEvents = ConcurrentHashMap<String, MutableList<Any>>()

    @KafkaListener(
        topics = ["\${inventory.topic.mappings.stock.reserved}"],
        groupId = "inventory-service-test",
        containerFactory = "kafkaListenerContainerFactory",
    )
    fun onStockReserved(event: CloudEvent<*>) {
        logger.info("✅ [TEST] StockReserved received: eventId=${event.id}")

        event.data?.let {
            try {
                val stockReservedEvent = objectMapper.convertValue(it, StockReservedEvent::class.java)
                logger.info("  → orderId=${stockReservedEvent.orderId}, items=${stockReservedEvent.items}")
                addEvent("stock.reserved", stockReservedEvent)
            } catch (e: Exception) {
                logger.error("Failed to deserialize StockReservedEvent: eventId=${event.id}", e)
            }
        }
    }

    @KafkaListener(
        topics = ["\${inventory.topic.mappings.stock.reservation.failed}"],
        groupId = "inventory-service-test",
        containerFactory = "kafkaListenerContainerFactory",
    )
    fun onStockReservationFailed(event: CloudEvent<*>) {
        logger.warn("❌ [TEST] StockReservationFailed received: eventId=${event.id}")

        event.data?.let { payload ->
            try {
                val failedEvent = objectMapper.convertValue(payload, StockReservationFailedEvent::class.java)
                logger.warn("  → orderId=${failedEvent.orderId}, reason=${failedEvent.reason}")
                addEvent("stock.reservation.failed", failedEvent)
            } catch (e: Exception) {
                logger.error("Failed to deserialize StockReservationFailedEvent: eventId=${event.id}", e)
            }
        }
    }

    @KafkaListener(
        topics = ["\${inventory.topic.mappings.stock.confirmed}"],
        groupId = "inventory-service-test",
        containerFactory = "kafkaListenerContainerFactory",
    )
    fun onStockConfirmed(event: CloudEvent<*>) {
        logger.info("✅ [TEST] StockConfirmed received: eventId=${event.id}")

        event.data?.let { payload ->
            try {
                val confirmedEvent = objectMapper.convertValue(payload, StockConfirmedEvent::class.java)
                logger.info("  → orderId=${confirmedEvent.orderId}, items=${confirmedEvent.items}")
                addEvent("stock.confirmed", confirmedEvent)
            } catch (e: Exception) {
                logger.error("Failed to deserialize StockConfirmedEvent: eventId=${event.id}", e)
            }
        }
    }

    @KafkaListener(
        topics = ["\${inventory.topic.mappings.stock.confirm.failed}"],
        groupId = "inventory-service-test",
        containerFactory = "kafkaListenerContainerFactory",
    )
    fun onStockConfirmFailed(event: CloudEvent<*>) {
        logger.warn("❌ [TEST] StockConfirmFailed received: eventId=${event.id}")

        event.data?.let { payload ->
            try {
                val failedEvent = objectMapper.convertValue(payload, StockConfirmFailedEvent::class.java)
                logger.warn("  → orderId=${failedEvent.orderId}, reason=${failedEvent.reason}")
                addEvent("stock.confirm.failed", failedEvent)
            } catch (e: Exception) {
                logger.error("Failed to deserialize StockConfirmFailedEvent: eventId=${event.id}", e)
            }
        }
    }

    private fun addEvent(eventType: String, event: Any) {
        receivedEvents.computeIfAbsent(eventType) { mutableListOf() }.add(event)
    }

    /**
     * 수신한 이벤트 조회 (테스트 검증용)
     */
    fun getReceivedEvents(eventType: String): List<Any> = receivedEvents[eventType]?.toList() ?: emptyList()

    /**
     * 모든 수신 이벤트 조회
     */
    fun getAllReceivedEvents(): Map<String, List<Any>> = receivedEvents.mapValues { it.value.toList() }

    /**
     * 수신한 이벤트 초기화 (테스트 간 독립성 보장)
     */
    fun clearReceivedEvents() {
        receivedEvents.clear()
        inventorySeedUseCase.clear()
        logger.info("🧹 [TEST] All received events cleared")
    }
}
