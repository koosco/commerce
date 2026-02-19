package com.koosco.orderservice.integration.kafka

import com.koosco.orderservice.application.port.IntegrationEventProducer
import com.koosco.orderservice.contract.outbound.order.OrderCancelledEvent
import com.koosco.orderservice.contract.outbound.order.OrderConfirmedEvent
import com.koosco.orderservice.contract.outbound.order.OrderPlacedEvent
import com.koosco.orderservice.domain.enums.OrderCancelReason
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration
import java.util.UUID

/**
 * Integration tests for Kafka event publishing.
 * Tests that OrderPlacedEvent, OrderConfirmedEvent, and OrderCancelledEvent
 * are published with valid CloudEvent format.
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@DisplayName("KafkaOrderEventProducer Integration Tests")
class KafkaOrderEventProducerIntegrationTest : KafkaIntegrationTestBase() {

    @Autowired
    private lateinit var eventProducer: IntegrationEventProducer

    @Value("\${order.topic.mappings.order.placed}")
    private lateinit var orderPlacedTopic: String

    @Value("\${order.topic.mappings.order.confirmed}")
    private lateinit var orderConfirmedTopic: String

    @Value("\${order.topic.mappings.order.cancelled}")
    private lateinit var orderCancelledTopic: String

    @Nested
    @DisplayName("OrderPlacedEvent Publishing Tests")
    inner class OrderPlacedEventTests {

        @Test
        @DisplayName("should publish OrderPlacedEvent with valid CloudEvent format")
        fun `should publish OrderPlacedEvent with valid CloudEvent format`() {
            // Given
            val orderId = 1L
            val userId = 100L
            val event = OrderPlacedEvent(
                orderId = orderId,
                userId = userId,
                payableAmount = 50000L,
                items = listOf(
                    OrderPlacedEvent.PlacedItem(skuId = "SKU-001", quantity = 2, unitPrice = 25000L),
                ),
                correlationId = orderId.toString(),
                causationId = UUID.randomUUID().toString(),
            )

            // When
            eventProducer.publish(event)

            // Then
            await().atMost(Duration.ofSeconds(10)).untilAsserted {
                val records = consumeMessages(orderPlacedTopic, 1)
                assertThat(records).hasSize(1)

                val record = records.first()
                assertValidCloudEvent(record)
                assertEventType(record, "order.placed")

                val cloudEvent = record.value()
                assertThat(cloudEvent.subject).isEqualTo("order/$orderId")
                assertThat(record.key()).isEqualTo(orderId.toString())
            }
        }

        @Test
        @DisplayName("should include all required fields in OrderPlacedEvent data")
        fun `should include all required fields in OrderPlacedEvent data`() {
            // Given
            val orderId = 2L
            val userId = 200L
            val event = OrderPlacedEvent(
                orderId = orderId,
                userId = userId,
                payableAmount = 100000L,
                items = listOf(
                    OrderPlacedEvent.PlacedItem(skuId = "SKU-001", quantity = 1, unitPrice = 50000L),
                    OrderPlacedEvent.PlacedItem(skuId = "SKU-002", quantity = 2, unitPrice = 25000L),
                ),
                correlationId = orderId.toString(),
                causationId = UUID.randomUUID().toString(),
            )

            // When
            eventProducer.publish(event)

            // Then
            await().atMost(Duration.ofSeconds(10)).untilAsserted {
                val records = consumeMessages(orderPlacedTopic, 1)
                assertThat(records).isNotEmpty

                val cloudEvent = records.first().value()
                assertThat(cloudEvent.data).isNotNull

                @Suppress("UNCHECKED_CAST")
                val data = cloudEvent.data as Map<String, Any>
                assertThat(data["orderId"]).isEqualTo(orderId.toInt())
                assertThat(data["userId"]).isEqualTo(userId.toInt())
                assertThat(data["payableAmount"]).isEqualTo(100000)
                assertThat(data["items"]).isNotNull
            }
        }
    }

    @Nested
    @DisplayName("OrderConfirmedEvent Publishing Tests")
    inner class OrderConfirmedEventTests {

        @Test
        @DisplayName("should publish OrderConfirmedEvent with valid CloudEvent format")
        fun `should publish OrderConfirmedEvent with valid CloudEvent format`() {
            // Given
            val orderId = 10L
            val event = OrderConfirmedEvent(
                orderId = orderId,
                items = listOf(
                    OrderConfirmedEvent.ConfirmedItem(skuId = "SKU-001", quantity = 3),
                ),
                correlationId = orderId.toString(),
                causationId = UUID.randomUUID().toString(),
            )

            // When
            eventProducer.publish(event)

            // Then
            await().atMost(Duration.ofSeconds(10)).untilAsserted {
                val records = consumeMessages(orderConfirmedTopic, 1)
                assertThat(records).hasSize(1)

                val record = records.first()
                assertValidCloudEvent(record)
                assertEventType(record, "order.confirmed")

                val cloudEvent = record.value()
                assertThat(cloudEvent.subject).isEqualTo("order/$orderId")
                assertThat(record.key()).isEqualTo(orderId.toString())
            }
        }

        @Test
        @DisplayName("should include confirmed items in OrderConfirmedEvent data")
        fun `should include confirmed items in OrderConfirmedEvent data`() {
            // Given
            val orderId = 11L
            val event = OrderConfirmedEvent(
                orderId = orderId,
                items = listOf(
                    OrderConfirmedEvent.ConfirmedItem(skuId = "SKU-A", quantity = 1),
                    OrderConfirmedEvent.ConfirmedItem(skuId = "SKU-B", quantity = 2),
                ),
                correlationId = orderId.toString(),
                causationId = UUID.randomUUID().toString(),
            )

            // When
            eventProducer.publish(event)

            // Then
            await().atMost(Duration.ofSeconds(10)).untilAsserted {
                val records = consumeMessages(orderConfirmedTopic, 1)
                assertThat(records).isNotEmpty

                val cloudEvent = records.first().value()
                assertThat(cloudEvent.data).isNotNull

                @Suppress("UNCHECKED_CAST")
                val data = cloudEvent.data as Map<String, Any>
                assertThat(data["orderId"]).isEqualTo(orderId.toInt())

                @Suppress("UNCHECKED_CAST")
                val items = data["items"] as List<Map<String, Any>>
                assertThat(items).hasSize(2)
            }
        }
    }

    @Nested
    @DisplayName("OrderCancelledEvent Publishing Tests")
    inner class OrderCancelledEventTests {

        @Test
        @DisplayName("should publish OrderCancelledEvent with valid CloudEvent format")
        fun `should publish OrderCancelledEvent with valid CloudEvent format`() {
            // Given
            val orderId = 20L
            val event = OrderCancelledEvent(
                orderId = orderId,
                reason = OrderCancelReason.USER_REQUEST,
                items = listOf(
                    OrderCancelledEvent.CancelledItem(skuId = "SKU-001", quantity = 1),
                ),
                correlationId = orderId.toString(),
                causationId = UUID.randomUUID().toString(),
            )

            // When
            eventProducer.publish(event)

            // Then
            await().atMost(Duration.ofSeconds(10)).untilAsserted {
                val records = consumeMessages(orderCancelledTopic, 1)
                assertThat(records).hasSize(1)

                val record = records.first()
                assertValidCloudEvent(record)
                assertEventType(record, "order.cancelled")

                val cloudEvent = record.value()
                assertThat(cloudEvent.subject).isEqualTo("order/$orderId")
                assertThat(record.key()).isEqualTo(orderId.toString())
            }
        }

        @Test
        @DisplayName("should include cancel reason in OrderCancelledEvent data")
        fun `should include cancel reason in OrderCancelledEvent data`() {
            // Given
            val orderId = 21L
            val event = OrderCancelledEvent(
                orderId = orderId,
                reason = OrderCancelReason.PAYMENT_FAILED,
                items = listOf(
                    OrderCancelledEvent.CancelledItem(skuId = "SKU-X", quantity = 5),
                ),
                correlationId = orderId.toString(),
                causationId = UUID.randomUUID().toString(),
            )

            // When
            eventProducer.publish(event)

            // Then
            await().atMost(Duration.ofSeconds(10)).untilAsserted {
                val records = consumeMessages(orderCancelledTopic, 1)
                assertThat(records).isNotEmpty

                val cloudEvent = records.first().value()
                assertThat(cloudEvent.data).isNotNull

                @Suppress("UNCHECKED_CAST")
                val data = cloudEvent.data as Map<String, Any>
                assertThat(data["orderId"]).isEqualTo(orderId.toInt())
                assertThat(data["reason"]).isEqualTo("PAYMENT_FAILED")
            }
        }

        @Test
        @DisplayName("should publish OrderCancelledEvent with PAYMENT_TIMEOUT reason")
        fun `should publish OrderCancelledEvent with PAYMENT_TIMEOUT reason`() {
            // Given
            val orderId = 22L
            val event = OrderCancelledEvent(
                orderId = orderId,
                reason = OrderCancelReason.PAYMENT_TIMEOUT,
                items = emptyList(),
                correlationId = orderId.toString(),
                causationId = UUID.randomUUID().toString(),
            )

            // When
            eventProducer.publish(event)

            // Then
            await().atMost(Duration.ofSeconds(10)).untilAsserted {
                val records = consumeMessages(orderCancelledTopic, 1)
                assertThat(records).isNotEmpty

                @Suppress("UNCHECKED_CAST")
                val data = records.first().value().data as Map<String, Any>
                assertThat(data["reason"]).isEqualTo("PAYMENT_TIMEOUT")
            }
        }
    }
}
