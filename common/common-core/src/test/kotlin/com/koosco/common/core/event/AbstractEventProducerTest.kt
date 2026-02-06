package com.koosco.common.core.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.concurrent.CopyOnWriteArrayList

class AbstractEventProducerTest {

    private lateinit var objectMapper: ObjectMapper
    private lateinit var testProducer: TestEventProducer

    @BeforeEach
    fun setUp() {
        objectMapper = jacksonObjectMapper().apply {
            registerModule(JavaTimeModule())
        }
        testProducer = TestEventProducer(objectMapper)
    }

    data class TestData(
        val value: String,
        val amount: Int,
    )

    data class TestDomainEvent(
        val testAggregateId: String,
        val testValue: String,
        override val eventId: String = CloudEvent.generateId(),
        override val occurredAt: Instant = Instant.now(),
    ) : DomainEvent {
        override fun getEventType(): String = "com.koosco.test.event"
        override fun getAggregateId(): String = testAggregateId
    }

    @Test
    fun `should publish CloudEvent with validation`() {
        // given
        val event = CloudEvent.of(
            source = "urn:koosco:test-service",
            type = "com.koosco.test.created",
            data = TestData("test", 100),
        )

        // when
        testProducer.publish(event)

        // then
        assertThat(testProducer.publishedEvents).hasSize(1)
        val published = testProducer.publishedEvents[0]
        assertThat(published.topic).isEqualTo("com-koosco-test-created")
        assertThat(published.key).isEqualTo("com.koosco.test.created")
        assertThat(published.payload).contains("\"type\":\"com.koosco.test.created\"")
    }

    @Test
    fun `should validate CloudEvent before publishing`() {
        // given - invalid CloudEvent with blank id
        val invalidEvent = CloudEvent<TestData>(
            id = " ",
            source = "urn:koosco:test-service",
            type = "com.koosco.test.created",
        )

        // when & then
        assertThatThrownBy {
            testProducer.publish(invalidEvent)
        }.isInstanceOf(ValidationException::class.java)
            .hasMessageContaining("id")

        assertThat(testProducer.publishedEvents).isEmpty()
    }

    @Test
    fun `should publish DomainEvent with validation`() {
        // given
        val domainEvent = TestDomainEvent(
            testAggregateId = "test-123",
            testValue = "domain-value",
        )

        // when
        testProducer.publishDomainEvent(
            event = domainEvent,
            source = "urn:koosco:test-service",
            dataSchema = null,
        )

        // then
        assertThat(testProducer.publishedEvents).hasSize(1)
        val published = testProducer.publishedEvents[0]
        assertThat(published.topic).isEqualTo("com-koosco-test-event")
        assertThat(published.key).isEqualTo("com.koosco.test.event")
        assertThat(published.payload).contains("\"type\":\"com.koosco.test.event\"")
        assertThat(published.payload).contains("test-123")
    }

    @Test
    fun `should validate DomainEvent before publishing`() {
        // given - invalid DomainEvent with blank aggregateId
        val invalidEvent = TestDomainEvent(
            testAggregateId = " ",
            testValue = "test",
        )

        // when & then
        assertThatThrownBy {
            testProducer.publishDomainEvent(
                event = invalidEvent,
                source = "urn:koosco:test-service",
                dataSchema = null,
            )
        }.isInstanceOf(ValidationException::class.java)
            .hasMessageContaining("aggregateId")

        assertThat(testProducer.publishedEvents).isEmpty()
    }

    @Test
    fun `should publish batch of CloudEvents with validation`() {
        // given
        val events = listOf(
            CloudEvent.of(
                source = "urn:koosco:test-service",
                type = "com.koosco.test.event1",
                data = TestData("test1", 100),
            ),
            CloudEvent.of(
                source = "urn:koosco:test-service",
                type = "com.koosco.test.event2",
                data = TestData("test2", 200),
            ),
            CloudEvent.of(
                source = "urn:koosco:test-service",
                type = "com.koosco.test.event3",
                data = TestData("test3", 300),
            ),
        )

        // when
        testProducer.publishBatch(events)

        // then
        assertThat(testProducer.publishedEvents).hasSize(3)
        assertThat(testProducer.publishedEvents[0].payload).contains("test1")
        assertThat(testProducer.publishedEvents[1].payload).contains("test2")
        assertThat(testProducer.publishedEvents[2].payload).contains("test3")
    }

