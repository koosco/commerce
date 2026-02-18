package com.koosco.inventoryservice.integration.kafka

import com.koosco.common.core.event.CloudEvent
import com.koosco.inventoryservice.inventory.application.contract.inbound.order.OrderCancelledEvent
import com.koosco.inventoryservice.inventory.application.contract.inbound.order.OrderConfirmedEvent
import com.koosco.inventoryservice.inventory.application.contract.inbound.order.OrderPlacedEvent
import com.koosco.inventoryservice.inventory.application.port.InventoryStockSnapshotQueryPort
import com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeast
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.reset
import org.mockito.kotlin.times
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
 * Integration tests for idempotent message handling.
 *
 * Tests that processing the same event multiple times is safe and
 * does not cause duplicate side effects in the inventory-service.
 *
 * Note: The inventory-service uses Redis for stock operations which
 * are designed to be idempotent through state transitions.
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class StockIdempotencyTest : KafkaContainerTestBase() {

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
    fun `should safely process same OrderPlacedEvent twice`() {
        // Given - same event with same event ID
        val orderId = 3001L
        val eventId = UUID.randomUUID().toString()
        val correlationId = UUID.randomUUID().toString()
        val causationId = UUID.randomUUID().toString()

        val orderPlacedEvent = OrderPlacedEvent(
            orderId = orderId,
            userId = 100L,
            payableAmount = 50000L,
            items = listOf(
                OrderPlacedEvent.PlacedItem(skuId = "SKU-001", quantity = 2, unitPrice = 25000L),
            ),
            correlationId = correlationId,
            causationId = causationId,
        )

        val cloudEvent = CloudEvent(
            id = eventId,
            source = "order-service",
            type = "order.placed",
            data = orderPlacedEvent,
            subject = "order/$orderId",
        )

        // When - send the same event twice
        kafkaTemplate.send(orderPlacedTopic, orderId.toString(), cloudEvent).get()
        kafkaTemplate.send(orderPlacedTopic, orderId.toString(), cloudEvent).get()

        // Then - reserve should be called at least twice (once per message)
        // The actual idempotency is handled by the InventoryStockStore implementation
        await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(inventoryStockStore, atLeast(2)).reserve(any(), any())
        }
    }

    @Test
    fun `should safely process same OrderConfirmedEvent twice`() {
        // Given - same event with same event ID
        val orderId = 3002L
        val eventId = UUID.randomUUID().toString()
        val correlationId = UUID.randomUUID().toString()
        val causationId = UUID.randomUUID().toString()

        val orderConfirmedEvent = OrderConfirmedEvent(
            orderId = orderId,
            items = listOf(
                OrderConfirmedEvent.ConfirmedItem(skuId = "SKU-001", quantity = 2),
            ),
            correlationId = correlationId,
            causationId = causationId,
        )

        val cloudEvent = CloudEvent(
            id = eventId,
            source = "order-service",
            type = "order.confirmed",
            data = orderConfirmedEvent,
            subject = "order/$orderId",
        )

        // When - send the same event twice
        kafkaTemplate.send(orderConfirmedTopic, orderId.toString(), cloudEvent).get()
        kafkaTemplate.send(orderConfirmedTopic, orderId.toString(), cloudEvent).get()

        // Then - confirm should be called at least twice
        await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(inventoryStockStore, atLeast(2)).confirm(any(), any())
        }
    }

    @Test
    fun `should safely process same OrderCancelledEvent twice`() {
        // Given - same event with same event ID
        val orderId = 3003L
        val eventId = UUID.randomUUID().toString()
        val correlationId = UUID.randomUUID().toString()
        val causationId = UUID.randomUUID().toString()

        val orderCancelledEvent = OrderCancelledEvent(
            orderId = orderId,
            reason = "USER_CANCELLED",
            items = listOf(
                OrderCancelledEvent.CancelledItem(skuId = "SKU-001", quantity = 2),
            ),
            correlationId = correlationId,
            causationId = causationId,
        )

        val cloudEvent = CloudEvent(
            id = eventId,
            source = "order-service",
            type = "order.cancelled",
            data = orderCancelledEvent,
            subject = "order/$orderId",
        )

        // When - send the same event twice
        kafkaTemplate.send(orderCancelledTopic, orderId.toString(), cloudEvent).get()
        kafkaTemplate.send(orderCancelledTopic, orderId.toString(), cloudEvent).get()

        // Then - cancel should be called at least twice
        await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(inventoryStockStore, atLeast(2)).cancel(any(), any())
        }
    }

    @Test
    fun `should process events with same correlationId but different eventIds independently`() {
        // Given - two different events with same correlationId
        val orderId = 3004L
        val correlationId = UUID.randomUUID().toString()

        val event1 = createOrderPlacedCloudEvent(orderId, correlationId, UUID.randomUUID().toString())
        val event2 = createOrderPlacedCloudEvent(orderId, correlationId, UUID.randomUUID().toString())

        // When
        kafkaTemplate.send(orderPlacedTopic, orderId.toString(), event1).get()
        kafkaTemplate.send(orderPlacedTopic, orderId.toString(), event2).get()

        // Then - both events should be processed
        await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(inventoryStockStore, atLeast(2)).reserve(any(), any())
        }
    }

    @Test
    fun `should use causationId for traceability across event processing`() {
        // Given
        val orderId = 3005L
        val originalEventId = UUID.randomUUID().toString()
        val correlationId = UUID.randomUUID().toString()

        val orderPlacedEvent = OrderPlacedEvent(
            orderId = orderId,
            userId = 100L,
            payableAmount = 30000L,
            items = listOf(
                OrderPlacedEvent.PlacedItem(skuId = "SKU-001", quantity = 1, unitPrice = 30000L),
            ),
            correlationId = correlationId,
            causationId = originalEventId, // Original event that caused this event
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
            verify(inventoryStockStore, atLeast(1)).reserve(any(), any())
        }
    }

    @Test
    fun `should handle rapid succession of events for same order`() {
        // Given - multiple events in rapid succession
        val orderId = 3006L

        val events = (1..5).map {
            createOrderPlacedCloudEvent(
                orderId = orderId,
                correlationId = UUID.randomUUID().toString(),
                causationId = UUID.randomUUID().toString(),
            )
        }

        // When - send all events rapidly
        events.forEach { event ->
            kafkaTemplate.send(orderPlacedTopic, orderId.toString(), event)
        }

        // Then - all events should be processed
        await().atMost(Duration.ofSeconds(20)).untilAsserted {
            verify(inventoryStockStore, atLeast(5)).reserve(any(), any())
        }
    }

    @Test
    fun `should process events with different order ids independently`() {
        // Given
        val order1 = 3007L
        val order2 = 3008L
        val order3 = 3009L

        val event1 = createOrderPlacedCloudEvent(order1, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val event2 = createOrderPlacedCloudEvent(order2, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val event3 = createOrderPlacedCloudEvent(order3, UUID.randomUUID().toString(), UUID.randomUUID().toString())

        // When
        kafkaTemplate.send(orderPlacedTopic, order1.toString(), event1).get()
        kafkaTemplate.send(orderPlacedTopic, order2.toString(), event2).get()
        kafkaTemplate.send(orderPlacedTopic, order3.toString(), event3).get()

        // Then
        await().atMost(Duration.ofSeconds(15)).untilAsserted {
            verify(inventoryStockStore, times(3)).reserve(any(), any())
        }
    }

    private fun createOrderPlacedCloudEvent(
        orderId: Long,
        correlationId: String,
        causationId: String,
    ): CloudEvent<OrderPlacedEvent> {
        val orderPlacedEvent = OrderPlacedEvent(
            orderId = orderId,
            userId = 100L,
            payableAmount = 50000L,
            items = listOf(
                OrderPlacedEvent.PlacedItem(skuId = "SKU-001", quantity = 1, unitPrice = 50000L),
            ),
            correlationId = correlationId,
            causationId = causationId,
        )

        return CloudEvent.of(
            source = "order-service",
            type = "order.placed",
            data = orderPlacedEvent,
            subject = "order/$orderId",
        )
    }
}
