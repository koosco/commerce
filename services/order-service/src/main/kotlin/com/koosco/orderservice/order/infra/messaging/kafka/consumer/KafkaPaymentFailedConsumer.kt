package com.koosco.orderservice.order.infra.messaging.kafka.consumer

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.util.JsonUtils.objectMapper
import com.koosco.orderservice.common.MessageContext
import com.koosco.orderservice.order.application.command.CancelOrderCommand
import com.koosco.orderservice.order.application.contract.inbound.payment.PaymentFailedEvent
import com.koosco.orderservice.order.application.usecase.CancelOrderByPaymentFailureUseCase
import com.koosco.orderservice.order.domain.enums.OrderCancelReason
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

/**
 * fileName       : KafkaPaymentFailedConsumer
 * author         : koo
 * date           : 2025. 12. 23. 오전 2:45
 * description    :
 */
@Component
class KafkaPaymentFailedConsumer(private val cancelOrderByPaymentFailureUseCase: CancelOrderByPaymentFailureUseCase) {

    private val logger = LoggerFactory.getLogger(KafkaPaymentCompletedConsumer::class.java)

    @KafkaListener(
        topics = ["\${order.topic.mappings.payment.failed}"],
        groupId = "\${spring.kafka.consumer.group-id:order-service-group}",
    )
    fun onPaymentFailed(event: CloudEvent<*>, ack: Acknowledgment) {
        val payload = event.data
            ?: run {
                logger.error("PaymentFailedEvent is null: eventId=${event.id}")
                ack.acknowledge()
                return
            }

        val paymentCompleted = try {
            objectMapper.convertValue(payload, PaymentFailedEvent::class.java)
        } catch (e: Exception) {
            logger.error("Failed to deserialize PaymentFailedEvent: eventId=${event.id}", e)
            ack.acknowledge()
            return
        }

        logger.info(
            "Received PaymentFailedEvent: eventId=${event.id}, " +
                "orderId=${paymentCompleted.orderId}, paymentId=...",
        )

        val context = MessageContext(
            correlationId = paymentCompleted.correlationId,
            causationId = event.id,
        )

        cancelOrderByPaymentFailureUseCase.execute(
            CancelOrderCommand(
                orderId = paymentCompleted.orderId,
                reason = OrderCancelReason.valueOf(paymentCompleted.reason),
            ),
            context,
        )

        ack.acknowledge()
    }
}