    @Test
    fun `should validate all events before publishing any in batch`() {
        // given - batch with one invalid event
        val events = listOf(
            CloudEvent.of(
                source = "urn:koosco:test-service",
                type = "com.koosco.test.event1",
                data = TestData("test1", 100),
            ),
            // Invalid event with blank id
            CloudEvent<TestData>(
                id = " ",
                source = "urn:koosco:test-service",
                type = "com.koosco.test.event2",
            ),
            CloudEvent.of(
                source = "urn:koosco:test-service",
                type = "com.koosco.test.event3",
                data = TestData("test3", 300),
            ),
        )

        // when & then
        assertThatThrownBy {
            testProducer.publishBatch(events)
        }.isInstanceOf(ValidationException::class.java)

        // No events should be published if validation fails
        assertThat(testProducer.publishedEvents).isEmpty()
    }

    @Test
    fun `should handle empty batch gracefully`() {
        // given
        val events = emptyList<CloudEvent<*>>()

        // when
        testProducer.publishBatch(events)

        // then
        assertThat(testProducer.publishedEvents).isEmpty()
    }

    @Test
    fun `should use custom topic resolution`() {
        // given
        val customProducer = CustomTopicProducer(objectMapper)
        val event = CloudEvent.of(
            source = "urn:koosco:test-service",
            type = "com.koosco.order.created",
            data = TestData("test", 100),
        )

        // when
        customProducer.publish(event)

        // then
        assertThat(customProducer.publishedEvents).hasSize(1)
        assertThat(customProducer.publishedEvents[0].topic).isEqualTo("custom-topic-created")
    }

    @Test
    fun `should use custom key resolution`() {
        // given
        val customProducer = CustomKeyProducer(objectMapper)
        val event = CloudEvent.of(
            source = "urn:koosco:test-service",
            type = "com.koosco.test.created",
            data = TestData("test", 100),
            subject = "test-subject-123",
        )

        // when
        customProducer.publish(event)

        // then
        assertThat(customProducer.publishedEvents).hasSize(1)
        assertThat(customProducer.publishedEvents[0].key).isEqualTo("test-subject-123")
    }

    @Test
    fun `should handle serialization errors`() {
        // given
        val producerWithBrokenMapper = TestEventProducer(
            ObjectMapper().apply {
                // Intentionally misconfigured to cause serialization errors
            },
        )
        val event = CloudEvent.of(
            source = "urn:koosco:test-service",
            type = "com.koosco.test.created",
            data = TestData("test", 100),
        )

        // when & then
        assertThatThrownBy {
            producerWithBrokenMapper.publish(event)
        }.isInstanceOf(EventProduceException::class.java)
            .hasMessageContaining("Failed to publish CloudEvent")
    }

    @Test
    fun `should handle publish errors`() {
        // given
        val failingProducer = FailingEventProducer(objectMapper)
        val event = CloudEvent.of(
            source = "urn:koosco:test-service",
            type = "com.koosco.test.created",
            data = TestData("test", 100),
        )

        // when & then
        assertThatThrownBy {
            failingProducer.publish(event)
        }.isInstanceOf(EventProduceException::class.java)
            .hasMessageContaining("Simulated publish failure")
    }

    @Test
    fun `publishWithValidation should delegate to publish`() {
        // given
        val event = CloudEvent.of(
            source = "urn:koosco:test-service",
            type = "com.koosco.test.created",
            data = TestData("test", 100),
        )

        // when
        testProducer.publishWithValidation(event)

        // then
        assertThat(testProducer.publishedEvents).hasSize(1)
    }

    // Test implementation classes

    data class PublishedEvent(
        val topic: String,
        val key: String?,
        val payload: String,
    )

    class TestEventProducer(
        objectMapper: ObjectMapper,
    ) : AbstractEventProducer(objectMapper) {

        val publishedEvents = CopyOnWriteArrayList<PublishedEvent>()

        override fun publishRaw(topic: String, key: String?, payload: String) {
            publishedEvents.add(PublishedEvent(topic, key, payload))
        }
    }

    class CustomTopicProducer(
        objectMapper: ObjectMapper,
    ) : AbstractEventProducer(objectMapper) {

        val publishedEvents = CopyOnWriteArrayList<PublishedEvent>()

        override fun publishRaw(topic: String, key: String?, payload: String) {
            publishedEvents.add(PublishedEvent(topic, key, payload))
        }

        override fun resolveTopic(event: CloudEvent<*>): String = "custom-topic-${event.type.substringAfterLast(".")}"
    }

    class CustomKeyProducer(
        objectMapper: ObjectMapper,
    ) : AbstractEventProducer(objectMapper) {

        val publishedEvents = CopyOnWriteArrayList<PublishedEvent>()

        override fun publishRaw(topic: String, key: String?, payload: String) {
            publishedEvents.add(PublishedEvent(topic, key, payload))
        }

        override fun resolveKey(event: CloudEvent<*>): String? = event.subject
    }

    class FailingEventProducer(
        objectMapper: ObjectMapper,
    ) : AbstractEventProducer(objectMapper) {

        override fun publishRaw(topic: String, key: String?, payload: String): Unit = throw EventProduceException("Simulated publish failure")
    }
}
