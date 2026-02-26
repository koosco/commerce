package com.koosco.catalogservice.infra.messaging.kafka.consumer

import com.koosco.catalogservice.application.usecase.UpdateProductStockStatusUseCase
import com.koosco.catalogservice.contract.inbound.inventory.StockDepletedEvent
import com.koosco.catalogservice.contract.inbound.inventory.StockRestoredEvent
import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.util.JsonUtils.objectMapper
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

/**
 * inventory-service에서 발행하는 재고 소진/복구 이벤트를 소비하여
 * 상품 상태를 OUT_OF_STOCK 또는 ACTIVE로 변경
 */
@Component
@Validated
class KafkaStockEventConsumer(private val updateProductStockStatusUseCase: UpdateProductStockStatusUseCase) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${catalog.topic.consumer.inventory.stock}"],
        groupId = "\${spring.kafka.consumer.group-id}",
    )
    fun onStockEvent(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
        val payload = event.data
            ?: run {
                logger.error("Stock event data is null: eventId=${event.id}")
                ack.acknowledge()
                return
            }

        try {
            when (event.type) {
                EVENT_TYPE_STOCK_DEPLETED -> handleStockDepleted(event, payload)
                EVENT_TYPE_STOCK_RESTORED -> handleStockRestored(event, payload)
                else -> logger.debug("Ignoring unhandled event type: ${event.type}")
            }
        } catch (e: Exception) {
            logger.error(
                "Failed to process stock event: eventId=${event.id}, type=${event.type}",
                e,
            )
            throw e
        }

        ack.acknowledge()
    }

    private fun handleStockDepleted(event: CloudEvent<*>, payload: Any) {
        val stockDepleted = try {
            objectMapper.convertValue(payload, StockDepletedEvent::class.java)
        } catch (e: Exception) {
            logger.error("Failed to deserialize StockDepletedEvent: eventId=${event.id}", e)
            return // poison message -> skip
        }

        logger.info(
            "Received StockDepletedEvent: eventId={}, skuId={}, orderId={}",
            event.id,
            stockDepleted.skuId,
            stockDepleted.orderId,
        )

        updateProductStockStatusUseCase.markOutOfStock(stockDepleted.skuId)
    }

    private fun handleStockRestored(event: CloudEvent<*>, payload: Any) {
        val stockRestored = try {
            objectMapper.convertValue(payload, StockRestoredEvent::class.java)
        } catch (e: Exception) {
            logger.error("Failed to deserialize StockRestoredEvent: eventId=${event.id}", e)
            return // poison message -> skip
        }

        logger.info(
            "Received StockRestoredEvent: eventId={}, skuId={}, orderId={}",
            event.id,
            stockRestored.skuId,
            stockRestored.orderId,
        )

        updateProductStockStatusUseCase.markActive(stockRestored.skuId)
    }

    companion object {
        private const val EVENT_TYPE_STOCK_DEPLETED = "stock.depleted"
        private const val EVENT_TYPE_STOCK_RESTORED = "stock.restored"
    }
}
