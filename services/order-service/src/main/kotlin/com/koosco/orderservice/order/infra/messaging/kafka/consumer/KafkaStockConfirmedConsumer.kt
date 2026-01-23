package com.koosco.orderservice.order.infra.messaging.kafka.consumer

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.util.JsonUtils.objectMapper
import com.koosco.orderservice.order.application.command.MarkOrderConfirmedCommand
import com.koosco.orderservice.order.application.contract.inbound.inventory.StockConfirmedEvent
import com.koosco.orderservice.order.application.usecase.MarkOrderConfirmedUseCase
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

/**
 * fileName       : KafkaStockConfirmedConsumer
 * author         : koo
 * date           : 2025. 12. 23. 오전 12:51
 * description    :
 */
@Component
class KafkaStockConfirmedConsumer(private val markOrderConfirmedUseCase: MarkOrderConfirmedUseCase) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${order.topic.mappings.stock.confirmed}"],
        groupId = "\${spring.kafka.consumer.group-id:order-service-group}",
    )
    fun onStockConfirmed(event: CloudEvent<*>, ack: Acknowledgment) {
        val payload = event.data
            ?: run {
                logger.error("StockConfirmedEvent is null: eventId=${event.id}")
                ack.acknowledge()
                return
            }

        val stockConfirmedEvent = try {
            objectMapper.convertValue(payload, StockConfirmedEvent::class.java)
        } catch (e: Exception) {
            logger.error("Failed to deserialize StockConfirmedEvent: eventId=${event.id}", e)
            ack.acknowledge()
            return
        }

        logger.info(
            "Received StockConfirmed: eventId=${event.id}, orderId=${stockConfirmedEvent.orderId}",
        )

        try {
            markOrderConfirmedUseCase.execute(
                MarkOrderConfirmedCommand(
                    orderId = stockConfirmedEvent.orderId,
                    reservationId = stockConfirmedEvent.reservationId,
                    items = stockConfirmedEvent.items.map {
                        MarkOrderConfirmedCommand.MarkedConfirmedItem(it.skuId, it.quantity)
                    },
                ),
            )

            ack.acknowledge()

            logger.info(
                "Successfully confirmed order: eventId=${event.id}, orderId=${stockConfirmedEvent.orderId}",
            )
        } catch (e: Exception) {
            logger.error(
                "Failed to process StockConfirmed event: eventId=${event.id}, orderId=${stockConfirmedEvent.orderId}",
                e,
            )
        }
    }
}
