package com.koosco.common.core.event

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant

class EventValidatorTest {

    data class TestData(val value: String)

    @Test
    fun `should validate valid CloudEvent`() {
        // given
        val event = CloudEvent(
            id = "test-id",
            source = "urn:koosco:order-service",
            type = "com.koosco.order.created",
            data = TestData("test"),
        )

        // when
        val result = EventValidator.validate(event)

        // then
        assertTrue(result.isValid)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun `should fail validation when id is blank`() {
        // given
        val event = CloudEvent<TestData>(
            id = " ",
            source = "urn:koosco:order-service",
            type = "com.koosco.order.created",
        )

        // when
        val result = EventValidator.validate(event)

        // then
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("id") })
    }

    @Test
    fun `should fail validation when source is blank`() {
        // given
        val event = CloudEvent<TestData>(
            id = "test-id",
            source = " ",
            type = "com.koosco.order.created",
        )

        // when
        val result = EventValidator.validate(event)

        // then
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("source") })
    }

    @Test
    fun `should fail validation when source is invalid URI`() {
        // given
        val event = CloudEvent<TestData>(
            id = "test-id",
            source = "invalid source",
            type = "com.koosco.order.created",
        )

        // when
        val result = EventValidator.validate(event)

        // then
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("URI") })
    }

    @Test
    fun `should validate various valid URI formats`() {
        // given
        val validSources = listOf(
            "urn:koosco:order-service",
            "http://api.koosco.com/orders",
            "https://api.koosco.com/orders",
            "custom:scheme:value",
        )

        validSources.forEach { source ->
            val event = CloudEvent<TestData>(
                id = "test-id",
                source = source,
                type = "com.koosco.order.created",
            )

            // when
            val result = EventValidator.validate(event)

            // then
            assertTrue(result.isValid, "Source '$source' should be valid")
        }
    }

    @Test
    fun `should fail validation when type is blank`() {
        // given
        val event = CloudEvent<TestData>(
            id = "test-id",
            source = "urn:koosco:order-service",
            type = " ",
        )

        // when
        val result = EventValidator.validate(event)

        // then
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("type") })
    }

    @Test
    fun `should validate specVersion is 1_0`() {
        // This test validates the CloudEvent constructor validation
        // Since the constructor throws, we test indirectly through validator
        val event = CloudEvent(
            id = "test-id",
            source = "urn:koosco:order-service",
            type = "com.koosco.order.created",
            data = TestData("test"),
        )

        // when
        val result = EventValidator.validate(event)

        // then - should be valid since constructor enforces 1.0
        assertTrue(result.isValid)
    }

    @Test
    fun `should fail validation when dataSchema is invalid URI`() {
        // given
        val event = CloudEvent<TestData>(
            id = "test-id",
            source = "urn:koosco:order-service",
            type = "com.koosco.order.created",
            dataSchema = "invalid schema",
        )

        // when
        val result = EventValidator.validate(event)

        // then
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("dataschema") })
    }

    @Test
    fun `should fail validation when dataContentType is invalid`() {
        // given
        val event = CloudEvent<TestData>(
            id = "test-id",
            source = "urn:koosco:order-service",
            type = "com.koosco.order.created",
            dataContentType = "invalid content type",
        )

        // when
        val result = EventValidator.validate(event)

        // then
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("datacontenttype") })
    }

    @Test
    fun `should validate various valid content types`() {
        // given
        val validContentTypes = listOf(
            "application/json",
            "application/xml",
            "text/plain",
            "application/cloudevents+json",
        )

        validContentTypes.forEach { contentType ->
            val event = CloudEvent<TestData>(
                id = "test-id",
                source = "urn:koosco:order-service",
                type = "com.koosco.order.created",
                dataContentType = contentType,
            )

            // when
            val result = EventValidator.validate(event)

            // then
            assertTrue(result.isValid, "Content type '$contentType' should be valid")
        }
    }

    @Test
    fun `should validate domain event`() {
        // given
        class TestDomainEvent : AbstractDomainEvent() {
            override fun getEventType(): String = "test.event"
            override fun getAggregateId(): String = "test-id"
        }
        val event = TestDomainEvent()

        // when
        val result = EventValidator.validate(event)

        // then
        assertTrue(result.isValid)
    }

    @Test
    fun `should fail domain event validation when eventId is blank`() {
        // given
        val event = object : DomainEvent {
            override val eventId: String = " "
            override val occurredAt: Instant = Instant.now()
            override fun getEventType(): String = "test.event"
            override fun getAggregateId(): String = "test-id"
        }

        // when
        val result = EventValidator.validate(event)

        // then
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("eventId") })
    }

    @Test
    fun `should throw exception when validation fails with throwIfInvalid`() {
        // given
        val event = CloudEvent<TestData>(
            id = " ",
            source = " ",
            type = " ",
        )
        val result = EventValidator.validate(event)

        // when & then
        assertThrows(ValidationException::class.java) {
            result.throwIfInvalid()
        }
    }

    @Test
    fun `should not throw exception when validation succeeds with throwIfInvalid`() {
        // given
        val event = CloudEvent<TestData>(
            id = "test-id",
            source = "urn:koosco:order-service",
            type = "com.koosco.order.created",
        )
        val result = EventValidator.validate(event)

        // when & then
        assertDoesNotThrow {
            result.throwIfInvalid()
        }
    }
}
