package com.koosco.inventoryservice.inventory.infra.messaging.kafka.consumer

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.util.JsonUtils.objectMapper
import com.koosco.inventoryservice.common.MessageContext
import com.koosco.inventoryservice.inventory.application.command.ConfirmStockCommand
import com.koosco.inventoryservice.inventory.application.contract.inbound.order.OrderConfirmedEvent
import com.koosco.inventoryservice.inventory.application.usecase.ConfirmStockUseCase
import com.koosco.inventoryservice.inventory.domain.exception.NotEnoughStockException
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

/**
 * fileName       : KafkaOrderConfirmedEventConsumer
 * author         : koo
 * date           : 2025. 12. 19. 오후 2:27
 * description    :
 */
@Component
@Validated
class KafkaOrderConfirmedEventConsumer(private val confirmStockUseCase: ConfirmStockUseCase) {
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

        logger.info(
            "Received OrderConfirmedEvent: eventId=${event.id}, orderId=${orderConfirmed.orderId}, items=${orderConfirmed.items}",
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

            ack.acknowledge()
            logger.info(
                "Successfully confirmed stock for ORDER: eventId=${event.id}, orderId=${orderConfirmed.orderId}, items=${orderConfirmed.items}",
            )
        } catch (_: NotEnoughStockException) {
            logger.warn(
                "Stock confirmation failed: eventId=${event.id}, orderId=${orderConfirmed.orderId}",
            )
            ack.acknowledge()
        } catch (e: Exception) {
            logger.error(
                "Failed to process OrderConfirmedEvent event: eventId=${event.id}, orderId=${orderConfirmed.orderId}",
                e,
            )
            throw e
        }
    }
}
