package com.koosco.common.core.event

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant

class DomainEventTest {

    data class OrderCreatedEvent(
        val orderId: String,
        val userId: String,
        val totalAmount: BigDecimal,
    ) : AbstractDomainEvent() {
        override fun getEventType(): String = "com.koosco.order.created"
        override fun getAggregateId(): String = orderId
    }

    data class PaymentCompletedEvent(
        val paymentId: String,
        val orderId: String,
        val amount: BigDecimal,
        override val eventId: String = CloudEvent.generateId(),
        override val occurredAt: Instant = Instant.now(),
    ) : DomainEvent {
        override fun getEventType(): String = "com.koosco.payment.completed"
        override fun getAggregateId(): String = paymentId
    }

    @Test
    fun `should create domain event with AbstractDomainEvent`() {
        // given & when
        val event = OrderCreatedEvent(
            orderId = "order-123",
            userId = "user-456",
            totalAmount = BigDecimal("10000"),
        )

        // then
        assertNotNull(event.eventId)
        assertNotNull(event.occurredAt)
        assertEquals("com.koosco.order.created", event.getEventType())
        assertEquals("order-123", event.getAggregateId())
        assertEquals("1.0", event.getEventVersion())
    }

    @Test
    fun `should create domain event with DomainEvent interface`() {
        // given
        val eventId = "test-event-id"
        val occurredAt = Instant.now()

        // when
        val event = PaymentCompletedEvent(
            paymentId = "payment-123",
            orderId = "order-456",
            amount = BigDecimal("10000"),
            eventId = eventId,
            occurredAt = occurredAt,
        )

        // then
        assertEquals(eventId, event.eventId)
        assertEquals(occurredAt, event.occurredAt)
        assertEquals("com.koosco.payment.completed", event.getEventType())
        assertEquals("payment-123", event.getAggregateId())
    }

    @Test
    fun `should convert domain event to CloudEvent`() {
        // given
        val event = OrderCreatedEvent(
            orderId = "order-123",
            userId = "user-456",
            totalAmount = BigDecimal("10000"),
        )
        val source = "urn:koosco:order-service"

        // when
        val cloudEvent = event.toCloudEvent(source)

        // then
        assertEquals(event.eventId, cloudEvent.id)
        assertEquals(source, cloudEvent.source)
        assertEquals(event.getEventType(), cloudEvent.type)
        assertEquals(event.getAggregateId(), cloudEvent.subject)
        assertEquals(event.occurredAt, cloudEvent.time)
        assertEquals(event, cloudEvent.data)
    }

    @Test
    fun `should convert domain event to CloudEvent with dataSchema`() {
        // given
        val event = OrderCreatedEvent(
            orderId = "order-123",
            userId = "user-456",
            totalAmount = BigDecimal("10000"),
        )
        val source = "urn:koosco:order-service"
        val dataSchema = "https://schemas.koosco.com/order/v1"

        // when
        val cloudEvent = event.toCloudEvent(source, dataSchema)

        // then
        assertEquals(dataSchema, cloudEvent.dataSchema)
    }

    @Test
    fun `should convert domain event to CloudEvent with extension function`() {
        // given
        val event = OrderCreatedEvent(
            orderId = "order-123",
            userId = "user-456",
            totalAmount = BigDecimal("10000"),
        )

        // when
        val cloudEvent = event.toCloudEventWithPrefix("koosco")

        // then
        assertEquals("urn:koosco", cloudEvent.source)
        assertEquals(event.getEventType(), cloudEvent.type)
    }

    @Test
    fun `should handle URN prefix in extension function`() {
        // given
        val event = OrderCreatedEvent(
            orderId = "order-123",
            userId = "user-456",
            totalAmount = BigDecimal("10000"),
        )

        // when
        val cloudEvent1 = event.toCloudEventWithPrefix("urn:koosco:order-service")
        val cloudEvent2 = event.toCloudEventWithPrefix("https://api.koosco.com")

        // then
        assertEquals("urn:koosco:order-service", cloudEvent1.source)
        assertEquals("https://api.koosco.com", cloudEvent2.source)
    }

    @Test
    fun `should support PublishableDomainEvent marker interface`() {
        // given
        class PublishableEvent :
            AbstractDomainEvent(),
            PublishableDomainEvent {
            override fun getEventType(): String = "test.event"
            override fun getAggregateId(): String = "test-id"
        }

        // when
        val event = PublishableEvent()

        // then
        assertTrue(event is PublishableDomainEvent)
        assertTrue(event is DomainEvent)
    }
}
