package com.koosco.catalogservice.integration.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.koosco.catalogservice.product.application.contract.outbound.ProductSkuCreatedEvent
import com.koosco.catalogservice.product.application.port.IntegrationEventPublisher
import com.koosco.common.core.event.CloudEvent
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.time.Duration
import java.time.LocalDateTime
import java.util.Properties
import java.util.UUID

/**
 * Integration tests for Kafka event publishing in catalog-service.
 *
 * Tests verify that:
 * 1. ProductSkuCreatedEvent is published with valid CloudEvent format
 * 2. Events are routed to the correct topic with skuId as partition key
 * 3. Event payload contains expected data
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class KafkaProductSkuEventPublisherIntegrationTest {

    companion object {
        @Container
        @JvmStatic
        val kafkaContainer: KafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.0"))
            .withKraft()
            .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true")

        @JvmStatic
        @DynamicPropertySource
        fun kafkaProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.kafka.bootstrap-servers") { kafkaContainer.bootstrapServers }
            registry.add("spring.kafka.producer.bootstrap-servers") { kafkaContainer.bootstrapServers }
        }
    }

    @Autowired
    private lateinit var eventPublisher: IntegrationEventPublisher

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, CloudEvent<*>>

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Value("\${catalog.topic.mappings.product.sku.created}")
    private lateinit var productSkuCreatedTopic: String

    private fun createTestConsumer(): KafkaConsumer<String, String> {
        val props = Properties().apply {
            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.bootstrapServers)
            put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-${UUID.randomUUID()}")
            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        }
        return KafkaConsumer(props)
    }

    @Test
    fun `should publish ProductSkuCreatedEvent with valid CloudEvent format`() {
        // Given
        val skuId = UUID.randomUUID().toString()
        val productId = 1L
        val productCode = "PROD-001"
        val price = 10000L
        val optionValues = "color:red,size:M"
        val initialQuantity = 100

        val event = ProductSkuCreatedEvent(
            skuId = skuId,
            productId = productId,
            productCode = productCode,
            price = price,
            optionValues = optionValues,
            initialQuantity = initialQuantity,
            createdAt = LocalDateTime.now(),
        )

        // When - publish and wait for completion
        eventPublisher.publish(event)

        // Flush the producer to ensure message is sent
        kafkaTemplate.flush()

        // Then - consume and verify
        val consumer = createTestConsumer()
        consumer.subscribe(listOf(productSkuCreatedTopic))

        val records = mutableListOf<Pair<String?, String>>()
        val deadline = System.currentTimeMillis() + 15_000

        while (System.currentTimeMillis() < deadline) {
            val polled = consumer.poll(Duration.ofMillis(500))
            for (record in polled) {
                records.add(record.key() to record.value())
                // Stop if we found our event
                if (record.key() == skuId) break
            }
            if (records.any { it.first == skuId }) break
        }
        consumer.close()

        // Find our event
        val targetRecord = records.find { it.first == skuId }
        assertThat(targetRecord)
            .withFailMessage("Event with key=$skuId not found. Available keys: ${records.map { it.first }}")
            .isNotNull

        // Verify partition key
        assertThat(targetRecord!!.first).isEqualTo(skuId)

        // Parse CloudEvent
        val cloudEvent = objectMapper.readTree(targetRecord.second)

        // Verify CloudEvent structure (v1.0 spec)
        assertThat(cloudEvent.has("id")).isTrue()
        assertThat(cloudEvent.has("source")).isTrue()
        assertThat(cloudEvent.has("specversion")).isTrue()
        assertThat(cloudEvent.has("type")).isTrue()
        assertThat(cloudEvent.has("data")).isTrue()

        // Verify CloudEvent values
        assertThat(cloudEvent.get("specversion").asText()).isEqualTo("1.0")
        assertThat(cloudEvent.get("type").asText()).isEqualTo("product.sku.created")
        assertThat(cloudEvent.get("source").asText()).isEqualTo("catalog-service")
        assertThat(cloudEvent.get("id").asText()).isNotBlank()
        assertThat(cloudEvent.get("subject").asText()).isEqualTo("sku/$skuId")

        // Verify optional attributes
        assertThat(cloudEvent.has("time")).isTrue()

        // Verify event data
        val data = cloudEvent.get("data")
        assertThat(data.get("skuId").asText()).isEqualTo(skuId)
        assertThat(data.get("productId").asLong()).isEqualTo(productId)
        assertThat(data.get("productCode").asText()).isEqualTo(productCode)
        assertThat(data.get("price").asLong()).isEqualTo(price)
        assertThat(data.get("optionValues").asText()).isEqualTo(optionValues)
        assertThat(data.get("initialQuantity").asInt()).isEqualTo(initialQuantity)
    }

    @Test
    fun `should use skuId as partition key for all published events`() {
        // Given - multiple events
        val events = (1..3).map { index ->
            ProductSkuCreatedEvent(
                skuId = UUID.randomUUID().toString(),
                productId = (100 + index).toLong(),
                productCode = "PROD-BATCH-$index",
                price = (10000 * index).toLong(),
                optionValues = "batch:$index",
                initialQuantity = index * 10,
                createdAt = LocalDateTime.now(),
            )
        }
        val expectedKeys = events.map { it.skuId }.toSet()

        // When
        events.forEach { eventPublisher.publish(it) }
        kafkaTemplate.flush()

        // Then
        val consumer = createTestConsumer()
        consumer.subscribe(listOf(productSkuCreatedTopic))

        val foundKeys = mutableSetOf<String>()
        val deadline = System.currentTimeMillis() + 20_000

        while (foundKeys.size < 3 && System.currentTimeMillis() < deadline) {
            val polled = consumer.poll(Duration.ofMillis(500))
            for (record in polled) {
                if (record.key() in expectedKeys) {
                    foundKeys.add(record.key())
                }
            }
        }
        consumer.close()

        assertThat(foundKeys.toSet())
            .withFailMessage("Expected keys $expectedKeys, found $foundKeys")
            .isEqualTo(expectedKeys)
    }
}
