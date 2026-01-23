package com.koosco.orderservice.order.infra.messaging.kafka.consumer

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.util.JsonUtils.objectMapper
import com.koosco.orderservice.order.application.command.MarkOrderPaymentPendingCommand
import com.koosco.orderservice.order.application.contract.inbound.inventory.StockReservedEvent
import com.koosco.orderservice.order.application.usecase.MarkOrderPaymentPendingUseCase
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

/**
 * fileName       : KafkaStockReservedConsumer
 * author         : koo
 * date           : 2025. 12. 22. 오전 6:18
 * description    :
 */
@Component
class KafkaStockReservedConsumer(private val markOrderPaymentPendingUseCase: MarkOrderPaymentPendingUseCase) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${order.topic.mappings.stock.reserved}"],
        groupId = "\${spring.kafka.consumer.group-id:order-service-group}",
    )
    fun onStockReserved(event: CloudEvent<*>, ack: Acknowledgment) {
        val payload = event.data
            ?: run {
                logger.error("StockReservedEvent is null: eventId=${event.id}")
                ack.acknowledge()
                return
            }

        val stockReserved = try {
            objectMapper.convertValue(payload, StockReservedEvent::class.java)
        } catch (e: Exception) {
            logger.error("Failed to deserialize StockReservedEvent: eventId=${event.id}", e)
            ack.acknowledge()
            return
        }

        markOrderPaymentPendingUseCase.execute(
            MarkOrderPaymentPendingCommand(
                orderId = stockReserved.orderId,
            ),
        )

        ack.acknowledge()
    }
}
