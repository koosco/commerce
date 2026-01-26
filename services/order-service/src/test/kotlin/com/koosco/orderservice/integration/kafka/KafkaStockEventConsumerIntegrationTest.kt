package com.koosco.orderservice.integration.kafka

import com.koosco.common.core.event.CloudEvent
import com.koosco.orderservice.order.application.contract.inbound.inventory.StockConfirmFailedEvent
import com.koosco.orderservice.order.application.contract.inbound.inventory.StockConfirmedEvent
import com.koosco.orderservice.order.application.contract.inbound.inventory.StockReserveFailedEvent
import com.koosco.orderservice.order.application.contract.inbound.inventory.StockReservedEvent
import com.koosco.orderservice.order.application.usecase.MarkOrderConfirmedUseCase
import com.koosco.orderservice.order.application.usecase.MarkOrderPaymentPendingUseCase
import org.assertj.core.api.Assertions.assertThat
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
 * Integration tests for Kafka stock event consumers.
 * Tests that StockReservedEvent, StockReservationFailedEvent, StockConfirmedEvent, and StockConfirmFailedEvent
 * are consumed and processed correctly.
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@DisplayName("KafkaStockEventConsumer Integration Tests")
class KafkaStockEventConsumerIntegrationTest : KafkaIntegrationTestBase() {

    @Value("\${order.topic.mappings.stock.reserved}")
    private lateinit var stockReservedTopic: String

    @Value("\${order.topic.mappings.stock.reservation.failed}")
    private lateinit var stockReservationFailedTopic: String

    @Value("\${order.topic.mappings.stock.confirmed}")
    private lateinit var stockConfirmedTopic: String

    @Value("\${order.topic.mappings.stock.confirm.failed}")
    private lateinit var stockConfirmFailedTopic: String

    @MockBean
    private lateinit var markOrderPaymentPendingUseCase: MarkOrderPaymentPendingUseCase

    @MockBean
    private lateinit var markOrderConfirmedUseCase: MarkOrderConfirmedUseCase

    @Nested
    @DisplayName("StockReservedEvent Consumer Tests")
    inner class StockReservedEventConsumerTests {

        @Test
        @DisplayName("should consume StockReservedEvent and call MarkOrderPaymentPendingUseCase")
        fun `should consume StockReservedEvent and call MarkOrderPaymentPendingUseCase`() {
            // Given
            val orderId = 100L
            val correlationId = orderId.toString()

            val stockReservedEvent = StockReservedEvent(
                orderId = orderId,
                items = listOf(
                    StockReservedEvent.ReservedItem(skuId = "SKU-001", quantity = 2),
                ),
                correlationId = correlationId,
                causationId = UUID.randomUUID().toString(),
            )

            val cloudEvent = CloudEvent.of(
                source = "inventory-service",
                type = "stock.reserved",
                subject = "stock/reservation/$orderId",
                data = stockReservedEvent,
            )

            // When
            val kafkaTemplate = createTestKafkaTemplate()
            kafkaTemplate.send(stockReservedTopic, orderId.toString(), cloudEvent)

            // Then
            verify(markOrderPaymentPendingUseCase, timeout(10000)).execute(any())
        }

        @Test
        @DisplayName("should consume StockReservedEvent with multiple items")
        fun `should consume StockReservedEvent with multiple items`() {
            // Given
            val orderId = 101L

            val stockReservedEvent = StockReservedEvent(
                orderId = orderId,
                items = listOf(
                    StockReservedEvent.ReservedItem(skuId = "SKU-A", quantity = 1),
                    StockReservedEvent.ReservedItem(skuId = "SKU-B", quantity = 3),
                    StockReservedEvent.ReservedItem(skuId = "SKU-C", quantity = 5),
                ),
                correlationId = orderId.toString(),
                causationId = UUID.randomUUID().toString(),
            )

            val cloudEvent = CloudEvent.of(
                source = "inventory-service",
                type = "stock.reserved",
                subject = "stock/reservation/$orderId",
                data = stockReservedEvent,
            )

            // When
            val kafkaTemplate = createTestKafkaTemplate()
            kafkaTemplate.send(stockReservedTopic, orderId.toString(), cloudEvent)

            // Then
            verify(markOrderPaymentPendingUseCase, timeout(10000)).execute(any())
        }

        @Test
        @DisplayName("should skip processing when StockReservedEvent data is null")
        fun `should skip processing when StockReservedEvent data is null`() {
            // Given
            val cloudEvent = CloudEvent.of<StockReservedEvent?>(
                source = "inventory-service",
                type = "stock.reserved",
                subject = "stock/reservation/null",
                data = null,
            )

            // When
            val kafkaTemplate = createTestKafkaTemplate()
            kafkaTemplate.send(stockReservedTopic, "null-key", cloudEvent)

            // Then
            await().during(Duration.ofSeconds(2)).untilAsserted {
                verify(markOrderPaymentPendingUseCase, never()).execute(any())
            }
        }
    }

