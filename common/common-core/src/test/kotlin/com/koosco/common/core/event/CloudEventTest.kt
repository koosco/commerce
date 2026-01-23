package com.koosco.common.core.event

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant

class CloudEventTest {

    data class TestData(
        val orderId: String,
        val amount: Int,
    )

    @Test
    fun `should create CloudEvent with required fields`() {
        // given
        val id = "test-id"
        val source = "urn:koosco:order-service"
        val type = "com.koosco.order.created"

        // when
        val event = CloudEvent<String>(
            id = id,
            source = source,
            type = type,
        )

        // then
        assertEquals(id, event.id)
        assertEquals(source, event.source)
        assertEquals(type, event.type)
        assertEquals("1.0", event.specVersion)
        assertNull(event.data)
    }

    @Test
    fun `should create CloudEvent with all fields`() {
        // given
        val id = "test-id"
        val source = "urn:koosco:order-service"
        val type = "com.koosco.order.created"
        val subject = "order-123"
        val time = Instant.now()
        val data = TestData("order-123", 10000)
        val dataSchema = "https://schemas.koosco.com/order/v1"

        // when
        val event = CloudEvent(
            id = id,
            source = source,
            type = type,
            subject = subject,
            time = time,
            data = data,
            dataSchema = dataSchema,
        )

        // then
        assertEquals(id, event.id)
        assertEquals(source, event.source)
        assertEquals(type, event.type)
        assertEquals(subject, event.subject)
        assertEquals(time, event.time)
        assertEquals(data, event.data)
        assertEquals(dataSchema, event.dataSchema)
    }

    // Note: Validation of blank fields is handled by EventValidator, not in the constructor.
    // See EventValidatorTest for validation tests.

    @Test
    fun `should fail when specVersion is not 1_0`() {
        assertThrows<IllegalArgumentException> {
            CloudEvent<String>(
                id = "test-id",
                source = "urn:koosco:order-service",
                type = "com.koosco.order.created",
                specVersion = "2.0",
            )
        }
    }

    @Test
    fun `should create CloudEvent with factory method`() {
        // given
        val source = "urn:koosco:order-service"
        val type = "com.koosco.order.created"
        val data = TestData("order-123", 10000)

        // when
        val event = CloudEvent.of(
            source = source,
            type = type,
            data = data,
            subject = "order-123",
        )

        // then
        assertNotNull(event.id)
        assertEquals(source, event.source)
        assertEquals(type, event.type)
        assertEquals("order-123", event.subject)
        assertEquals(data, event.data)
        assertNotNull(event.time)
    }

    @Test
    fun `should generate unique IDs`() {
        // when
        val id1 = CloudEvent.generateId()
        val id2 = CloudEvent.generateId()

        // then
        assertNotEquals(id1, id2)
    }

    @Test
    fun `should create new event with different data`() {
        // given
        val originalData = TestData("order-123", 10000)
        val event = CloudEvent.of(
            source = "urn:koosco:order-service",
            type = "com.koosco.order.created",
            data = originalData,
        )

        // when
        val newData = "new-data"
        val newEvent = event.withData(newData)

        // then
        assertEquals(event.id, newEvent.id)
        assertEquals(event.source, newEvent.source)
        assertEquals(event.type, newEvent.type)
        assertEquals(newData, newEvent.data)
    }

    @Test
    fun `should create event without data`() {
        // given
        val event = CloudEvent.of(
            source = "urn:koosco:order-service",
            type = "com.koosco.order.created",
            data = TestData("order-123", 10000),
        )

        // when
        val eventWithoutData = event.withoutData()

        // then
        assertEquals(event.id, eventWithoutData.id)
        assertEquals(event.source, eventWithoutData.source)
        assertEquals(event.type, eventWithoutData.type)
        assertNull(eventWithoutData.data)
    }
}
