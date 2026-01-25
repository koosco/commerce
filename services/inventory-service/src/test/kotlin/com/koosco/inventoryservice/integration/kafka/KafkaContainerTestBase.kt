package com.koosco.inventoryservice.integration.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.koosco.common.core.event.CloudEvent
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.time.Duration
import java.util.Properties
import java.util.UUID

/**
 * Base class for Kafka integration tests using Testcontainers.
 *
 * Provides:
 * - Shared Kafka container (started once per test class)
 * - Shared Redis container for inventory-service tests
 * - Dynamic property configuration for Spring Kafka
 * - Test utilities for consuming and verifying messages
 */
@Testcontainers
abstract class KafkaContainerTestBase {

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    companion object {
        private const val KAFKA_IMAGE = "confluentinc/cp-kafka:7.6.0"

        @Container
        @JvmStatic
        val kafkaContainer: KafkaContainer = KafkaContainer(DockerImageName.parse(KAFKA_IMAGE))
            .withKraft()
            .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true")
            .withEnv("KAFKA_NUM_PARTITIONS", "3")

        @Container
        @JvmStatic
        val redisContainer: GenericContainer<*> = GenericContainer("redis:7.2-alpine")
            .withExposedPorts(6379)
            .withReuse(true)

        // All topics used in tests - must be created before Spring context starts
        private val ALL_TOPICS = listOf(
            // Order events (consumed by inventory-service)
            "koosco.commerce.order.placed",
            "koosco.commerce.order.confirmed",
            "koosco.commerce.order.cancelled",
            // Stock events (published by inventory-service)
            "koosco.commerce.stock.reserved",
            "koosco.commerce.stock.reservation.failed",
            "koosco.commerce.stock.confirmed",
            "koosco.commerce.stock.confirm.failed",
            "koosco.commerce.stock.cancelled",
            // Product events
            "koosco.commerce.sku.created",
        )

        @JvmStatic
        @DynamicPropertySource
        fun kafkaProperties(registry: DynamicPropertyRegistry) {
            // Create topics before Spring context starts
            createTopicsStatic(ALL_TOPICS)

            // Override all bootstrap-servers settings (general, producer, consumer)
            registry.add("spring.kafka.bootstrap-servers") { kafkaContainer.bootstrapServers }
            registry.add("spring.kafka.producer.bootstrap-servers") { kafkaContainer.bootstrapServers }
            registry.add("spring.kafka.consumer.bootstrap-servers") { kafkaContainer.bootstrapServers }
            registry.add("spring.kafka.consumer.auto-offset-reset") { "earliest" }
            registry.add("spring.kafka.consumer.group-id") { "test-group-${UUID.randomUUID()}" }
            // Redis properties
            registry.add("spring.data.redis.host") { redisContainer.host }
            registry.add("spring.data.redis.port") { redisContainer.getMappedPort(6379).toString() }
        }

        private fun createTopicsStatic(topics: List<String>) {
            val props = Properties().apply {
                put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.bootstrapServers)
            }
            AdminClient.create(props).use { admin ->
                val existingTopics = admin.listTopics().names().get()
                val newTopics = topics
                    .filter { it !in existingTopics }
                    .map { NewTopic(it, 3, 1.toShort()) }
                if (newTopics.isNotEmpty()) {
                    admin.createTopics(newTopics).all().get()
                }
            }
        }
    }

    /**
     * Create a test KafkaConsumer for the given topic.
     */
    protected fun createTestConsumer(
        topic: String,
        groupId: String = "test-consumer-${UUID.randomUUID()}",
    ): KafkaConsumer<String, CloudEvent<*>> {
        val props = Properties().apply {
            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.bootstrapServers)
            put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer::class.java)
            put(JsonDeserializer.TRUSTED_PACKAGES, "*")
            put(JsonDeserializer.VALUE_DEFAULT_TYPE, CloudEvent::class.java.name)
        }

        return KafkaConsumer<String, CloudEvent<*>>(props).apply {
            subscribe(listOf(topic))
        }
    }

    /**
     * Consume messages from a topic with timeout.
     * @param startTimestamp If provided, only messages with timestamp >= this value are returned.
     *                       Use System.currentTimeMillis() before publishing to filter out old messages.
     */
    protected fun consumeMessages(
        topic: String,
        expectedCount: Int,
        timeout: Duration = Duration.ofSeconds(10),
        startTimestamp: Long = 0L,
    ): List<ConsumerRecord<String, CloudEvent<*>>> {
        val consumer = createTestConsumer(topic)
        val records = mutableListOf<ConsumerRecord<String, CloudEvent<*>>>()
        val deadline = System.currentTimeMillis() + timeout.toMillis()

        try {
            while (records.size < expectedCount && System.currentTimeMillis() < deadline) {
                val polled = consumer.poll(Duration.ofMillis(500))
                // Filter by timestamp if startTimestamp is specified
                val filtered = if (startTimestamp > 0) {
                    polled.filter { it.timestamp() >= startTimestamp }
                } else {
                    polled.toList()
                }
                records.addAll(filtered)
            }
        } finally {
            consumer.close()
        }

        return records
    }

    /**
     * Consume raw string messages from a topic.
     */
    protected fun consumeRawMessages(
        topic: String,
        expectedCount: Int,
        timeout: Duration = Duration.ofSeconds(10),
    ): List<ConsumerRecord<String, String>> {
        val props = Properties().apply {
            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.bootstrapServers)
            put(ConsumerConfig.GROUP_ID_CONFIG, "raw-consumer-${UUID.randomUUID()}")
            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
        }

        val consumer = KafkaConsumer<String, String>(props).apply {
            subscribe(listOf(topic))
        }

        val records = mutableListOf<ConsumerRecord<String, String>>()
        val deadline = System.currentTimeMillis() + timeout.toMillis()

        try {
            while (records.size < expectedCount && System.currentTimeMillis() < deadline) {
                val polled = consumer.poll(Duration.ofMillis(500))
                records.addAll(polled)
            }
        } finally {
            consumer.close()
        }

        return records
    }

    /**
     * Create a test KafkaTemplate for sending messages.
     */
    protected fun createTestKafkaTemplate(): KafkaTemplate<String, CloudEvent<*>> {
        val props = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaContainer.bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
        )
        val factory = DefaultKafkaProducerFactory<String, CloudEvent<*>>(props)
        return KafkaTemplate(factory)
    }

    /**
     * Wait for a topic to be created.
     */
    protected fun waitForTopic(topic: String, timeout: Duration = Duration.ofSeconds(30)) {
        val deadline = System.currentTimeMillis() + timeout.toMillis()
        while (System.currentTimeMillis() < deadline) {
            try {
                val consumer = createTestConsumer(topic)
                consumer.close()
                return
            } catch (_: Exception) {
                Thread.sleep(100)
            }
        }
        throw IllegalStateException("Topic $topic was not created within timeout")
    }

    /**
     * Create Kafka topics using AdminClient.
     * Only creates topics that don't already exist.
     * Must be called before publishing events in tests.
     */
    protected fun createTopics(vararg topics: String) {
        val props = Properties().apply {
            put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.bootstrapServers)
        }
        AdminClient.create(props).use { admin ->
            val existingTopics = admin.listTopics().names().get()
            val newTopics = topics
                .filter { it !in existingTopics }
                .map { NewTopic(it, 3, 1.toShort()) }
            if (newTopics.isNotEmpty()) {
                admin.createTopics(newTopics).all().get()
            }
        }
    }

    /**
     * Assert that a CloudEvent has the expected type.
     */
    protected fun assertEventType(record: ConsumerRecord<String, CloudEvent<*>>, expectedType: String) {
        val event = record.value()
        assert(event.type == expectedType) {
            "Expected event type '$expectedType' but got '${event.type}'"
        }
    }

    /**
     * Assert that a CloudEvent has valid CloudEvents v1.0 format.
     */
    protected fun assertValidCloudEvent(record: ConsumerRecord<String, CloudEvent<*>>) {
        val event = record.value()
        assert(event.id.isNotBlank()) { "CloudEvent id must not be blank" }
        assert(event.source.isNotBlank()) { "CloudEvent source must not be blank" }
        assert(event.type.isNotBlank()) { "CloudEvent type must not be blank" }
        assert(event.specVersion == "1.0") { "CloudEvent specVersion must be '1.0'" }
    }
}
