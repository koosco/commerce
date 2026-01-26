package com.koosco.paymentservice.integration.kafka

import com.koosco.common.core.test.KafkaContainerTestBase
import com.koosco.paymentservice.application.contract.outbound.payment.PaymentCreatedEvent
import com.koosco.paymentservice.application.port.IdempotencyRepository
import com.koosco.paymentservice.application.port.IntegrationEventPublisher
import com.koosco.paymentservice.application.port.PaymentGateway
import com.koosco.paymentservice.application.port.PaymentRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration
import java.util.UUID

/**
 * Integration tests for KafkaIntegrationEventPublisher.
 *
 * Verifies that PaymentCreatedEvent is correctly published to Kafka
 * in CloudEvent format.
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class KafkaPaymentEventPublisherIntegrationTest : KafkaContainerTestBase() {

    @Autowired
    private lateinit var eventPublisher: IntegrationEventPublisher

    @Value("\${payment.topic.mappings.payment.created}")
    private lateinit var paymentCreatedTopic: String

    @MockBean
    private lateinit var paymentRepository: PaymentRepository

    @MockBean
    private lateinit var idempotencyRepository: IdempotencyRepository

    @MockBean
    private lateinit var paymentGateway: PaymentGateway

    @Test
    @DisplayName("should publish PaymentCreatedEvent with valid CloudEvent format")
    fun `should publish PaymentCreatedEvent with valid CloudEvent format`() {
        // Given
        val paymentId = UUID.randomUUID().toString()
        val orderId = 12345L
        val event = PaymentCreatedEvent(paymentId = paymentId, orderId = orderId)

        // When
        eventPublisher.publish(event)

        // Then
        val records = consumeMessages(paymentCreatedTopic, 1, Duration.ofSeconds(15))

        assertThat(records).hasSize(1)

        val record = records.first()
        assertValidCloudEvent(record)

        val cloudEvent = record.value()
        assertThat(cloudEvent.type).isEqualTo("payment.created")
        assertThat(cloudEvent.source).isEqualTo("payment-service-test")
        assertThat(cloudEvent.specVersion).isEqualTo("1.0")
        assertThat(cloudEvent.subject).isEqualTo("payment/$paymentId")
        assertThat(cloudEvent.dataContentType).isEqualTo("application/json")
    }

    @Test
    @DisplayName("should use paymentId as Kafka partition key")
    fun `should use paymentId as Kafka partition key`() {
        // Given
        val paymentId = UUID.randomUUID().toString()
        val orderId = 67890L
        val event = PaymentCreatedEvent(paymentId = paymentId, orderId = orderId)

        // When
        eventPublisher.publish(event)

        // Then
        val records = consumeMessages(paymentCreatedTopic, 1, Duration.ofSeconds(15))

        assertThat(records).hasSize(1)
        assertThat(records.first().key()).isEqualTo(paymentId)
    }

    @Test
    @DisplayName("should include event data in CloudEvent payload")
    fun `should include event data in CloudEvent payload`() {
        // Given
        val paymentId = UUID.randomUUID().toString()
        val orderId = 11111L
        val event = PaymentCreatedEvent(paymentId = paymentId, orderId = orderId)

        // When
        eventPublisher.publish(event)

        // Then
        val records = consumeMessages(paymentCreatedTopic, 1, Duration.ofSeconds(15))
        assertThat(records).hasSize(1)

        val cloudEvent = records.first().value()
        assertThat(cloudEvent.data).isNotNull

        val data = cloudEvent.data as Map<*, *>
        assertThat(data["paymentId"]).isEqualTo(paymentId)
        assertThat(data["orderId"]).isEqualTo(orderId.toInt())
    }

    @Test
    @DisplayName("should publish multiple events independently")
    fun `should publish multiple events independently`() {
        // Given
        val events = (1..3).map { i ->
            PaymentCreatedEvent(
                paymentId = UUID.randomUUID().toString(),
                orderId = i.toLong(),
            )
        }

        // When
        events.forEach { eventPublisher.publish(it) }

        // Then
        val records = consumeMessages(paymentCreatedTopic, 3, Duration.ofSeconds(20))

        assertThat(records).hasSize(3)
        records.forEach { record ->
            assertValidCloudEvent(record)
            assertEventType(record, "payment.created")
        }
    }

    @Test
    @DisplayName("should generate unique event ID for each published event")
    fun `should generate unique event ID for each published event`() {
        // Given
        val event1 = PaymentCreatedEvent(
            paymentId = UUID.randomUUID().toString(),
            orderId = 1L,
        )
        val event2 = PaymentCreatedEvent(
            paymentId = UUID.randomUUID().toString(),
            orderId = 2L,
        )

        // When
        eventPublisher.publish(event1)
        eventPublisher.publish(event2)

        // Then
        val records = consumeMessages(paymentCreatedTopic, 2, Duration.ofSeconds(15))

        assertThat(records).hasSize(2)

        val eventIds = records.map { it.value().id }
        assertThat(eventIds).doesNotHaveDuplicates()
    }
}
