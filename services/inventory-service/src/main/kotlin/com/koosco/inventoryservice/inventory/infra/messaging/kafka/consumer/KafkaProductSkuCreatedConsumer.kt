package com.koosco.inventoryservice.inventory.infra.messaging.kafka.consumer

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.util.JsonUtils.objectMapper
import com.koosco.inventoryservice.inventory.application.command.InitStockCommand
import com.koosco.inventoryservice.inventory.application.contract.inbound.catalog.ProductSkuCreatedEvent
import com.koosco.inventoryservice.inventory.application.usecase.InitializeStockUseCase
import com.koosco.inventoryservice.inventory.domain.entity.InventoryEventIdempotency.Companion.Actions
import com.koosco.inventoryservice.inventory.infra.idempotency.IdempotencyChecker
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

/**
 * ProductSkuCreatedEvent 처리 - 재고 초기화
 */
@Component
@Validated
class KafkaProductSkuCreatedConsumer(
    private val initializeStockUseCase: InitializeStockUseCase,
    private val idempotencyChecker: IdempotencyChecker,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${inventory.topic.mappings.product.sku.created}"],
        groupId = "\${spring.kafka.consumer.group-id}",
    )
    fun onProductSkuCreated(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
        val payload = event.data
            ?: run {
                logger.error("ProductSkuCreatedEvent is null: eventId=${event.id}")
                ack.acknowledge()
                return
            }

        val skuCreated = try {
            objectMapper.convertValue(payload, ProductSkuCreatedEvent::class.java)
        } catch (e: Exception) {
            logger.error("Failed to deserialize ProductSkuCreatedEvent: eventId=${event.id}", e)
            ack.acknowledge() // poison message → skip
            return
        }

        // Idempotency fast-path check (use skuId as orderId for this case)
        if (idempotencyChecker.isAlreadyProcessed(event.id, Actions.INITIALIZE_STOCK)) {
            logger.info("Event already processed: eventId=${event.id}, skuId=${skuCreated.skuId}")
            ack.acknowledge()
            return
        }

        logger.info(
            "Received ProductSkuCreatedEvent: eventId=${event.id}, skuId=${skuCreated.skuId}, productId=${skuCreated.productId}",
        )

        try {
            initializeStockUseCase.execute(
                InitStockCommand(
                    skuCreated.skuId,
                    skuCreated.initialQuantity,
                ),
            )

            // Record idempotency after successful processing
            idempotencyChecker.recordProcessed(
                eventId = event.id,
                action = Actions.INITIALIZE_STOCK,
                referenceId = skuCreated.skuId,
            )

            ack.acknowledge()

            logger.info(
                "Successfully initialized stock: eventId=${event.id}, skuId=${skuCreated.skuId}, quantity=${skuCreated.initialQuantity}",
            )
        } catch (e: Exception) {
            logger.error(
                "Failed to process ProductSkuCreatedEvent: eventId=${event.id}, skuId=${skuCreated.skuId}",
                e,
            )
            throw e
        }
    }
}
