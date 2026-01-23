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

class AbstractEventPublisherTest {

    private lateinit var objectMapper: ObjectMapper
    private lateinit var testPublisher: TestEventPublisher

    @BeforeEach
    fun setUp() {
        objectMapper = jacksonObjectMapper().apply {
            registerModule(JavaTimeModule())
        }
        testPublisher = TestEventPublisher(objectMapper)
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
        testPublisher.publish(event)

        // then
        assertThat(testPublisher.publishedEvents).hasSize(1)
        val published = testPublisher.publishedEvents[0]
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
            testPublisher.publish(invalidEvent)
        }.isInstanceOf(ValidationException::class.java)
            .hasMessageContaining("id")

        assertThat(testPublisher.publishedEvents).isEmpty()
    }

    @Test
    fun `should publish DomainEvent with validation`() {
        // given
        val domainEvent = TestDomainEvent(
            testAggregateId = "test-123",
            testValue = "domain-value",
        )

        // when
        testPublisher.publishDomainEvent(
            event = domainEvent,
            source = "urn:koosco:test-service",
            dataSchema = null,
        )

        // then
        assertThat(testPublisher.publishedEvents).hasSize(1)
        val published = testPublisher.publishedEvents[0]
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
            testPublisher.publishDomainEvent(
                event = invalidEvent,
                source = "urn:koosco:test-service",
                dataSchema = null,
            )
        }.isInstanceOf(ValidationException::class.java)
            .hasMessageContaining("aggregateId")

        assertThat(testPublisher.publishedEvents).isEmpty()
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
        testPublisher.publishBatch(events)

        // then
        assertThat(testPublisher.publishedEvents).hasSize(3)
        assertThat(testPublisher.publishedEvents[0].payload).contains("test1")
        assertThat(testPublisher.publishedEvents[1].payload).contains("test2")
        assertThat(testPublisher.publishedEvents[2].payload).contains("test3")
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
            testPublisher.publishBatch(events)
        }.isInstanceOf(ValidationException::class.java)

        // No events should be published if validation fails
        assertThat(testPublisher.publishedEvents).isEmpty()
    }

    @Test
    fun `should handle empty batch gracefully`() {
        // given
        val events = emptyList<CloudEvent<*>>()

        // when
        testPublisher.publishBatch(events)

        // then
        assertThat(testPublisher.publishedEvents).isEmpty()
    }

    @Test
    fun `should use custom topic resolution`() {
        // given
        val customPublisher = CustomTopicPublisher(objectMapper)
        val event = CloudEvent.of(
            source = "urn:koosco:test-service",
            type = "com.koosco.order.created",
            data = TestData("test", 100),
        )

        // when
        customPublisher.publish(event)

        // then
        assertThat(customPublisher.publishedEvents).hasSize(1)
        assertThat(customPublisher.publishedEvents[0].topic).isEqualTo("custom-topic-created")
    }

    @Test
    fun `should use custom key resolution`() {
        // given
        val customPublisher = CustomKeyPublisher(objectMapper)
        val event = CloudEvent.of(
            source = "urn:koosco:test-service",
            type = "com.koosco.test.created",
            data = TestData("test", 100),
            subject = "test-subject-123",
        )

        // when
        customPublisher.publish(event)

        // then
        assertThat(customPublisher.publishedEvents).hasSize(1)
        assertThat(customPublisher.publishedEvents[0].key).isEqualTo("test-subject-123")
    }

    @Test
    fun `should handle serialization errors`() {
        // given
        val publisherWithBrokenMapper = TestEventPublisher(
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
            publisherWithBrokenMapper.publish(event)
        }.isInstanceOf(EventPublishException::class.java)
            .hasMessageContaining("Failed to publish CloudEvent")
    }

    @Test
    fun `should handle publish errors`() {
        // given
        val failingPublisher = FailingEventPublisher(objectMapper)
        val event = CloudEvent.of(
            source = "urn:koosco:test-service",
            type = "com.koosco.test.created",
            data = TestData("test", 100),
        )

        // when & then
        assertThatThrownBy {
            failingPublisher.publish(event)
        }.isInstanceOf(EventPublishException::class.java)
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
        testPublisher.publishWithValidation(event)

        // then
        assertThat(testPublisher.publishedEvents).hasSize(1)
    }

    // Test implementation classes

    data class PublishedEvent(
        val topic: String,
        val key: String?,
        val payload: String,
    )

    class TestEventPublisher(
        objectMapper: ObjectMapper,
    ) : AbstractEventPublisher(objectMapper) {

        val publishedEvents = CopyOnWriteArrayList<PublishedEvent>()

        override fun publishRaw(topic: String, key: String?, payload: String) {
            publishedEvents.add(PublishedEvent(topic, key, payload))
        }
    }

    class CustomTopicPublisher(
        objectMapper: ObjectMapper,
    ) : AbstractEventPublisher(objectMapper) {

        val publishedEvents = CopyOnWriteArrayList<PublishedEvent>()

        override fun publishRaw(topic: String, key: String?, payload: String) {
            publishedEvents.add(PublishedEvent(topic, key, payload))
        }

        override fun resolveTopic(event: CloudEvent<*>): String = "custom-topic-${event.type.substringAfterLast(".")}"
    }

    class CustomKeyPublisher(
        objectMapper: ObjectMapper,
    ) : AbstractEventPublisher(objectMapper) {

        val publishedEvents = CopyOnWriteArrayList<PublishedEvent>()

        override fun publishRaw(topic: String, key: String?, payload: String) {
            publishedEvents.add(PublishedEvent(topic, key, payload))
        }

        override fun resolveKey(event: CloudEvent<*>): String? = event.subject
    }

    class FailingEventPublisher(
        objectMapper: ObjectMapper,
    ) : AbstractEventPublisher(objectMapper) {

        override fun publishRaw(topic: String, key: String?, payload: String): Unit = throw EventPublishException("Simulated publish failure")
    }
}
