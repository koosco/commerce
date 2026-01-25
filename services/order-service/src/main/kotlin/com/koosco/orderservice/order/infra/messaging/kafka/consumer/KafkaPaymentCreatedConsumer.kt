package com.koosco.orderservice.order.infra.messaging.kafka.consumer

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.util.JsonUtils.objectMapper
import com.koosco.orderservice.common.MessageContext
import com.koosco.orderservice.order.application.command.MarkOrderPaymentCreatedCommand
import com.koosco.orderservice.order.application.contract.inbound.payment.PaymentCreatedEvent
import com.koosco.orderservice.order.application.usecase.MarkOrderPaymentCreatedUseCase
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

/**
 * fileName       : KafkaPaymentCreatedConsumer
 * author         : koo
 * date           : 2025. 12. 24. 오후 9:26
 * description    :
 */
@Component
@Validated
class KafkaPaymentCreatedConsumer(private val markOrderPaymentCreatedUseCase: MarkOrderPaymentCreatedUseCase) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${order.topic.mappings.payment.created}"],
        groupId = "order-service",
    )
    fun onPaymentCreated(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
        val payload = event.data
            ?: run {
                logger.error("PaymentCreatedEvent data is null: eventId=${event.id}")
                ack.acknowledge()
                return
            }

        val paymentCreated = try {
            objectMapper.convertValue(payload, PaymentCreatedEvent::class.java)
        } catch (e: Exception) {
            logger.error("Failed to deserialize PaymentCreatedEvent: eventId=${event.id}", e)
            ack.acknowledge()
            return
        }

        logger.info(
            "Received PaymentCreatedEvent: eventId=${event.id}, " +
                "orderId=${paymentCreated.orderId}, paymentId=...",
        )

        val command = MarkOrderPaymentCreatedCommand(
            orderId = paymentCreated.orderId,
            paymentId = paymentCreated.paymentId,
        )

        val context = MessageContext(
            correlationId = paymentCreated.correlationId,
            causationId = event.id,
        )

        try {
            markOrderPaymentCreatedUseCase.execute(command, context)
        } catch (e: Exception) {
            logger.error("Transient failure, will retry", e)
            throw e
        }
    }
}
