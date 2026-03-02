package com.koosco.paymentservice.domain

import com.koosco.paymentservice.domain.entity.PaymentIdempotency
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("PaymentIdempotency 엔티티 테스트")
class PaymentIdempotencyTest {

    @Nested
    @DisplayName("PaymentIdempotency 생성")
    inner class Create {

        @Test
        fun `factory 메서드로 생성할 수 있다`() {
            val idempotency = PaymentIdempotency.create(
                messageId = "msg-123",
                action = PaymentIdempotency.Companion.Actions.CREATE,
                aggregateId = "agg-456",
            )

            assertThat(idempotency.messageId).isEqualTo("msg-123")
            assertThat(idempotency.action).isEqualTo("CREATE")
            assertThat(idempotency.aggregateId).isEqualTo("agg-456")
            assertThat(idempotency.aggregateType).isEqualTo("Payment")
            assertThat(idempotency.processedAt).isNotNull()
        }

        @Test
        fun `APPROVE 액션으로 생성할 수 있다`() {
            val idempotency = PaymentIdempotency.create(
                messageId = "msg-789",
                action = PaymentIdempotency.Companion.Actions.APPROVE,
                aggregateId = "agg-101",
            )

            assertThat(idempotency.action).isEqualTo("APPROVE")
        }

        @Test
        fun `CANCEL 액션으로 생성할 수 있다`() {
            val idempotency = PaymentIdempotency.create(
                messageId = "msg-000",
                action = PaymentIdempotency.Companion.Actions.CANCEL,
                aggregateId = "agg-202",
            )

            assertThat(idempotency.action).isEqualTo("CANCEL")
        }
    }

    @Nested
    @DisplayName("Actions 상수")
    inner class ActionsConstants {

        @Test
        fun `CREATE 상수 값이 올바르다`() {
            assertThat(PaymentIdempotency.Companion.Actions.CREATE).isEqualTo("CREATE")
        }

        @Test
        fun `APPROVE 상수 값이 올바르다`() {
            assertThat(PaymentIdempotency.Companion.Actions.APPROVE).isEqualTo("APPROVE")
        }

        @Test
        fun `CANCEL 상수 값이 올바르다`() {
            assertThat(PaymentIdempotency.Companion.Actions.CANCEL).isEqualTo("CANCEL")
        }
    }
}
