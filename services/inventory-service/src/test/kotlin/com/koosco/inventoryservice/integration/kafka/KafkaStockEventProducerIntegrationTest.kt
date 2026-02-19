package com.koosco.inventoryservice.integration.kafka

import com.fasterxml.jackson.databind.JsonNode
import com.koosco.inventoryservice.application.port.IntegrationEventProducer
import com.koosco.inventoryservice.application.port.InventoryStockSnapshotQueryPort
import com.koosco.inventoryservice.contract.outbound.inventory.StockConfirmFailedEvent
import com.koosco.inventoryservice.contract.outbound.inventory.StockConfirmedEvent
import com.koosco.inventoryservice.contract.outbound.inventory.StockReservationFailedEvent
import com.koosco.inventoryservice.contract.outbound.inventory.StockReservedEvent
import com.koosco.inventoryservice.domain.enums.StockConfirmFailReason
import com.koosco.inventoryservice.domain.enums.StockReservationFailReason
import com.koosco.inventoryservice.infra.outbox.JpaInventoryOutboxRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.UUID

/**
 * Integration tests for Kafka stock event publishing.
 *
 * Tests that the IntegrationEventProducer correctly publishes events
 * with valid CloudEvent format to the appropriate Kafka topics.
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class KafkaStockEventProducerIntegrationTest : KafkaContainerTestBase() {

    @MockitoBean
    private lateinit var inventoryStockSnapshotQueryPort: InventoryStockSnapshotQueryPort

    @Autowired
    private lateinit var eventProducer: IntegrationEventProducer

    @Autowired
    private lateinit var outboxRepository: JpaInventoryOutboxRepository

    @Value("\${inventory.topic.mappings.stock.reserved}")
    private lateinit var stockReservedTopic: String

    @Value("\${inventory.topic.mappings.stock.reservation.failed}")
    private lateinit var stockReservationFailedTopic: String

    @Value("\${inventory.topic.mappings.stock.confirmed}")
    private lateinit var stockConfirmedTopic: String

    @Value("\${inventory.topic.mappings.stock.confirm.failed}")
    private lateinit var stockConfirmFailedTopic: String

    @BeforeEach
    fun setUp() {
        outboxRepository.deleteAll()
    }

    @Test
    fun `should publish StockReservedEvent with valid CloudEvent format`() {
        // Given
        val orderId = 12345L
        val correlationId = UUID.randomUUID().toString()
        val causationId = UUID.randomUUID().toString()
        val items = listOf(
            StockReservedEvent.Item(skuId = "SKU-001", quantity = 2),
            StockReservedEvent.Item(skuId = "SKU-002", quantity = 5),
        )
        val event = StockReservedEvent(
            orderId = orderId,
            items = items,
            correlationId = correlationId,
            causationId = causationId,
        )

        // When
        eventProducer.publish(event)

        // Then
        val data = assertOutboxCloudEvent(
            orderId = orderId,
            expectedEventType = "stock.reserved",
            expectedTopic = stockReservedTopic,
        )
        assertThat(data["items"]).isNotNull
        assertThat(data["correlationId"].asText()).isEqualTo(correlationId)
        assertThat(data["causationId"].asText()).isEqualTo(causationId)
    }

    @Test
    fun `should publish StockReservationFailedEvent with valid CloudEvent format`() {
        // Given
        val orderId = 12346L
        val correlationId = UUID.randomUUID().toString()
        val causationId = UUID.randomUUID().toString()
        val failedItems = listOf(
            StockReservationFailedEvent.FailedItem(
                skuId = "SKU-001",
                requestedQuantity = 10,
                availableQuantity = 5,
            ),
        )
        val event = StockReservationFailedEvent(
            orderId = orderId,
            reason = StockReservationFailReason.NOT_ENOUGH_STOCK,
            failedItems = failedItems,
            correlationId = correlationId,
            causationId = causationId,
        )

        // When
        eventProducer.publish(event)

        // Then
        val data = assertOutboxCloudEvent(
            orderId = orderId,
            expectedEventType = "stock.reservation.failed",
            expectedTopic = stockReservationFailedTopic,
        )
        assertThat(data["reason"].asText()).isEqualTo(StockReservationFailReason.NOT_ENOUGH_STOCK.name)
        assertThat(data["failedItems"]).isNotNull
        assertThat(data["correlationId"].asText()).isEqualTo(correlationId)
        assertThat(data["causationId"].asText()).isEqualTo(causationId)
    }

    @Test
    fun `should publish StockConfirmedEvent with valid CloudEvent format`() {
        // Given
        val orderId = 12347L
        val correlationId = UUID.randomUUID().toString()
        val causationId = UUID.randomUUID().toString()
        val items = listOf(
            StockConfirmedEvent.ConfirmedItem(skuId = "SKU-001", quantity = 2),
            StockConfirmedEvent.ConfirmedItem(skuId = "SKU-002", quantity = 3),
        )
        val event = StockConfirmedEvent(
            orderId = orderId,
            items = items,
            correlationId = correlationId,
            causationId = causationId,
        )

        // When
        eventProducer.publish(event)

        // Then
        val data = assertOutboxCloudEvent(
            orderId = orderId,
            expectedEventType = "stock.confirmed",
            expectedTopic = stockConfirmedTopic,
        )
        assertThat(data["items"]).isNotNull
        assertThat(data["correlationId"].asText()).isEqualTo(correlationId)
        assertThat(data["causationId"].asText()).isEqualTo(causationId)
    }

    @Test
    fun `should publish StockConfirmFailedEvent with valid CloudEvent format`() {
        // Given
        val orderId = 12348L
        val correlationId = UUID.randomUUID().toString()
        val causationId = UUID.randomUUID().toString()
        val event = StockConfirmFailedEvent(
            orderId = orderId,
            reason = StockConfirmFailReason.NOT_ENOUGH_RESERVED,
            correlationId = correlationId,
            causationId = causationId,
        )

        // When
        eventProducer.publish(event)

        // Then
        val data = assertOutboxCloudEvent(
            orderId = orderId,
            expectedEventType = "stock.confirm.failed",
            expectedTopic = stockConfirmFailedTopic,
        )
        assertThat(data["reason"].asText()).isEqualTo(StockConfirmFailReason.NOT_ENOUGH_RESERVED.name)
        assertThat(data["correlationId"].asText()).isEqualTo(correlationId)
        assertThat(data["causationId"].asText()).isEqualTo(causationId)
    }

    @Test
    fun `should use orderId as partition key for stock events`() {
        // Given
        val orderId = 99999L
        val event = StockReservedEvent(
            orderId = orderId,
            items = listOf(StockReservedEvent.Item("SKU-TEST", 1)),
            correlationId = UUID.randomUUID().toString(),
            causationId = UUID.randomUUID().toString(),
        )

        // When
        eventProducer.publish(event)

        // Then
        val data = assertOutboxCloudEvent(
            orderId = orderId,
            expectedEventType = "stock.reserved",
            expectedTopic = stockReservedTopic,
        )
        assertThat(data["orderId"].asLong()).isEqualTo(orderId)
    }

    @Test
    fun `should include correlation and causation ids in event data`() {
        // Given
        val orderId = 88888L
        val correlationId = "correlation-${UUID.randomUUID()}"
        val causationId = "causation-${UUID.randomUUID()}"
        val event = StockReservedEvent(
            orderId = orderId,
            items = listOf(StockReservedEvent.Item("SKU-TEST", 1)),
            correlationId = correlationId,
            causationId = causationId,
        )

        // When
        eventProducer.publish(event)

        // Then
        val data = assertOutboxCloudEvent(
            orderId = orderId,
            expectedEventType = "stock.reserved",
            expectedTopic = stockReservedTopic,
        )
        assertThat(data["correlationId"].asText()).isEqualTo(correlationId)
        assertThat(data["causationId"].asText()).isEqualTo(causationId)
    }

    private fun assertOutboxCloudEvent(orderId: Long, expectedEventType: String, expectedTopic: String): JsonNode {
        val outboxEntry = outboxRepository.findAll()
            .filter { it.aggregateId == orderId.toString() && it.eventType == expectedEventType }
            .maxByOrNull { it.id }

        assertThat(outboxEntry).isNotNull
        assertThat(outboxEntry!!.topic).isEqualTo(expectedTopic)
        assertThat(outboxEntry.partitionKey).isEqualTo(orderId.toString())

        val payload = objectMapper.readTree(outboxEntry.payload)
        assertThat(payload["id"].asText()).isNotBlank
        assertThat(payload["specversion"].asText()).isEqualTo("1.0")
        assertThat(payload["source"].asText()).isEqualTo("inventory-service")
        assertThat(payload["type"].asText()).isEqualTo(expectedEventType)
        assertThat(payload["subject"].asText()).isEqualTo("inventory/$orderId")
        assertThat(payload["data"]).isNotNull
        return payload["data"]
    }
}
