package com.koosco.inventoryservice.integration.kafka

import com.koosco.inventoryservice.inventory.application.contract.outbound.inventory.StockConfirmFailedEvent
import com.koosco.inventoryservice.inventory.application.contract.outbound.inventory.StockConfirmedEvent
import com.koosco.inventoryservice.inventory.application.contract.outbound.inventory.StockReservationFailedEvent
import com.koosco.inventoryservice.inventory.application.contract.outbound.inventory.StockReservedEvent
import com.koosco.inventoryservice.inventory.application.port.IntegrationEventPublisher
import com.koosco.inventoryservice.inventory.application.port.InventoryStockSnapshotQueryPort
import com.koosco.inventoryservice.inventory.domain.enums.StockConfirmFailReason
import com.koosco.inventoryservice.inventory.domain.enums.StockReservationFailReason
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
import java.time.Duration
import java.util.UUID

/**
 * Integration tests for Kafka stock event publishing.
 *
 * Tests that the IntegrationEventPublisher correctly publishes events
 * with valid CloudEvent format to the appropriate Kafka topics.
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class KafkaStockEventPublisherIntegrationTest : KafkaContainerTestBase() {

    @MockitoBean
    private lateinit var inventoryStockSnapshotQueryPort: InventoryStockSnapshotQueryPort

    @Autowired
    private lateinit var eventPublisher: IntegrationEventPublisher

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
        createTopics(
            stockReservedTopic,
            stockReservationFailedTopic,
            stockConfirmedTopic,
            stockConfirmFailedTopic,
        )
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
        val startTimestamp = System.currentTimeMillis()
        eventPublisher.publish(event)

        // Then
        val records = consumeMessages(stockReservedTopic, 1, Duration.ofSeconds(15), startTimestamp)

        assertThat(records).hasSize(1)

        val record = records.first()
        assertValidCloudEvent(record)
        assertEventType(record, "stock.reserved")

        val cloudEvent = record.value()
        assertThat(cloudEvent.source).isEqualTo("inventory-service")
        assertThat(cloudEvent.subject).isEqualTo("inventory/$orderId")
        assertThat(cloudEvent.data).isNotNull
        assertThat(record.key()).isEqualTo(orderId.toString())
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
        val startTimestamp = System.currentTimeMillis()
        eventPublisher.publish(event)

        // Then
        val records = consumeMessages(stockReservationFailedTopic, 1, Duration.ofSeconds(15), startTimestamp)

        assertThat(records).hasSize(1)

        val record = records.first()
        assertValidCloudEvent(record)
        assertEventType(record, "stock.reservation.failed")

        val cloudEvent = record.value()
        assertThat(cloudEvent.source).isEqualTo("inventory-service")
        assertThat(cloudEvent.subject).isEqualTo("inventory/$orderId")
        assertThat(cloudEvent.data).isNotNull
        assertThat(record.key()).isEqualTo(orderId.toString())
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
        val startTimestamp = System.currentTimeMillis()
        eventPublisher.publish(event)

        // Then
        val records = consumeMessages(stockConfirmedTopic, 1, Duration.ofSeconds(15), startTimestamp)

        assertThat(records).hasSize(1)

        val record = records.first()
        assertValidCloudEvent(record)
        assertEventType(record, "stock.confirmed")

        val cloudEvent = record.value()
        assertThat(cloudEvent.source).isEqualTo("inventory-service")
        assertThat(cloudEvent.subject).isEqualTo("inventory/$orderId")
        assertThat(cloudEvent.data).isNotNull
        assertThat(record.key()).isEqualTo(orderId.toString())
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
        val startTimestamp = System.currentTimeMillis()
        eventPublisher.publish(event)

        // Then
        val records = consumeMessages(stockConfirmFailedTopic, 1, Duration.ofSeconds(15), startTimestamp)

        assertThat(records).hasSize(1)

        val record = records.first()
        assertValidCloudEvent(record)
        assertEventType(record, "stock.confirm.failed")

        val cloudEvent = record.value()
        assertThat(cloudEvent.source).isEqualTo("inventory-service")
        assertThat(cloudEvent.subject).isEqualTo("inventory/$orderId")
        assertThat(cloudEvent.data).isNotNull
        assertThat(record.key()).isEqualTo(orderId.toString())
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
        val startTimestamp = System.currentTimeMillis()
        eventPublisher.publish(event)

        // Then
        val records = consumeMessages(stockReservedTopic, 1, Duration.ofSeconds(15), startTimestamp)

        assertThat(records).hasSize(1)
        assertThat(records.first().key()).isEqualTo(orderId.toString())
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
        val startTimestamp = System.currentTimeMillis()
        eventPublisher.publish(event)

        // Then
        val records = consumeMessages(stockReservedTopic, 1, Duration.ofSeconds(15), startTimestamp)

        assertThat(records).hasSize(1)

        val cloudEvent = records.first().value()
        assertThat(cloudEvent.data).isNotNull

        // Verify the data contains correlationId and causationId
        val dataMap = objectMapper.convertValue(cloudEvent.data, Map::class.java)
        assertThat(dataMap["correlationId"]).isEqualTo(correlationId)
        assertThat(dataMap["causationId"]).isEqualTo(causationId)
    }
}