    @Nested
    @DisplayName("StockReservationFailedEvent Consumer Tests")
    inner class StockReservationFailedEventConsumerTests {

        @Test
        @DisplayName("should receive StockReservationFailedEvent on the correct topic")
        fun `should receive StockReservationFailedEvent on the correct topic`() {
            // Given
            val orderId = 200L

            val stockReserveFailedEvent = StockReserveFailedEvent(
                orderId = orderId,
                reason = "NOT_ENOUGH_STOCK",
                failedItems = listOf(
                    StockReserveFailedEvent.ReserveFailedItem(
                        skuId = "SKU-001",
                        quantity = 10,
                        availableQuantity = 5,
                    ),
                ),
                occurredAt = System.currentTimeMillis(),
                correlationId = orderId.toString(),
            )

            val cloudEvent = CloudEvent.of(
                source = "inventory-service",
                type = "stock.reservation.failed",
                subject = "stock/reservation/$orderId",
                data = stockReserveFailedEvent,
            )

            // When
            val kafkaTemplate = createTestKafkaTemplate()
            kafkaTemplate.send(stockReservationFailedTopic, orderId.toString(), cloudEvent)

            // Then - verify message was sent to topic
            await().atMost(Duration.ofSeconds(10)).untilAsserted {
                val records = consumeMessages(stockReservationFailedTopic, 1)
                assertThat(records).isNotEmpty

                val receivedEvent = records.first().value()
                assertThat(receivedEvent.type).isEqualTo("stock.reservation.failed")
            }
        }

        @Test
        @DisplayName("should include failure details in StockReservationFailedEvent")
        fun `should include failure details in StockReservationFailedEvent`() {
            // Given
            val orderId = 201L

            val stockReserveFailedEvent = StockReserveFailedEvent(
                orderId = orderId,
                reason = "INTERNAL_ERROR",
                failedItems = null,
                occurredAt = System.currentTimeMillis(),
                correlationId = orderId.toString(),
            )

            val cloudEvent = CloudEvent.of(
                source = "inventory-service",
                type = "stock.reservation.failed",
                subject = "stock/reservation/$orderId",
                data = stockReserveFailedEvent,
            )

            // When
            val kafkaTemplate = createTestKafkaTemplate()
            kafkaTemplate.send(stockReservationFailedTopic, orderId.toString(), cloudEvent)

            // Then
            await().atMost(Duration.ofSeconds(10)).untilAsserted {
                val records = consumeMessages(stockReservationFailedTopic, 1)
                assertThat(records).isNotEmpty

                @Suppress("UNCHECKED_CAST")
                val data = records.first().value().data as Map<String, Any>
                assertThat(data["orderId"]).isEqualTo(orderId.toInt())
                assertThat(data["reason"]).isEqualTo("INTERNAL_ERROR")
            }
        }
    }

