package com.koosco.paymentservice.domain

import com.koosco.common.core.outbox.OutboxStatus
import com.koosco.paymentservice.domain.entity.PaymentOutboxEntry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("PaymentOutboxEntry 엔티티 테스트")
class PaymentOutboxEntryTest {

    @Nested
    @DisplayName("PaymentOutboxEntry 생성")
    inner class Create {

        @Test
        fun `factory 메서드로 생성할 수 있다`() {
            val entry = PaymentOutboxEntry.create(
                aggregateId = "payment-123",
                eventType = "payment.created",
                payload = """{"paymentId":"payment-123"}""",
                topic = "koosco.commerce.payment.created",
                partitionKey = "payment-123",
            )

            assertThat(entry.aggregateId).isEqualTo("payment-123")
            assertThat(entry.aggregateType).isEqualTo("Payment")
            assertThat(entry.eventType).isEqualTo("payment.created")
            assertThat(entry.payload).isEqualTo("""{"paymentId":"payment-123"}""")
            assertThat(entry.status).isEqualTo(OutboxStatus.PENDING)
            assertThat(entry.topic).isEqualTo("koosco.commerce.payment.created")
            assertThat(entry.partitionKey).isEqualTo("payment-123")
            assertThat(entry.createdAt).isNotNull()
        }

        @Test
        fun `status를 변경할 수 있다`() {
            val entry = PaymentOutboxEntry.create(
                aggregateId = "payment-123",
                eventType = "payment.created",
                payload = "{}",
                topic = "topic",
                partitionKey = "key",
            )

            entry.status = OutboxStatus.PUBLISHED

            assertThat(entry.status).isEqualTo(OutboxStatus.PUBLISHED)
        }
    }
}
