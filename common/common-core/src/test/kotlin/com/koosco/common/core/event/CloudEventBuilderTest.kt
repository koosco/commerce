package com.koosco.common.core.event

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant

class CloudEventBuilderTest {

    data class TestData(val value: String)

    @Test
    fun `should build CloudEvent with builder pattern`() {
        // given
        val source = "urn:koosco:order-service"
        val type = "com.koosco.order.created"
        val data = TestData("test")

        // when
        val event = CloudEventBuilder.builder<TestData>()
            .source(source)
            .type(type)
            .data(data)
            .build()

        // then
        assertNotNull(event.id)
        assertEquals(source, event.source)
        assertEquals(type, event.type)
        assertEquals(data, event.data)
        assertNotNull(event.time)
    }

    @Test
    fun `should build CloudEvent with all fields`() {
        // given
        val id = "test-id"
        val source = "urn:koosco:order-service"
        val type = "com.koosco.order.created"
        val subject = "order-123"
        val time = Instant.now()
        val data = TestData("test")
        val dataSchema = "https://schemas.koosco.com/order/v1"

        // when
        val event = CloudEventBuilder.builder<TestData>()
            .id(id)
            .source(source)
            .type(type)
            .subject(subject)
            .time(time)
            .data(data)
            .dataSchema(dataSchema)
            .build()

        // then
        assertEquals(id, event.id)
        assertEquals(source, event.source)
        assertEquals(type, event.type)
        assertEquals(subject, event.subject)
        assertEquals(time, event.time)
        assertEquals(data, event.data)
        assertEquals(dataSchema, event.dataSchema)
    }

    @Test
    fun `should fail when source is not set`() {
        assertThrows<IllegalArgumentException> {
            CloudEventBuilder.builder<TestData>()
                .type("com.koosco.order.created")
                .build()
        }
    }

    @Test
    fun `should fail when type is not set`() {
        assertThrows<IllegalArgumentException> {
            CloudEventBuilder.builder<TestData>()
                .source("urn:koosco:order-service")
                .build()
        }
    }

    @Test
    fun `should build with pre-filled source and type`() {
        // given
        val source = "urn:koosco:order-service"
        val type = "com.koosco.order.created"

        // when
        val event = CloudEventBuilder.builder<TestData>(source, type)
            .data(TestData("test"))
            .build()

        // then
        assertEquals(source, event.source)
        assertEquals(type, event.type)
    }

    @Test
    fun `should build CloudEvent with DSL style`() {
        // when
        val event = cloudEvent<TestData> {
            source("urn:koosco:order-service")
            type("com.koosco.order.created")
            subject("order-123")
            data(TestData("test"))
        }

        // then
        assertEquals("urn:koosco:order-service", event.source)
        assertEquals("com.koosco.order.created", event.type)
        assertEquals("order-123", event.subject)
        assertEquals(TestData("test"), event.data)
    }
}