    @Nested
    @DisplayName("StockConfirmedEvent Consumer Tests")
    inner class StockConfirmedEventConsumerTests {

        @Test
        @DisplayName("should consume StockConfirmedEvent and call MarkOrderConfirmedUseCase")
        fun `should consume StockConfirmedEvent and call MarkOrderConfirmedUseCase`() {
            // Given
            val orderId = 300L
            val reservationId = "RES-${UUID.randomUUID()}"

            val stockConfirmedEvent = StockConfirmedEvent(
                orderId = orderId,
                reservationId = reservationId,
                items = listOf(
                    StockConfirmedEvent.ConfirmedItem(skuId = "SKU-001", quantity = 2),
                ),
                correlationId = orderId.toString(),
                causationId = UUID.randomUUID().toString(),
            )

            val cloudEvent = CloudEvent.of(
                source = "inventory-service",
                type = "stock.confirmed",
                subject = "stock/confirmation/$orderId",
                data = stockConfirmedEvent,
            )

            // When
            val kafkaTemplate = createTestKafkaTemplate()
            kafkaTemplate.send(stockConfirmedTopic, orderId.toString(), cloudEvent)

            // Then
            verify(markOrderConfirmedUseCase, timeout(10000)).execute(any())
        }

        @Test
        @DisplayName("should consume StockConfirmedEvent with multiple items")
        fun `should consume StockConfirmedEvent with multiple items`() {
            // Given
            val orderId = 301L
            val reservationId = "RES-${UUID.randomUUID()}"

            val stockConfirmedEvent = StockConfirmedEvent(
                orderId = orderId,
                reservationId = reservationId,
                items = listOf(
                    StockConfirmedEvent.ConfirmedItem(skuId = "SKU-X", quantity = 1),
                    StockConfirmedEvent.ConfirmedItem(skuId = "SKU-Y", quantity = 2),
                    StockConfirmedEvent.ConfirmedItem(skuId = "SKU-Z", quantity = 3),
                ),
                correlationId = orderId.toString(),
                causationId = UUID.randomUUID().toString(),
            )

            val cloudEvent = CloudEvent.of(
                source = "inventory-service",
                type = "stock.confirmed",
                subject = "stock/confirmation/$orderId",
                data = stockConfirmedEvent,
            )

            // When
            val kafkaTemplate = createTestKafkaTemplate()
            kafkaTemplate.send(stockConfirmedTopic, orderId.toString(), cloudEvent)

            // Then
            verify(markOrderConfirmedUseCase, timeout(10000)).execute(any())
        }

        @Test
        @DisplayName("should consume StockConfirmedEvent without reservationId")
        fun `should consume StockConfirmedEvent without reservationId`() {
            // Given
            val orderId = 302L

            val stockConfirmedEvent = StockConfirmedEvent(
                orderId = orderId,
                reservationId = null,
                items = listOf(
                    StockConfirmedEvent.ConfirmedItem(skuId = "SKU-001", quantity = 5),
                ),
                correlationId = orderId.toString(),
                causationId = UUID.randomUUID().toString(),
            )

            val cloudEvent = CloudEvent.of(
                source = "inventory-service",
                type = "stock.confirmed",
                subject = "stock/confirmation/$orderId",
                data = stockConfirmedEvent,
            )

            // When
            val kafkaTemplate = createTestKafkaTemplate()
            kafkaTemplate.send(stockConfirmedTopic, orderId.toString(), cloudEvent)

            // Then
            verify(markOrderConfirmedUseCase, timeout(10000)).execute(any())
        }

        @Test
        @DisplayName("should skip processing when StockConfirmedEvent data is null")
        fun `should skip processing when StockConfirmedEvent data is null`() {
            // Given
            val cloudEvent = CloudEvent.of<StockConfirmedEvent?>(
                source = "inventory-service",
                type = "stock.confirmed",
                subject = "stock/confirmation/null",
                data = null,
            )

            // When
            val kafkaTemplate = createTestKafkaTemplate()
            kafkaTemplate.send(stockConfirmedTopic, "null-key", cloudEvent)

            // Then
            await().during(Duration.ofSeconds(2)).untilAsserted {
                verify(markOrderConfirmedUseCase, never()).execute(any())
            }
        }
    }

