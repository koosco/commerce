package com.koosco.common.core.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.koosco.common.core.event.CloudEvent
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
import org.testcontainers.containers.MariaDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.time.Duration
import java.util.Properties
import java.util.UUID

/**
 * Unified base class for full integration tests combining MariaDB + Redis + Kafka containers.
 *
 * Provides:
 * - Shared MariaDB container with JPA auto-DDL
 * - Shared Redis container
 * - Shared Kafka container with test utilities
 * - Dynamic property configuration for all three
 *
 * Usage:
 * ```kotlin
 * @SpringBootTest
 * @ActiveProfiles("test")
 * class MyFullIntegrationTest : IntegrationTestBase() {
 *     @Test
 *     fun `should process order end-to-end`() {
 *         // test with real MariaDB + Redis + Kafka
 *     }
 * }
 * ```
 */
@Testcontainers
abstract class IntegrationTestBase {

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    companion object {
        private const val MARIADB_IMAGE = "mariadb:10.11"
        private const val REDIS_IMAGE = "redis:7.2-alpine"
        private const val KAFKA_IMAGE = "confluentinc/cp-kafka:7.6.0"

        @Container
        @JvmStatic
        val mariaDBContainer: MariaDBContainer<*> = MariaDBContainer(MARIADB_IMAGE)
            .withDatabaseName("commerce-test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true)

        @Container
        @JvmStatic
        val redisContainer: GenericContainer<*> = GenericContainer(REDIS_IMAGE)
            .withExposedPorts(6379)
            .withReuse(true)

        @Container
        @JvmStatic
        val kafkaContainer: KafkaContainer = KafkaContainer(DockerImageName.parse(KAFKA_IMAGE))
            .withKraft()
            .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true")
            .withEnv("KAFKA_NUM_PARTITIONS", "3")
            .withReuse(true)

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            // MariaDB
            registry.add("spring.datasource.url") { mariaDBContainer.jdbcUrl }
            registry.add("spring.datasource.username") { mariaDBContainer.username }
            registry.add("spring.datasource.password") { mariaDBContainer.password }
            registry.add("spring.datasource.driver-class-name") { "org.mariadb.jdbc.Driver" }
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
            registry.add("spring.jpa.properties.hibernate.dialect") {
                "org.hibernate.dialect.MariaDBDialect"
            }

            // Redis
            registry.add("spring.data.redis.host") { redisContainer.host }
            registry.add("spring.data.redis.port") { redisContainer.getMappedPort(6379).toString() }

            // Kafka (only set bootstrap-servers and minimal config;
            // serializer/deserializer config is handled by each service's KafkaConfig or application.yaml)
            registry.add("spring.kafka.bootstrap-servers") { kafkaContainer.bootstrapServers }
            registry.add("spring.kafka.producer.bootstrap-servers") { kafkaContainer.bootstrapServers }
            registry.add("spring.kafka.consumer.bootstrap-servers") { kafkaContainer.bootstrapServers }
            registry.add("spring.kafka.consumer.auto-offset-reset") { "earliest" }
            registry.add("spring.kafka.consumer.group-id") { "test-group-${UUID.randomUUID()}" }
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
     */
    protected fun consumeMessages(
        topic: String,
        expectedCount: Int,
        timeout: Duration = Duration.ofSeconds(10),
    ): List<ConsumerRecord<String, CloudEvent<*>>> {
        val consumer = createTestConsumer(topic)
        val records = mutableListOf<ConsumerRecord<String, CloudEvent<*>>>()
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
}
