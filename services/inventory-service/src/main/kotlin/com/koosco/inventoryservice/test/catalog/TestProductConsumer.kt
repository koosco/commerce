package com.koosco.inventoryservice.test.catalog

import com.fasterxml.jackson.databind.ObjectMapper
import com.koosco.common.core.event.CloudEvent
import com.koosco.inventoryservice.application.contract.inbound.catalog.ProductSkuCreatedEvent
import com.koosco.inventoryservice.application.usecase.InventorySeedUseCase
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

/**
 * fileName       : TestProductConsumer
 * author         : koo
 * date           : 2025. 12. 27. 오후 6:52
 * description    : Integration Event 발행 테스트를 위한 Consumer, local profile only
 */
@Profile("local")
@Component
class TestProductConsumer(
    private val objectMapper: ObjectMapper,
    private val inventorySeedUseCase: InventorySeedUseCase,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val receivedEvents = ConcurrentHashMap<String, MutableList<Any>>()

    @KafkaListener(
        topics = ["\${inventory.topic.mappings.product.sku.created}"],
        groupId = "inventory-service-test",
    )
    fun onProductSkuCreated(event: CloudEvent<*>) {
        logger.info("✅ [TEST] ProductSkuCreated received: eventId=${event.id}")

        event.data?.let {
            try {
                val productSkuCreated =
                    objectMapper.convertValue(it, ProductSkuCreatedEvent::class.java)
                logger.info("  → orderId=${productSkuCreated.skuId}, items=${productSkuCreated.productCode}")
                addEvent("product.sku.created", productSkuCreated)
            } catch (e: Exception) {
                logger.error("Failed to deserialize productSkuCreatedEvent: eventId=${event.id}", e)
            }
        }
    }

    private fun addEvent(eventType: String, event: Any) {
        receivedEvents.computeIfAbsent(eventType) { mutableListOf() }.add(event)
    }

    fun getAllReceivedEvents(): Map<String, List<Any>> = receivedEvents.mapValues { it.value.toList() }

    fun clearReceivedEvents() {
        receivedEvents.clear()
        inventorySeedUseCase.clear()
        logger.info("🧹 [TEST] All received events cleared")
    }
}