    @Nested
    @DisplayName("StockConfirmFailedEvent Consumer Tests")
    inner class StockConfirmFailedEventConsumerTests {

        @Test
        @DisplayName("should receive StockConfirmFailedEvent on the correct topic")
        fun `should receive StockConfirmFailedEvent on the correct topic`() {
            // Given
            val orderId = 400L
            val reservationId = "RES-${UUID.randomUUID()}"

            val stockConfirmFailedEvent = StockConfirmFailedEvent(
                orderId = orderId,
                reservationId = reservationId,
                reason = "RESERVATION_NOT_FOUND",
                correlationId = orderId.toString(),
                causationId = UUID.randomUUID().toString(),
            )

            val cloudEvent = CloudEvent.of(
                source = "inventory-service",
                type = "stock.confirm.failed",
                subject = "stock/confirmation/$orderId",
                data = stockConfirmFailedEvent,
            )

            // When
            val kafkaTemplate = createTestKafkaTemplate()
            kafkaTemplate.send(stockConfirmFailedTopic, orderId.toString(), cloudEvent)

            // Then - verify message was sent to topic
            await().atMost(Duration.ofSeconds(10)).untilAsserted {
                val records = consumeMessages(stockConfirmFailedTopic, 1)
                assertThat(records).isNotEmpty

                val receivedEvent = records.first().value()
                assertThat(receivedEvent.type).isEqualTo("stock.confirm.failed")
            }
        }

        @Test
        @DisplayName("should include failure reason in StockConfirmFailedEvent")
        fun `should include failure reason in StockConfirmFailedEvent`() {
            // Given
            val orderId = 401L

            val stockConfirmFailedEvent = StockConfirmFailedEvent(
                orderId = orderId,
                reservationId = null,
                reason = "NOT_ENOUGH_STOCK",
                correlationId = orderId.toString(),
                causationId = UUID.randomUUID().toString(),
            )

            val cloudEvent = CloudEvent.of(
                source = "inventory-service",
                type = "stock.confirm.failed",
                subject = "stock/confirmation/$orderId",
                data = stockConfirmFailedEvent,
            )

            // When
            val kafkaTemplate = createTestKafkaTemplate()
            kafkaTemplate.send(stockConfirmFailedTopic, orderId.toString(), cloudEvent)

            // Then
            await().atMost(Duration.ofSeconds(10)).untilAsserted {
                val records = consumeMessages(stockConfirmFailedTopic, 1)
                assertThat(records).isNotEmpty

                @Suppress("UNCHECKED_CAST")
                val data = records.first().value().data as Map<String, Any>
                assertThat(data["orderId"]).isEqualTo(orderId.toInt())
                assertThat(data["reason"]).isEqualTo("NOT_ENOUGH_STOCK")
            }
        }

        @Test
        @DisplayName("should include correlationId in StockConfirmFailedEvent")
        fun `should include correlationId in StockConfirmFailedEvent`() {
            // Given
            val orderId = 402L
            val correlationId = orderId.toString()

            val stockConfirmFailedEvent = StockConfirmFailedEvent(
                orderId = orderId,
                reservationId = "RES-123",
                reason = "INTERNAL_ERROR",
                correlationId = correlationId,
                causationId = UUID.randomUUID().toString(),
            )

            val cloudEvent = CloudEvent.of(
                source = "inventory-service",
                type = "stock.confirm.failed",
                subject = "stock/confirmation/$orderId",
                data = stockConfirmFailedEvent,
            )

            // When
            val kafkaTemplate = createTestKafkaTemplate()
            kafkaTemplate.send(stockConfirmFailedTopic, orderId.toString(), cloudEvent)

            // Then
            await().atMost(Duration.ofSeconds(10)).untilAsserted {
                val records = consumeMessages(stockConfirmFailedTopic, 1)
                assertThat(records).isNotEmpty

                @Suppress("UNCHECKED_CAST")
                val data = records.first().value().data as Map<String, Any>
                assertThat(data["correlationId"]).isEqualTo(correlationId)
            }
        }
    }
}
