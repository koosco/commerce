package com.koosco.paymentservice.infra.messaging.kafka.consumer

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.messaging.MessageContext
import com.koosco.common.core.util.JsonUtils.objectMapper
import com.koosco.paymentservice.application.command.CreatePaymentCommand
import com.koosco.paymentservice.application.usecase.CreatePaymentUseCase
import com.koosco.paymentservice.contract.inbound.order.OrderPlacedEvent
import com.koosco.paymentservice.domain.entity.PaymentIdempotency
import com.koosco.paymentservice.infra.idempotency.IdempotencyChecker
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

@Component
@Validated
class KafkaOrderPlacedEventConsumer(
    private val createPaymentUseCase: CreatePaymentUseCase,
    private val idempotencyChecker: IdempotencyChecker,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${payment.topic.mappings.order.placed}"],
        groupId = "\${spring.kafka.consumer.group-id}",
    )
    fun onOrderPlaced(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
        val rawData = event.data
            ?: run {
                logger.error("OrderPlacedEvent data is null: eventId=${event.id}")
                ack.acknowledge()
                return
            }

        val orderPlacedEvent = try {
            objectMapper.convertValue(rawData, OrderPlacedEvent::class.java)
        } catch (e: Exception) {
            logger.error("Failed to deserialize OrderPlacedEvent: eventId=${event.id}", e)
            ack.acknowledge() // poison message -> skip
            return
        }

        logger.info(
            "Received OrderPlacedEvent: eventId=${event.id}, " +
                "orderId=${orderPlacedEvent.orderId}",
        )

        if (idempotencyChecker.isAlreadyProcessed(event.id, PaymentIdempotency.Companion.Actions.CREATE)) {
            logger.info("Event already processed: eventId=${event.id}")
            ack.acknowledge()
            return
        }

        val command = CreatePaymentCommand(
            orderId = orderPlacedEvent.orderId,
            userId = orderPlacedEvent.userId,
            amount = orderPlacedEvent.payableAmount,
        )

        val context = MessageContext(
            correlationId = orderPlacedEvent.correlationId,
            causationId = event.id,
        )

        createPaymentUseCase.execute(command, context)
        idempotencyChecker.recordProcessed(
            event.id,
            PaymentIdempotency.Companion.Actions.CREATE,
            command.orderId.toString(),
        )
        ack.acknowledge()
    }
}
