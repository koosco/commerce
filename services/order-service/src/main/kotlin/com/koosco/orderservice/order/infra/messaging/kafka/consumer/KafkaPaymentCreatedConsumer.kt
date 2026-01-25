package com.koosco.orderservice.order.infra.messaging.kafka.consumer

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.messaging.MessageContext
import com.koosco.common.core.util.JsonUtils.objectMapper
import com.koosco.orderservice.order.application.command.MarkOrderPaymentCreatedCommand
import com.koosco.orderservice.order.application.contract.inbound.payment.PaymentCreatedEvent
import com.koosco.orderservice.order.application.usecase.MarkOrderPaymentCreatedUseCase
import com.koosco.orderservice.order.domain.entity.OrderEventIdempotency.Companion.Actions
import com.koosco.orderservice.order.infra.idempotency.IdempotencyChecker
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

/**
 * 결제 생성 이벤트 핸들러
 */
@Component
@Validated
class KafkaPaymentCreatedConsumer(
    private val markOrderPaymentCreatedUseCase: MarkOrderPaymentCreatedUseCase,
    private val idempotencyChecker: IdempotencyChecker,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${order.topic.mappings.payment.created}"],
        groupId = "\${spring.kafka.consumer.group-id}",
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

        // Idempotency fast-path check
        if (idempotencyChecker.isAlreadyProcessed(event.id, Actions.MARK_PAYMENT_CREATED)) {
            logger.info("Event already processed: eventId=${event.id}, orderId=${paymentCreated.orderId}")
            ack.acknowledge()
            return
        }

        logger.info(
            "Received PaymentCreatedEvent: eventId=${event.id}, orderId=${paymentCreated.orderId}",
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

            // Record idempotency after successful processing
            idempotencyChecker.recordProcessed(
                eventId = event.id,
                action = Actions.MARK_PAYMENT_CREATED,
                orderId = paymentCreated.orderId,
            )

            ack.acknowledge()

            logger.info(
                "Successfully marked payment created: eventId=${event.id}, orderId=${paymentCreated.orderId}",
            )
        } catch (e: Exception) {
            logger.error("Transient failure, will retry: eventId=${event.id}", e)
            throw e
        }
    }
}
