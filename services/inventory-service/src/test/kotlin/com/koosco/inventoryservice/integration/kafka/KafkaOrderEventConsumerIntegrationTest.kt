package com.koosco.inventoryservice.integration.kafka

import com.koosco.common.core.event.CloudEvent
import com.koosco.inventoryservice.application.port.InventoryStockSnapshotQueryPort
import com.koosco.inventoryservice.application.port.InventoryStockStorePort
import com.koosco.inventoryservice.contract.inbound.order.OrderCancelledEvent
import com.koosco.inventoryservice.contract.inbound.order.OrderConfirmedEvent
import com.koosco.inventoryservice.contract.inbound.order.OrderPlacedEvent
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration
import java.util.UUID

/**
 * Integration tests for Kafka order event consumers.
 *
 * Tests that the consumers correctly process OrderPlacedEvent,
 * OrderConfirmedEvent, and OrderCancelledEvent from Kafka topics.
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class KafkaOrderEventConsumerIntegrationTest : KafkaContainerTestBase() {

    @MockitoBean
    private lateinit var inventoryStockStore: InventoryStockStorePort

    @MockitoBean
    private lateinit var inventoryStockSnapshotQueryPort: InventoryStockSnapshotQueryPort

    @Value("\${inventory.topic.mappings.order.placed}")
    private lateinit var orderPlacedTopic: String

    @Value("\${inventory.topic.mappings.order.confirmed}")
    private lateinit var orderConfirmedTopic: String

    @Value("\${inventory.topic.mappings.order.cancelled}")
    private lateinit var orderCancelledTopic: String

    private lateinit var kafkaTemplate: KafkaTemplate<String, CloudEvent<*>>

    @BeforeEach
    fun setUp() {
        // Create topics before tests
        createTopics(orderPlacedTopic, orderConfirmedTopic, orderCancelledTopic)

        reset(inventoryStockStore)
        kafkaTemplate = createTestKafkaTemplate()

        // Default mock behavior
        doNothing().whenever(inventoryStockStore).reserve(any(), any())
        doNothing().whenever(inventoryStockStore).confirm(any(), any())
        doNothing().whenever(inventoryStockStore).cancel(any(), any())
    }

    @Test
    fun `should consume OrderPlacedEvent and call reserve stock use case`() {
        // Given
        val orderId = 1001L
        val correlationId = UUID.randomUUID().toString()
        val causationId = UUID.randomUUID().toString()

        val orderPlacedEvent = OrderPlacedEvent(
            orderId = orderId,
            userId = 100L,
            payableAmount = 50000L,
            items = listOf(
                OrderPlacedEvent.PlacedItem(skuId = "SKU-001", quantity = 2, unitPrice = 10000L),
                OrderPlacedEvent.PlacedItem(skuId = "SKU-002", quantity = 3, unitPrice = 10000L),
            ),
            correlationId = correlationId,
            causationId = causationId,
        )

        val cloudEvent = CloudEvent.of(
            source = "order-service",
            type = "order.placed",
            data = orderPlacedEvent,
            subject = "order/$orderId",
        )

        // When
        kafkaTemplate.send(orderPlacedTopic, orderId.toString(), cloudEvent).get()

        // Then
        await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(inventoryStockStore, atLeastOnce()).reserve(any(), any())
        }
    }

    @Test
    fun `should consume OrderConfirmedEvent and call confirm stock use case`() {
        // Given
        val orderId = 1002L
        val correlationId = UUID.randomUUID().toString()
        val causationId = UUID.randomUUID().toString()

        val orderConfirmedEvent = OrderConfirmedEvent(
            orderId = orderId,
            items = listOf(
                OrderConfirmedEvent.ConfirmedItem(skuId = "SKU-001", quantity = 2),
                OrderConfirmedEvent.ConfirmedItem(skuId = "SKU-002", quantity = 3),
            ),
            correlationId = correlationId,
            causationId = causationId,
        )

        val cloudEvent = CloudEvent.of(
            source = "order-service",
            type = "order.confirmed",
            data = orderConfirmedEvent,
            subject = "order/$orderId",
        )

        // When
        kafkaTemplate.send(orderConfirmedTopic, orderId.toString(), cloudEvent).get()

        // Then
        await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(inventoryStockStore, atLeastOnce()).confirm(any(), any())
        }
    }

    @Test
    fun `should consume OrderCancelledEvent and call release stock use case`() {
        // Given
        val orderId = 1003L
        val correlationId = UUID.randomUUID().toString()
        val causationId = UUID.randomUUID().toString()

        val orderCancelledEvent = OrderCancelledEvent(
            orderId = orderId,
            reason = "USER_CANCELLED",
            items = listOf(
                OrderCancelledEvent.CancelledItem(skuId = "SKU-001", quantity = 2),
                OrderCancelledEvent.CancelledItem(skuId = "SKU-002", quantity = 3),
            ),
            correlationId = correlationId,
            causationId = causationId,
        )

        val cloudEvent = CloudEvent.of(
            source = "order-service",
            type = "order.cancelled",
            data = orderCancelledEvent,
            subject = "order/$orderId",
        )

        // When
        kafkaTemplate.send(orderCancelledTopic, orderId.toString(), cloudEvent).get()

        // Then
        await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(inventoryStockStore, atLeastOnce()).cancel(any(), any())
        }
    }

    @Test
    fun `should handle null data in CloudEvent gracefully`() {
        // Given
        val orderId = 1004L

        val cloudEvent = CloudEvent.of<OrderPlacedEvent>(
            source = "order-service",
            type = "order.placed",
            data = null,
            subject = "order/$orderId",
        )

        // When
        kafkaTemplate.send(orderPlacedTopic, orderId.toString(), cloudEvent).get()

        // Then - should not throw exception, just ack the message
        Thread.sleep(2000) // Give time for consumer to process

        // Verify reserve was NOT called since data is null
        verify(inventoryStockStore, org.mockito.kotlin.never()).reserve(any(), any())
    }

    @Test
    fun `should process multiple OrderPlacedEvents with different order ids`() {
        // Given
        val events = (1..3).map { index ->
            val orderId = 2000L + index
            val orderPlacedEvent = OrderPlacedEvent(
                orderId = orderId,
                userId = 100L,
                payableAmount = 10000L * index,
                items = listOf(
                    OrderPlacedEvent.PlacedItem(skuId = "SKU-00$index", quantity = index, unitPrice = 10000L),
                ),
                correlationId = UUID.randomUUID().toString(),
                causationId = UUID.randomUUID().toString(),
            )
            CloudEvent.of(
                source = "order-service",
                type = "order.placed",
                data = orderPlacedEvent,
                subject = "order/$orderId",
            ) to orderId.toString()
        }

        // When
        events.forEach { (event, key) ->
            kafkaTemplate.send(orderPlacedTopic, key, event).get()
        }

        // Then
        await().atMost(Duration.ofSeconds(20)).untilAsserted {
            verify(inventoryStockStore, org.mockito.kotlin.atLeast(3)).reserve(any(), any())
        }
    }

    @Test
    fun `should handle OrderCancelledEvent with PAYMENT_FAILED reason`() {
        // Given
        val orderId = 1005L
        val correlationId = UUID.randomUUID().toString()
        val causationId = UUID.randomUUID().toString()

        val orderCancelledEvent = OrderCancelledEvent(
            orderId = orderId,
            reason = "PAYMENT_FAILED",
            items = listOf(
                OrderCancelledEvent.CancelledItem(skuId = "SKU-001", quantity = 1),
            ),
            correlationId = correlationId,
            causationId = causationId,
        )

        val cloudEvent = CloudEvent.of(
            source = "order-service",
            type = "order.cancelled",
            data = orderCancelledEvent,
            subject = "order/$orderId",
        )

        // When
        kafkaTemplate.send(orderCancelledTopic, orderId.toString(), cloudEvent).get()

        // Then
        await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(inventoryStockStore, atLeastOnce()).cancel(any(), any())
        }
    }

    @Test
    fun `should handle CloudEvent with specversion 1_0`() {
        // Given
        val orderId = 1006L
        val orderPlacedEvent = OrderPlacedEvent(
            orderId = orderId,
            userId = 100L,
            payableAmount = 50000L,
            items = listOf(
                OrderPlacedEvent.PlacedItem(skuId = "SKU-001", quantity = 1, unitPrice = 50000L),
            ),
            correlationId = UUID.randomUUID().toString(),
            causationId = UUID.randomUUID().toString(),
        )

        val cloudEvent = CloudEvent.of(
            source = "order-service",
            type = "order.placed",
            data = orderPlacedEvent,
            subject = "order/$orderId",
        )

        // Verify the CloudEvent spec version
        assertThat(cloudEvent.specVersion).isEqualTo("1.0")

        // When
        kafkaTemplate.send(orderPlacedTopic, orderId.toString(), cloudEvent).get()

        // Then
        await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(inventoryStockStore, atLeastOnce()).reserve(any(), any())
        }
    }
}
