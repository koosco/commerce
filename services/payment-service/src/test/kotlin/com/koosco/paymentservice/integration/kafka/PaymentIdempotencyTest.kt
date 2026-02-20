package com.koosco.paymentservice.integration.kafka

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.test.KafkaContainerTestBase
import com.koosco.paymentservice.application.port.IdempotencyRepository
import com.koosco.paymentservice.application.port.IntegrationEventProducer
import com.koosco.paymentservice.application.port.PaymentGateway
import com.koosco.paymentservice.application.port.PaymentRepository
import com.koosco.paymentservice.contract.inbound.order.OrderPlacedEvent
import com.koosco.paymentservice.domain.entity.Payment
import com.koosco.paymentservice.domain.entity.PaymentIdempotency
import com.koosco.paymentservice.infra.persist.jpa.JpaIdempotencyRepository
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.UUID
import java.util.concurrent.TimeUnit

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class PaymentIdempotencyTest : KafkaContainerTestBase() {

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, CloudEvent<*>>

    @Value("\${payment.topic.mappings.order.placed}")
    private lateinit var orderPlacedTopic: String

    @MockBean
    private lateinit var paymentRepository: PaymentRepository

    @SpyBean
    private lateinit var idempotencyRepository: IdempotencyRepository

    @Autowired
    private lateinit var jpaIdempotencyRepository: JpaIdempotencyRepository

    @MockBean
    private lateinit var paymentGateway: PaymentGateway

    @MockBean
    private lateinit var integrationEventProducer: IntegrationEventProducer

    @BeforeEach
    fun setUp() {
        jpaIdempotencyRepository.deleteAll()
    }

    @Test
    @DisplayName("should save idempotency key when processing new event")
    fun `should save idempotency key when processing new event`() {
        // Given
        val orderId = 10001L
        val causationId = UUID.randomUUID().toString()

        val orderPlacedEvent = OrderPlacedEvent(
            orderId = orderId,
            userId = 100L,
            payableAmount = 50000L,
            items = emptyList(),
            correlationId = UUID.randomUUID().toString(),
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

        // When
        kafkaTemplate.send(orderPlacedTopic, orderId.toString(), cloudEvent)

        // Then
        await()
            .atMost(15, TimeUnit.SECONDS)
            .untilAsserted {
                val savedIdempotency = jpaIdempotencyRepository.findAll()
                assertThat(savedIdempotency).hasSize(1)
                assertThat(savedIdempotency.first().aggregateId).isEqualTo(orderId.toString())
                assertThat(savedIdempotency.first().action).isEqualTo(PaymentIdempotency.Companion.Actions.CREATE)
                assertThat(savedIdempotency.first().messageId).isNotBlank()
            }
    }

    @Test
    @DisplayName("should prevent duplicate payment creation with same idempotency key")
    fun `should prevent duplicate payment creation with same idempotency key`() {
        // Given
        val orderId = 20002L
        val causationId = UUID.randomUUID().toString()

        // Pre-insert idempotency record to simulate already processed event
        val existingIdempotency = PaymentIdempotency.create(
            messageId = causationId,
            action = PaymentIdempotency.Companion.Actions.CREATE,
            aggregateId = orderId.toString(),
        )
        jpaIdempotencyRepository.save(existingIdempotency)

        val orderPlacedEvent = OrderPlacedEvent(
            orderId = orderId,
            userId = 200L,
            payableAmount = 30000L,
            items = emptyList(),
            correlationId = UUID.randomUUID().toString(),
            causationId = causationId,
        )

        // Create CloudEvent with the same ID as causationId to trigger idempotency
        val cloudEvent = CloudEvent(
            id = causationId, // Same as already-saved message ID
            source = "order-service",
            type = "order.placed",
            subject = "order/$orderId",
            data = orderPlacedEvent,
        )

        whenever(paymentRepository.existsByOrderId(orderId)).thenReturn(false)

        // When
        kafkaTemplate.send(orderPlacedTopic, orderId.toString(), cloudEvent)

        // Then - payment should NOT be created due to idempotency constraint
        await()
            .during(3, TimeUnit.SECONDS)
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted {
                verify(paymentRepository, never()).save(any())
            }
    }

    @Test
    @DisplayName("should allow different messageIds for same aggregate")
    fun `should allow different messageIds for same aggregate`() {
        // Given - different message IDs are allowed for the same aggregate
        val orderId = 30001L

        val event1 = OrderPlacedEvent(
            orderId = orderId,
            userId = 300L,
            payableAmount = 10000L,
            items = emptyList(),
            correlationId = UUID.randomUUID().toString(),
            causationId = UUID.randomUUID().toString(),
        )

        val event2 = OrderPlacedEvent(
            orderId = orderId,
            userId = 300L,
            payableAmount = 20000L,
            items = emptyList(),
            correlationId = UUID.randomUUID().toString(),
            causationId = UUID.randomUUID().toString(),
        )

        val cloudEvent1 = CloudEvent.of(
            source = "order-service",
            type = "order.placed",
            subject = "order/$orderId",
            data = event1,
        )

        val cloudEvent2 = CloudEvent.of(
            source = "order-service",
            type = "order.placed",
            subject = "order/$orderId",
            data = event2,
        )

        whenever(paymentRepository.existsByOrderId(any())).thenReturn(false)
        whenever(paymentRepository.save(any())).thenAnswer { invocation ->
            invocation.getArgument<Payment>(0)
        }

        // When
        kafkaTemplate.send(orderPlacedTopic, orderId.toString(), cloudEvent1)
        kafkaTemplate.send(orderPlacedTopic, orderId.toString(), cloudEvent2)

        // Then - both should be saved because message IDs are different
        await()
            .atMost(20, TimeUnit.SECONDS)
            .untilAsserted {
                verify(paymentRepository, times(2)).save(any())

                val savedIdempotencies = jpaIdempotencyRepository.findAll()
                assertThat(savedIdempotencies).hasSize(2)
            }
    }

    @Test
    @DisplayName("should handle concurrent duplicate events with idempotency")
    fun `should handle concurrent duplicate events with idempotency`() {
        // Given
        val orderId = 40004L
        val causationId = UUID.randomUUID().toString()

        val orderPlacedEvent = OrderPlacedEvent(
            orderId = orderId,
            userId = 400L,
            payableAmount = 100000L,
            items = emptyList(),
            correlationId = UUID.randomUUID().toString(),
            causationId = causationId,
        )

        val cloudEvent = CloudEvent(
            id = causationId,
            source = "order-service",
            type = "order.placed",
            subject = "order/$orderId",
            data = orderPlacedEvent,
        )

        whenever(paymentRepository.existsByOrderId(orderId)).thenReturn(false)
        whenever(paymentRepository.save(any())).thenAnswer { invocation ->
            invocation.getArgument<Payment>(0)
        }

        // When - send the same event multiple times (simulating duplicates)
        repeat(3) {
            kafkaTemplate.send(orderPlacedTopic, orderId.toString(), cloudEvent)
        }

        // Then - only one payment should be created
        await()
            .atMost(20, TimeUnit.SECONDS)
            .untilAsserted {
                val idempotencyRecords = jpaIdempotencyRepository.findAll()
                assertThat(idempotencyRecords).hasSize(1)
            }
    }

    @Test
    @DisplayName("should distinguish different actions for same aggregate")
    fun `should distinguish different actions for same aggregate`() {
        // Given - idempotency is per (messageId, action)
        val orderId = 50005L
        val messageId1 = UUID.randomUUID().toString()
        val messageId2 = UUID.randomUUID().toString()

        val createIdempotency = PaymentIdempotency.create(
            messageId = messageId1,
            action = PaymentIdempotency.Companion.Actions.CREATE,
            aggregateId = orderId.toString(),
        )
        val approveIdempotency = PaymentIdempotency.create(
            messageId = messageId2,
            action = PaymentIdempotency.Companion.Actions.APPROVE,
            aggregateId = orderId.toString(),
        )

        // When
        jpaIdempotencyRepository.save(createIdempotency)
        jpaIdempotencyRepository.save(approveIdempotency)

        // Then - both should be saved because actions are different
        val savedRecords = jpaIdempotencyRepository.findAll()
        assertThat(savedRecords).hasSize(2)
        assertThat(savedRecords.map { it.action }).containsExactlyInAnyOrder(
            PaymentIdempotency.Companion.Actions.CREATE,
            PaymentIdempotency.Companion.Actions.APPROVE,
        )
    }

    @Test
    @DisplayName("should include timestamp in idempotency record")
    fun `should include timestamp in idempotency record`() {
        // Given
        val orderId = 60006L
        val idempotencyKey = UUID.randomUUID().toString()

        val orderPlacedEvent = OrderPlacedEvent(
            orderId = orderId,
            userId = 600L,
            payableAmount = 75000L,
            items = emptyList(),
            correlationId = UUID.randomUUID().toString(),
            causationId = idempotencyKey,
        )

        val cloudEvent = CloudEvent(
            id = idempotencyKey,
            source = "order-service",
            type = "order.placed",
            subject = "order/$orderId",
            data = orderPlacedEvent,
        )

        whenever(paymentRepository.existsByOrderId(orderId)).thenReturn(false)
        whenever(paymentRepository.save(any())).thenAnswer { invocation ->
            invocation.getArgument<Payment>(0)
        }

        // When
        kafkaTemplate.send(orderPlacedTopic, orderId.toString(), cloudEvent)

        // Then
        await()
            .atMost(15, TimeUnit.SECONDS)
            .untilAsserted {
                val savedIdempotency = jpaIdempotencyRepository.findAll()
                assertThat(savedIdempotency).hasSize(1)
                assertThat(savedIdempotency.first().processedAt).isNotNull()
            }
    }

    @Test
    @DisplayName("should fail fast when causationId is null")
    fun `should fail when causationId is null in event`() {
        // Given - event without causationId
        val orderId = 70007L

        val orderPlacedEvent = OrderPlacedEvent(
            orderId = orderId,
            userId = 700L,
            payableAmount = 25000L,
            items = emptyList(),
            correlationId = UUID.randomUUID().toString(),
            causationId = null, // No causation ID
        )

        val cloudEvent = CloudEvent.of(
            source = "order-service",
            type = "order.placed",
            subject = "order/$orderId",
            data = orderPlacedEvent,
        )

        // When
        kafkaTemplate.send(orderPlacedTopic, orderId.toString(), cloudEvent)

        // Then - payment should not be created because causationId is required
        await()
            .during(3, TimeUnit.SECONDS)
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted {
                verify(paymentRepository, never()).save(any())
            }
    }
}
