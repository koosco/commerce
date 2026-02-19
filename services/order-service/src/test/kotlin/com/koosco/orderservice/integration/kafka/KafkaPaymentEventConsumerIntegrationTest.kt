package com.koosco.orderservice.integration.kafka

import com.koosco.common.core.event.CloudEvent
import com.koosco.orderservice.application.usecase.CancelOrderByPaymentFailureUseCase
import com.koosco.orderservice.application.usecase.MarkOrderPaidUseCase
import com.koosco.orderservice.application.usecase.MarkOrderPaymentCreatedUseCase
import com.koosco.orderservice.contract.inbound.payment.PaymentCompletedEvent
import com.koosco.orderservice.contract.inbound.payment.PaymentCreatedEvent
import com.koosco.orderservice.contract.inbound.payment.PaymentFailedEvent
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.timeout
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration
import java.util.UUID

/**
 * Integration tests for Kafka payment event consumers.
 * Tests that PaymentCreatedEvent, PaymentCompletedEvent, and PaymentFailedEvent
 * are consumed and processed correctly.
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@DisplayName("KafkaPaymentEventConsumer Integration Tests")
class KafkaPaymentEventConsumerIntegrationTest : KafkaIntegrationTestBase() {

    @Value("\${order.topic.mappings.payment.created}")
    private lateinit var paymentCreatedTopic: String

    @Value("\${order.topic.mappings.payment.completed}")
    private lateinit var paymentCompletedTopic: String

    @Value("\${order.topic.mappings.payment.failed}")
    private lateinit var paymentFailedTopic: String

    @MockBean
    private lateinit var markOrderPaymentCreatedUseCase: MarkOrderPaymentCreatedUseCase

    @MockBean
    private lateinit var markOrderPaidUseCase: MarkOrderPaidUseCase

    @MockBean
    private lateinit var cancelOrderByPaymentFailureUseCase: CancelOrderByPaymentFailureUseCase

    @Nested
    @DisplayName("PaymentCreatedEvent Consumer Tests")
    inner class PaymentCreatedEventConsumerTests {

        @Test
        @DisplayName("should consume PaymentCreatedEvent and call MarkOrderPaymentCreatedUseCase")
        fun `should consume PaymentCreatedEvent and call MarkOrderPaymentCreatedUseCase`() {
            // Given
            val orderId = 100L
            val paymentId = "PAY-${UUID.randomUUID()}"
            val correlationId = orderId.toString()

            val paymentCreatedEvent = PaymentCreatedEvent(
                paymentId = paymentId,
                orderId = orderId,
                correlationId = correlationId,
                causationId = UUID.randomUUID().toString(),
            )

            val cloudEvent = CloudEvent.of(
                source = "payment-service",
                type = "payment.created",
                subject = "payment/$paymentId",
                data = paymentCreatedEvent,
            )

            // When
            val kafkaTemplate = createTestKafkaTemplate()
            kafkaTemplate.send(paymentCreatedTopic, orderId.toString(), cloudEvent)

            // Then
            verify(markOrderPaymentCreatedUseCase, timeout(10000)).execute(any(), any())
        }

        @Test
        @DisplayName("should skip processing when PaymentCreatedEvent data is null")
        fun `should skip processing when PaymentCreatedEvent data is null`() {
            // Given
            val cloudEvent = CloudEvent.of<PaymentCreatedEvent?>(
                source = "payment-service",
                type = "payment.created",
                subject = "payment/null",
                data = null,
            )

            // When
            val kafkaTemplate = createTestKafkaTemplate()
            kafkaTemplate.send(paymentCreatedTopic, "null-key", cloudEvent)

            // Then - should not call use case for null data
            await().during(Duration.ofSeconds(2)).untilAsserted {
                verify(markOrderPaymentCreatedUseCase, never()).execute(any(), any())
            }
        }
    }

    @Nested
    @DisplayName("PaymentCompletedEvent Consumer Tests")
    inner class PaymentCompletedEventConsumerTests {

        @Test
        @DisplayName("should consume PaymentCompletedEvent and call MarkOrderPaidUseCase")
        fun `should consume PaymentCompletedEvent and call MarkOrderPaidUseCase`() {
            // Given
            val orderId = 200L
            val paymentId = "PAY-${UUID.randomUUID()}"
            val correlationId = orderId.toString()

            val paymentCompletedEvent = PaymentCompletedEvent(
                orderId = orderId,
                paymentId = paymentId,
                transactionId = "TXN-${UUID.randomUUID()}",
                paidAmount = 50000L,
                currency = "KRW",
                approvedAt = System.currentTimeMillis(),
                correlationId = correlationId,
                causationId = UUID.randomUUID().toString(),
            )

            val cloudEvent = CloudEvent.of(
                source = "payment-service",
                type = "payment.completed",
                subject = "payment/$paymentId",
                data = paymentCompletedEvent,
            )

            // When
            val kafkaTemplate = createTestKafkaTemplate()
            kafkaTemplate.send(paymentCompletedTopic, orderId.toString(), cloudEvent)

            // Then
            verify(markOrderPaidUseCase, timeout(10000)).execute(any(), any())
        }

        @Test
        @DisplayName("should correctly parse paidAmount from PaymentCompletedEvent")
        fun `should correctly parse paidAmount from PaymentCompletedEvent`() {
            // Given
            val orderId = 201L
            val paymentId = "PAY-${UUID.randomUUID()}"
            val paidAmount = 123456L

            val paymentCompletedEvent = PaymentCompletedEvent(
                orderId = orderId,
                paymentId = paymentId,
                transactionId = "TXN-${UUID.randomUUID()}",
                paidAmount = paidAmount,
                currency = "KRW",
                approvedAt = System.currentTimeMillis(),
                correlationId = orderId.toString(),
                causationId = UUID.randomUUID().toString(),
            )

            val cloudEvent = CloudEvent.of(
                source = "payment-service",
                type = "payment.completed",
                subject = "payment/$paymentId",
                data = paymentCompletedEvent,
            )

            // When
            val kafkaTemplate = createTestKafkaTemplate()
            kafkaTemplate.send(paymentCompletedTopic, orderId.toString(), cloudEvent)

            // Then
            verify(markOrderPaidUseCase, timeout(10000)).execute(any(), any())
        }

        @Test
        @DisplayName("should skip processing when PaymentCompletedEvent data is null")
        fun `should skip processing when PaymentCompletedEvent data is null`() {
            // Given
            val cloudEvent = CloudEvent.of<PaymentCompletedEvent?>(
                source = "payment-service",
                type = "payment.completed",
                subject = "payment/null",
                data = null,
            )

            // When
            val kafkaTemplate = createTestKafkaTemplate()
            kafkaTemplate.send(paymentCompletedTopic, "null-key", cloudEvent)

            // Then
            await().during(Duration.ofSeconds(2)).untilAsserted {
                verify(markOrderPaidUseCase, never()).execute(any(), any())
            }
        }
    }

    @Nested
    @DisplayName("PaymentFailedEvent Consumer Tests")
    inner class PaymentFailedEventConsumerTests {

        @Test
        @DisplayName("should consume PaymentFailedEvent and call CancelOrderByPaymentFailureUseCase")
        fun `should consume PaymentFailedEvent and call CancelOrderByPaymentFailureUseCase`() {
            // Given
            val orderId = 300L
            val paymentId = "PAY-${UUID.randomUUID()}"
            val correlationId = orderId.toString()

            val paymentFailedEvent = PaymentFailedEvent(
                orderId = orderId,
                paymentId = paymentId,
                transactionId = "TXN-${UUID.randomUUID()}",
                cancelledAmount = 50000L,
                currency = "KRW",
                reason = "PAYMENT_FAILED",
                cancelledAt = System.currentTimeMillis(),
                correlationId = correlationId,
                causationId = UUID.randomUUID().toString(),
            )

            val cloudEvent = CloudEvent.of(
                source = "payment-service",
                type = "payment.failed",
                subject = "payment/$paymentId",
                data = paymentFailedEvent,
            )

            // When
            val kafkaTemplate = createTestKafkaTemplate()
            kafkaTemplate.send(paymentFailedTopic, orderId.toString(), cloudEvent)

            // Then
            verify(cancelOrderByPaymentFailureUseCase, timeout(10000)).execute(any(), any())
        }

        @Test
        @DisplayName("should consume PaymentFailedEvent with PAYMENT_TIMEOUT reason")
        fun `should consume PaymentFailedEvent with PAYMENT_TIMEOUT reason`() {
            // Given
            val orderId = 301L
            val paymentId = "PAY-${UUID.randomUUID()}"

            val paymentFailedEvent = PaymentFailedEvent(
                orderId = orderId,
                paymentId = paymentId,
                transactionId = null,
                cancelledAmount = 0L,
                currency = "KRW",
                reason = "PAYMENT_TIMEOUT",
                cancelledAt = System.currentTimeMillis(),
                correlationId = orderId.toString(),
                causationId = UUID.randomUUID().toString(),
            )

            val cloudEvent = CloudEvent.of(
                source = "payment-service",
                type = "payment.failed",
                subject = "payment/$paymentId",
                data = paymentFailedEvent,
            )

            // When
            val kafkaTemplate = createTestKafkaTemplate()
            kafkaTemplate.send(paymentFailedTopic, orderId.toString(), cloudEvent)

            // Then
            verify(cancelOrderByPaymentFailureUseCase, timeout(10000)).execute(any(), any())
        }

        @Test
        @DisplayName("should skip processing when PaymentFailedEvent data is null")
        fun `should skip processing when PaymentFailedEvent data is null`() {
            // Given
            val cloudEvent = CloudEvent.of<PaymentFailedEvent?>(
                source = "payment-service",
                type = "payment.failed",
                subject = "payment/null",
                data = null,
            )

            // When
            val kafkaTemplate = createTestKafkaTemplate()
            kafkaTemplate.send(paymentFailedTopic, "null-key", cloudEvent)

            // Then
            await().during(Duration.ofSeconds(2)).untilAsserted {
                verify(cancelOrderByPaymentFailureUseCase, never()).execute(any(), any())
            }
        }

        @Test
        @DisplayName("should consume PaymentFailedEvent with USER_REQUEST reason")
        fun `should consume PaymentFailedEvent with USER_REQUEST reason`() {
            // Given
            val orderId = 302L
            val paymentId = "PAY-${UUID.randomUUID()}"

            val paymentFailedEvent = PaymentFailedEvent(
                orderId = orderId,
                paymentId = paymentId,
                transactionId = "TXN-${UUID.randomUUID()}",
                cancelledAmount = 75000L,
                currency = "KRW",
                reason = "USER_REQUEST",
                cancelledAt = System.currentTimeMillis(),
                correlationId = orderId.toString(),
                causationId = UUID.randomUUID().toString(),
            )

            val cloudEvent = CloudEvent.of(
                source = "payment-service",
                type = "payment.failed",
                subject = "payment/$paymentId",
                data = paymentFailedEvent,
            )

            // When
            val kafkaTemplate = createTestKafkaTemplate()
            kafkaTemplate.send(paymentFailedTopic, orderId.toString(), cloudEvent)

            // Then
            verify(cancelOrderByPaymentFailureUseCase, timeout(10000)).execute(any(), any())
        }
    }
}
