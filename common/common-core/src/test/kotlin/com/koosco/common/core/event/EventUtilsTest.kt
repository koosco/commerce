package com.koosco.common.core.event

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class EventUtilsTest {

    data class OrderData(
        val orderId: String,
        val amount: Int,
    )

    @Test
    fun `should serialize CloudEvent to JSON`() {
        // given
        val event = CloudEvent.of(
            source = "urn:koosco:order-service",
            type = "com.koosco.order.created",
            data = OrderData("order-123", 10000),
            subject = "order-123",
        )

        // when
        val json = EventUtils.toJson(event)

        // then
        assertThat(json).isNotNull()
        assertThat(json).contains("\"id\"")
        assertThat(json).contains("\"source\"")
        assertThat(json).contains("\"type\"")
        assertThat(json).contains("order-123")
    }

    @Test
    fun `should deserialize JSON to CloudEvent`() {
        // given
        val originalEvent = CloudEvent.of(
            source = "urn:koosco:order-service",
            type = "com.koosco.order.created",
            data = OrderData("order-123", 10000),
        )
        val json = EventUtils.toJson(originalEvent)

        // when
        val deserializedEvent = EventUtils.fromJson<OrderData>(json)

        // then
        assertThat(deserializedEvent.id).isEqualTo(originalEvent.id)
        assertThat(deserializedEvent.source).isEqualTo(originalEvent.source)
        assertThat(deserializedEvent.type).isEqualTo(originalEvent.type)
    }

    @Test
    fun `should convert CloudEvent to Map`() {
        // given
        val event = CloudEvent.of(
            source = "urn:koosco:order-service",
            type = "com.koosco.order.created",
            data = OrderData("order-123", 10000),
        )

        // when
        val map = EventUtils.toMap(event)

        // then
        assertThat(map["id"]).isEqualTo(event.id)
        assertThat(map["source"]).isEqualTo(event.source)
        assertThat(map["type"]).isEqualTo(event.type)
        assertThat(map["data"]).isNotNull()
    }

    @Test
    fun `should convert Map to CloudEvent`() {
        // given
        val event = CloudEvent.of(
            source = "urn:koosco:order-service",
            type = "com.koosco.order.created",
            data = OrderData("order-123", 10000),
        )
        val map = EventUtils.toMap(event)

        // when
        val convertedEvent = EventUtils.fromMap<OrderData>(map)

        // then
        assertThat(convertedEvent.id).isEqualTo(event.id)
        assertThat(convertedEvent.source).isEqualTo(event.source)
        assertThat(convertedEvent.type).isEqualTo(event.type)
    }

    @Test
    fun `should extract data from CloudEvent`() {
        // given
        val orderData = OrderData("order-123", 10000)
        val event = CloudEvent.of(
            source = "urn:koosco:order-service",
            type = "com.koosco.order.created",
            data = orderData,
        )

        // when
        val extractedData = EventUtils.extractData(event, OrderData::class.java)

        // then
        assertThat(extractedData).isNotNull()
        assertThat(extractedData?.orderId).isEqualTo(orderData.orderId)
        assertThat(extractedData?.amount).isEqualTo(orderData.amount)
    }

    @Test
    fun `should convert data between CloudEvent instances`() {
        // given
        data class SourceData(val value: String)
        data class TargetData(val value: String)

        val sourceEvent = CloudEvent.of(
            source = "urn:koosco:service",
            type = "test.event",
            data = SourceData("test"),
        )

        // when
        val targetEvent = EventUtils.convertData(sourceEvent, TargetData::class.java)

        // then
        assertThat(targetEvent.id).isEqualTo(sourceEvent.id)
        assertThat(targetEvent.source).isEqualTo(sourceEvent.source)
        assertThat(targetEvent.type).isEqualTo(sourceEvent.type)
        assertThat(targetEvent.data).isNotNull()
        assertThat(targetEvent.data?.value).isEqualTo("test")
    }

    @Test
    fun `should validate and serialize CloudEvent`() {
        // given
        val event = CloudEvent.of(
            source = "urn:koosco:order-service",
            type = "com.koosco.order.created",
            data = OrderData("order-123", 10000),
        )

        // when
        EventValidator.validate(event).throwIfInvalid()
        val json = EventUtils.toJson(event)

        // then
        assertThat(json).isNotNull()
        assertThat(json).contains("order-123")
    }

    @Test
    fun `should fail validate and serialize for invalid CloudEvent`() {
        // given
        val event = CloudEvent<OrderData>(
            id = " ",
            source = "urn:koosco:order-service",
            type = "com.koosco.order.created",
        )

        // when & then
        assertThatThrownBy {
            EventValidator.validate(event).throwIfInvalid()
        }.isInstanceOf(ValidationException::class.java)
    }

    @Test
    fun `should deserialize and validate CloudEvent`() {
        // given
        val originalEvent = CloudEvent.of(
            source = "urn:koosco:order-service",
            type = "com.koosco.order.created",
            data = OrderData("order-123", 10000),
        )
        val json = EventUtils.toJson(originalEvent)

        // when
        val event = EventUtils.deserializeAndValidate<OrderData>(json)

        // then
        assertThat(event.id).isEqualTo(originalEvent.id)
        assertThat(event.source).isEqualTo(originalEvent.source)
        assertThat(event.type).isEqualTo(originalEvent.type)
    }

    @Test
    fun `should handle null data in CloudEvent`() {
        // given
        val event = CloudEvent.of<OrderData?>(
            source = "urn:koosco:order-service",
            type = "com.koosco.order.created",
            data = null,
        )

        // when
        val json = EventUtils.toJson(event)
        val deserializedEvent = EventUtils.fromJson<OrderData?>(json)

        // then
        assertThat(deserializedEvent.id).isEqualTo(event.id)
        assertThat(deserializedEvent.data).isNull()
    }

    @Test
    fun `should serialize and deserialize with time field`() {
        // given
        val event = CloudEvent.of(
            source = "urn:koosco:order-service",
            type = "com.koosco.order.created",
            data = OrderData("order-123", 10000),
        )

        // when
        val json = EventUtils.toJson(event)
        val deserializedEvent = EventUtils.fromJson<OrderData>(json)

        // then
        assertThat(deserializedEvent.time).isNotNull()
    }
}
