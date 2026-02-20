package com.koosco.inventoryservice.infra.messaging.kafka.consumer

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.messaging.MessageContext
import com.koosco.common.core.util.JsonUtils.objectMapper
import com.koosco.inventoryservice.application.command.ConfirmStockCommand
import com.koosco.inventoryservice.application.usecase.ConfirmStockUseCase
import com.koosco.inventoryservice.contract.inbound.order.OrderConfirmedEvent
import com.koosco.inventoryservice.domain.entity.InventoryEventIdempotency.Companion.Actions
import com.koosco.inventoryservice.domain.exception.NotEnoughStockException
import com.koosco.inventoryservice.infra.idempotency.IdempotencyChecker
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

/**
 * OrderConfirmedEvent 처리 - 재고 확정
 */
@Component
@Validated
class KafkaOrderConfirmedEventConsumer(
    private val confirmStockUseCase: ConfirmStockUseCase,
    private val idempotencyChecker: IdempotencyChecker,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${inventory.topic.mappings.order.confirmed}"],
        groupId = "\${spring.kafka.consumer.group-id}",
    )
    fun onOrderConfirmed(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
        val payload = event.data
            ?: run {
                logger.error("OrderConfirmedEvent is null: eventId=${event.id}")
                ack.acknowledge()
                return
            }

        val orderConfirmed = try {
            objectMapper.convertValue(payload, OrderConfirmedEvent::class.java)
        } catch (e: Exception) {
            logger.error("Failed to deserialize OrderConfirmedEvent: eventId=${event.id}", e)
            ack.acknowledge() // poison message → skip
            return
        }

        // Idempotency fast-path check
        if (idempotencyChecker.isAlreadyProcessed(event.id, Actions.CONFIRM_STOCK)) {
            logger.info("Event already processed: eventId=${event.id}, orderId=${orderConfirmed.orderId}")
            ack.acknowledge()
            return
        }

        logger.info(
            "Received OrderConfirmedEvent: eventId=${event.id}, orderId=${orderConfirmed.orderId}",
        )

        val context = MessageContext(
            correlationId = orderConfirmed.correlationId,
            causationId = event.id,
        )

        val command = ConfirmStockCommand(
            orderId = orderConfirmed.orderId,
            items = orderConfirmed.items.map { item ->
                ConfirmStockCommand.ConfirmedSku(
                    skuId = item.skuId,
                    quantity = item.quantity,
                )
            },
        )

        try {
            confirmStockUseCase.execute(command, context)

            // Record idempotency after successful processing
            idempotencyChecker.recordProcessed(
                messageId = event.id,
                action = Actions.CONFIRM_STOCK,
                aggregateId = orderConfirmed.orderId.toString(),
            )

            ack.acknowledge()
            logger.info(
                "Successfully confirmed stock: eventId=${event.id}, orderId=${orderConfirmed.orderId}",
            )
        } catch (_: NotEnoughStockException) {
            logger.warn(
                "Stock confirmation failed: eventId=${event.id}, orderId=${orderConfirmed.orderId}",
            )
            ack.acknowledge()
        } catch (e: Exception) {
            logger.error(
                "Failed to process OrderConfirmedEvent: eventId=${event.id}, orderId=${orderConfirmed.orderId}",
                e,
            )
            throw e
        }
    }
}
