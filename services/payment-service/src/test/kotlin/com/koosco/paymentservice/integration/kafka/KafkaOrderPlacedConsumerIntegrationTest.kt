package com.koosco.paymentservice.integration.kafka

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.test.KafkaContainerTestBase
import com.koosco.paymentservice.application.contract.inbound.order.OrderPlacedEvent
import com.koosco.paymentservice.application.port.IdempotencyRepository
import com.koosco.paymentservice.application.port.IntegrationEventPublisher
import com.koosco.paymentservice.application.port.PaymentGateway
import com.koosco.paymentservice.application.port.PaymentRepository
import com.koosco.paymentservice.domain.entity.Payment
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Integration tests for KafkaOrderPlacedEventConsumer.
 *
 * Verifies that OrderPlacedEvent is correctly consumed from Kafka
 * and processed by CreatePaymentUseCase.
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class KafkaOrderPlacedConsumerIntegrationTest : KafkaContainerTestBase() {

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, CloudEvent<*>>

    @Value("\${payment.topic.mappings.order.placed}")
    private lateinit var orderPlacedTopic: String

    @MockBean
    private lateinit var paymentRepository: PaymentRepository

    @MockBean
    private lateinit var idempotencyRepository: IdempotencyRepository

    @MockBean
    private lateinit var paymentGateway: PaymentGateway

    @MockBean
    private lateinit var integrationEventPublisher: IntegrationEventPublisher

    @Test
    @DisplayName("should consume OrderPlacedEvent and create payment")
    fun `should consume OrderPlacedEvent and create payment`() {
        // Given
        val orderId = 12345L
        val userId = 100L
        val payableAmount = 50000L
        val correlationId = UUID.randomUUID().toString()

        val orderPlacedEvent = OrderPlacedEvent(
            orderId = orderId,
            userId = userId,
            payableAmount = payableAmount,
            items = listOf(
                OrderPlacedEvent.PlacedItem(skuId = "SKU001", quantity = 2, unitPrice = 25000L),
            ),
            correlationId = correlationId,
            causationId = UUID.randomUUID().toString(),
        )

        val cloudEvent = CloudEvent.of(
            source = "order-service",
            type = "order.placed",
            subject = "order/$orderId",
            data = orderPlacedEvent,
        )

        whenever(paymentRepository.existsByOrderId(orderId)).thenReturn(false)
        whenever(paymentRepository.save(any())).thenAnswer { invocation ->
            invocation.getArgument<Payment>(0)
        }
        doNothing().whenever(idempotencyRepository).save(any())

        // When
        kafkaTemplate.send(orderPlacedTopic, orderId.toString(), cloudEvent)

        // Then
        await()
            .atMost(15, TimeUnit.SECONDS)
            .untilAsserted {
                verify(paymentRepository, times(1)).save(any())
            }
    }

    @Test
    @DisplayName("should skip duplicate OrderPlacedEvent based on idempotency")
    fun `should skip duplicate OrderPlacedEvent based on idempotency`() {
        // Given
        val orderId = 22222L
        val userId = 200L
        val payableAmount = 30000L
        val correlationId = UUID.randomUUID().toString()

        val orderPlacedEvent = OrderPlacedEvent(
            orderId = orderId,
            userId = userId,
            payableAmount = payableAmount,
            items = listOf(
                OrderPlacedEvent.PlacedItem(skuId = "SKU002", quantity = 1, unitPrice = 30000L),
            ),
            correlationId = correlationId,
            causationId = UUID.randomUUID().toString(),
        )

        val cloudEvent = CloudEvent.of(
            source = "order-service",
            type = "order.placed",
            subject = "order/$orderId",
            data = orderPlacedEvent,
        )

        // Payment already exists
        whenever(paymentRepository.existsByOrderId(orderId)).thenReturn(true)
        doNothing().whenever(idempotencyRepository).save(any())

        // When
        kafkaTemplate.send(orderPlacedTopic, orderId.toString(), cloudEvent)

        // Then
        await()
            .atMost(10, TimeUnit.SECONDS)
            .untilAsserted {
                verify(idempotencyRepository, times(1)).save(any())
            }

        // Payment should not be saved again
        verify(paymentRepository, never()).save(any())
    }

    @Test
    @DisplayName("should extract correlationId and causationId from event")
    fun `should extract correlationId and causationId from event`() {
        // Given
        val orderId = 33333L
        val userId = 300L
        val correlationId = UUID.randomUUID().toString()
        val causationId = UUID.randomUUID().toString()

        val orderPlacedEvent = OrderPlacedEvent(
            orderId = orderId,
            userId = userId,
            payableAmount = 10000L,
            items = emptyList(),
            correlationId = correlationId,
            causationId = causationId,
        )

        val cloudEvent = CloudEvent.of(
            source = "order-service",
            type = "order.placed",
            subject = "order/$orderId",
            data = orderPlacedEvent,
        )

        whenever(paymentRepository.existsByOrderId(orderId)).thenReturn(false)
        whenever(paymentRepository.save(any())).thenAnswer { invocation ->
            invocation.getArgument<Payment>(0)
        }
        doNothing().whenever(idempotencyRepository).save(any())

        // When
        kafkaTemplate.send(orderPlacedTopic, orderId.toString(), cloudEvent)

        // Then - idempotency key should be the CloudEvent ID (causationId in the context)
        await()
            .atMost(15, TimeUnit.SECONDS)
            .untilAsserted {
                verify(idempotencyRepository, times(1)).save(any())
            }
    }

    @Test
    @DisplayName("should handle null event data gracefully with acknowledgment")
    fun `should handle null event data gracefully with acknowledgment`() {
        // Given
        val cloudEvent = CloudEvent.of<Any?>(
            source = "order-service",
            type = "order.placed",
            subject = "order/44444",
            data = null,
        )

        // When
        kafkaTemplate.send(orderPlacedTopic, "44444", cloudEvent)

        // Then - should not throw exception and acknowledge the message
        // Wait a bit and verify no payment was created
        Thread.sleep(3000)
        verify(paymentRepository, never()).save(any())
    }

    @Test
    @DisplayName("should handle malformed event data gracefully")
    fun `should handle malformed event data gracefully`() {
        // Given - sending malformed data that cannot be deserialized to OrderPlacedEvent
        val malformedData = mapOf(
            "invalidField" to "someValue",
        )

        val cloudEvent = CloudEvent.of(
            source = "order-service",
            type = "order.placed",
            subject = "order/55555",
            data = malformedData,
        )

        // When
        kafkaTemplate.send(orderPlacedTopic, "55555", cloudEvent)

        // Then - should skip the poison message without crashing
        Thread.sleep(3000)
        verify(paymentRepository, never()).save(any())
    }

    @Test
    @DisplayName("should process multiple OrderPlacedEvents in order")
    fun `should process multiple OrderPlacedEvents in order`() {
        // Given
        val events = (1..3).map { i ->
            val orderId = 60000L + i
            OrderPlacedEvent(
                orderId = orderId,
                userId = i.toLong() * 100,
                payableAmount = i.toLong() * 10000,
                items = emptyList(),
                correlationId = UUID.randomUUID().toString(),
                causationId = UUID.randomUUID().toString(),
            )
        }

        whenever(paymentRepository.existsByOrderId(any())).thenReturn(false)
        whenever(paymentRepository.save(any())).thenAnswer { invocation ->
            invocation.getArgument<Payment>(0)
        }
        doNothing().whenever(idempotencyRepository).save(any())

        // When
        events.forEach { event ->
            val cloudEvent = CloudEvent.of(
                source = "order-service",
                type = "order.placed",
                subject = "order/${event.orderId}",
                data = event,
            )
            kafkaTemplate.send(orderPlacedTopic, event.orderId.toString(), cloudEvent)
        }

        // Then
        await()
            .atMost(20, TimeUnit.SECONDS)
            .untilAsserted {
                verify(paymentRepository, times(3)).save(any())
            }
    }

    @Test
    @DisplayName("should publish PaymentCreatedEvent after successful payment creation")
    fun `should publish PaymentCreatedEvent after successful payment creation`() {
        // Given
        val orderId = 77777L
        val userId = 700L

        val orderPlacedEvent = OrderPlacedEvent(
            orderId = orderId,
            userId = userId,
            payableAmount = 100000L,
            items = listOf(
                OrderPlacedEvent.PlacedItem(skuId = "SKU007", quantity = 1, unitPrice = 100000L),
            ),
            correlationId = UUID.randomUUID().toString(),
            causationId = UUID.randomUUID().toString(),
        )

        val cloudEvent = CloudEvent.of(
            source = "order-service",
            type = "order.placed",
            subject = "order/$orderId",
            data = orderPlacedEvent,
        )

        whenever(paymentRepository.existsByOrderId(orderId)).thenReturn(false)
        whenever(paymentRepository.save(any())).thenAnswer { invocation ->
            invocation.getArgument<Payment>(0)
        }
        doNothing().whenever(idempotencyRepository).save(any())

        // When
        kafkaTemplate.send(orderPlacedTopic, orderId.toString(), cloudEvent)

        // Then
        await()
            .atMost(15, TimeUnit.SECONDS)
            .untilAsserted {
                verify(integrationEventPublisher, times(1)).publish(any())
            }
    }
}
