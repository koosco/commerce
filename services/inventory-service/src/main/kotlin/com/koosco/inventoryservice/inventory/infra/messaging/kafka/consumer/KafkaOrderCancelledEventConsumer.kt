package com.koosco.inventoryservice.inventory.infra.messaging.kafka.consumer

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.util.JsonUtils.objectMapper
import com.koosco.inventoryservice.common.MessageContext
import com.koosco.inventoryservice.inventory.application.command.CancelStockCommand
import com.koosco.inventoryservice.inventory.application.contract.inbound.order.OrderCancelledEvent
import com.koosco.inventoryservice.inventory.application.usecase.ReleaseStockUseCase
import com.koosco.inventoryservice.inventory.domain.enums.StockCancelReason.Companion.mapCancelReason
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

/**
 * fileName       : KafkaOrderCancelledEventConsumer
 * author         : koo
 * date           : 2025. 12. 19. 오후 3:47
 * description    :
 */
@Component
@Validated
class KafkaOrderCancelledEventConsumer(private val releaseStockUseCase: ReleaseStockUseCase) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${inventory.topic.mappings.order.cancelled}"],
        groupId = "\${spring.kafka.consumer.group-id}",
    )
    fun onOrderCancelled(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
        val payload = event.data
            ?: run {
                logger.error("OrderCanceled is null: eventId=${event.id}")
                ack.acknowledge()
                return
            }

        val orderCancelled = try {
            objectMapper.convertValue(payload, OrderCancelledEvent::class.java)
        } catch (e: Exception) {
            logger.error("Failed to deserialize OrderCancelledEvent: eventId=${event.id}", e)
            ack.acknowledge() // poison message → skip
            return
        }

        logger.info(
            "Received OrderCanceled: eventId=${event.id}, orderId=${orderCancelled.orderId}, items=${orderCancelled.items}, reason=${orderCancelled.reason}",
        )

        val context = MessageContext(
            correlationId = orderCancelled.correlationId,
            causationId = event.id,
        )

        val command = CancelStockCommand(
            orderId = orderCancelled.orderId,
            items = orderCancelled.items.map { item ->
                CancelStockCommand.CancelledSku(
                    skuId = item.skuId,
                    quantity = item.quantity,
                )
            },
            reason = mapCancelReason(orderCancelled.reason),
        )

        try {
            releaseStockUseCase.execute(command, context)

            ack.acknowledge()
            logger.info(
                "Stock reservation cancelled: eventId=${event.id}, orderId=${orderCancelled.orderId}, items=${orderCancelled.items}",
            )
        } catch (e: Exception) {
            logger.error(
                "Failed to process OrderCanceled event: eventId=${event.id}, orderId=${orderCancelled.orderId}",
                e,
            )
            // TODO: 주문 취소에 따른 재고 증가는 반드시 일어나야하므로 재시도 후 DLQ 처리
            throw e
        }
    }
}
