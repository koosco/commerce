package com.koosco.orderservice.test.payment

import com.koosco.common.core.event.CloudEvent
import com.koosco.orderservice.application.contract.inbound.payment.PaymentCompletedEvent
import com.koosco.orderservice.application.contract.inbound.payment.PaymentCreatedEvent
import com.koosco.orderservice.application.contract.inbound.payment.PaymentFailedEvent
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.*

/**
 * fileName       : PaymentSeeder
 * author         : koo
 * date           : 2025. 12. 24. 오후 9:58
 * description    : 결제 이벤트 발행 테스트를 위한 Service, local profile only
 */
@Profile("local")
@Component
class TestPaymentService(
    private val kafkaTemplate: KafkaTemplate<String, CloudEvent<*>>,
    @Value("\${order.topic.mappings.payment.created}") private val createdTopic: String,
    @Value("\${order.topic.mappings.payment.completed}") private val completedTopic: String,
    @Value("\${order.topic.mappings.payment.failed}") private val failedTopic: String,
) {

    companion object {
        const val ORDER_ID = 11111111111
    }

    fun created(orderId: Long = ORDER_ID): PaymentCreatedEvent {
        val event = PaymentCreatedEvent(
            paymentId = UUID.randomUUID().toString(),
            orderId = orderId,
            correlationId = UUID.randomUUID().toString(),
            causationId = null,
        )

        val cloudEvent = CloudEvent.of(
            source = "payment-service",
            type = "payment.created",
            subject = "payment/${event.paymentId}",
            data = event,
        )

        kafkaTemplate.send(createdTopic, orderId.toString(), cloudEvent)
        return event
    }

    fun completed(orderId: Long = ORDER_ID): PaymentCompletedEvent {
        val event = PaymentCompletedEvent(
            orderId = orderId,
            paymentId = UUID.randomUUID().toString(),
            transactionId = "TXN-${UUID.randomUUID()}",
            paidAmount = 10000L,
            currency = "KRW",
            approvedAt = System.currentTimeMillis(),
            correlationId = UUID.randomUUID().toString(),
            causationId = null,
        )

        val cloudEvent = CloudEvent.of(
            source = "payment-service",
            type = "payment.completed",
            subject = "payment/${event.paymentId}",
            data = event,
        )

        kafkaTemplate.send(completedTopic, orderId.toString(), cloudEvent)
        return event
    }

    fun failed(orderId: Long = ORDER_ID): PaymentFailedEvent {
        val event = PaymentFailedEvent(
            orderId = orderId,
            paymentId = UUID.randomUUID().toString(),
            transactionId = "TXN-${UUID.randomUUID()}",
            cancelledAmount = 10000L,
            currency = "KRW",
            reason = "PAYMENT_TIMEOUT",
            cancelledAt = System.currentTimeMillis(),
            correlationId = UUID.randomUUID().toString(),
            causationId = null,
        )

        val cloudEvent = CloudEvent.of(
            source = "payment-service",
            type = "payment.failed",
            subject = "payment/${event.paymentId}",
            data = event,
        )

        kafkaTemplate.send(failedTopic, orderId.toString(), cloudEvent)
        return event
    }
}
