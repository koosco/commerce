package com.koosco.orderservice.application.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.koosco.common.core.event.IntegrationEventProducer
import com.koosco.common.core.exception.ForbiddenException
import com.koosco.common.core.exception.NotFoundException
import com.koosco.common.core.messaging.MessageContext
import com.koosco.orderservice.application.command.CancelOrderCommand
import com.koosco.orderservice.application.command.CreateOrderCommand
import com.koosco.orderservice.application.command.MarkOrderConfirmedCommand
import com.koosco.orderservice.application.command.MarkOrderFailedCommand
import com.koosco.orderservice.application.command.MarkOrderPaidCommand
import com.koosco.orderservice.application.command.MarkOrderPaymentCreatedCommand
import com.koosco.orderservice.application.command.MarkOrderPaymentPendingCommand
import com.koosco.orderservice.application.command.MarkRefundCompletedCommand
import com.koosco.orderservice.application.command.RefundOrderItemsCommand
import com.koosco.orderservice.application.port.InventoryReservationPort
import com.koosco.orderservice.application.port.OrderIdempotencyRepository
import com.koosco.orderservice.application.port.OrderRepository
import com.koosco.orderservice.application.port.OrderStatusHistoryRepository
import com.koosco.orderservice.application.port.UserBehaviorEventProducer
import com.koosco.orderservice.domain.entity.Order
import com.koosco.orderservice.domain.entity.OrderIdempotency
import com.koosco.orderservice.domain.entity.OrderItem
import com.koosco.orderservice.domain.enums.OrderCancelReason
import com.koosco.orderservice.domain.enums.OrderStatus
import com.koosco.orderservice.domain.exception.InvalidOrderStatus
import com.koosco.orderservice.domain.vo.Money
import com.koosco.orderservice.domain.vo.OrderAmount
import com.koosco.orderservice.domain.vo.OrderItemSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

@DisplayName("Order UseCase 테스트")
class OrderUseCaseTest {

    private val orderRepository: OrderRepository = mock()
    private val orderStatusHistoryRepository: OrderStatusHistoryRepository = mock()
    private val orderIdempotencyRepository: OrderIdempotencyRepository = mock()
    private val inventoryReservationPort: InventoryReservationPort = mock()
    private val integrationEventProducer: IntegrationEventProducer = mock()
    private val userBehaviorEventProducer: UserBehaviorEventProducer = mock()
    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    private val messageContext = MessageContext(
        correlationId = "test-correlation-id",
        causationId = "test-causation-id",
    )

    private fun createTestOrder(id: Long = 1L, userId: Long = 1L, status: OrderStatus = OrderStatus.CREATED): Order {
        val specs = listOf(
            OrderItemSpec(1L, 1L, 1L, "상품1", null, 2, Money(10000)),
        )
        val amount = OrderAmount.from(specs, Money.ZERO)
        val order = Order.create(
            orderNo = "ORD-TEST-ORDER",
            userId = userId,
            itemSpecs = specs,
            amount = amount,
            shippingAddressSnapshot = "{}",
        )

        // Set id via reflection
        val idField = Order::class.java.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(order, id)

        // Set item ids
        order.items.forEachIndexed { index, item ->
            val itemIdField = OrderItem::class.java.getDeclaredField("id")
            itemIdField.isAccessible = true
            itemIdField.set(item, (index + 1).toLong())
        }

        // Advance state
        when (status) {
            OrderStatus.RESERVED -> order.markReserved()
            OrderStatus.PAYMENT_CREATED -> {
                order.markReserved()
                order.markPaymentCreated()
            }
            OrderStatus.PAYMENT_PENDING -> {
                order.markReserved()
                order.markPaymentPending()
            }
            OrderStatus.PAID -> {
                order.markReserved()
                order.markPaymentPending()
                order.markPaid(amount.total)
            }
            OrderStatus.CONFIRMED -> {
                order.markReserved()
                order.markPaymentPending()
                order.markPaid(amount.total)
                order.confirmStock()
            }
            OrderStatus.CANCELLED -> {
                order.markReserved()
                order.markPaymentPending()
                order.cancel(OrderCancelReason.USER_REQUEST)
            }
            OrderStatus.FAILED -> {
                order.markFailed(OrderCancelReason.STOCK_RESERVATION_FAILED)
            }
            else -> {}
        }

        return order
    }

    private fun createCommand(): CreateOrderCommand = CreateOrderCommand(
        userId = 1L,
        idempotencyKey = "test-key",
        items = listOf(
            CreateOrderCommand.OrderItemCommand(
                skuId = 1L,
                productId = 1L,
                brandId = 1L,
                titleSnapshot = "상품1",
                optionSnapshot = null,
                quantity = 2,
                unitPrice = Money(10000),
            ),
        ),
        discountAmount = Money.ZERO,
        shippingFee = Money.ZERO,
        shippingAddress = CreateOrderCommand.ShippingAddressCommand(
            recipient = "홍길동",
            phone = "010-1234-5678",
            zipCode = "12345",
            address = "서울시",
            addressDetail = "101호",
        ),
    )

    @Nested
    @DisplayName("CreateOrderUseCase")
    inner class CreateOrderUseCaseTest {

        private val useCase = CreateOrderUseCase(
            orderRepository,
            orderStatusHistoryRepository,
            orderIdempotencyRepository,
            inventoryReservationPort,
            integrationEventProducer,
            objectMapper,
        )

        @Test
        fun `주문 생성 성공`() {
            val command = createCommand()
            val savedOrder = createTestOrder(id = 1L)

            whenever(orderIdempotencyRepository.findByUserIdAndIdempotencyKey(any(), any())).thenReturn(null)
            whenever(orderRepository.save(any())).thenReturn(savedOrder)

            val result = useCase.execute(command)

            assertThat(result.orderId).isEqualTo(1L)
            assertThat(result.status).isEqualTo(OrderStatus.PAYMENT_PENDING)
            verify(orderRepository, times(2)).save(any())
            verify(orderStatusHistoryRepository, times(3)).save(any())
            verify(orderIdempotencyRepository).save(any())

            val reserveCaptor = argumentCaptor<InventoryReservationPort.ReserveCommand>()
            verify(inventoryReservationPort).reserve(reserveCaptor.capture())
            assertThat(reserveCaptor.firstValue.idempotencyKey).isEqualTo("reserve-order-1")
            assertThat(reserveCaptor.firstValue.correlationId).isEqualTo("1")

            verify(integrationEventProducer).publish(any())
        }

        @Test
        fun `멱등성 키가 있고 기존 주문이 존재하면 기존 주문을 반환한다`() {
            val command = createCommand()
            val existingOrder = createTestOrder(id = 1L)
            val idempotency = OrderIdempotency.create(1L, "test-key", 1L)

            whenever(orderIdempotencyRepository.findByUserIdAndIdempotencyKey(1L, "test-key"))
                .thenReturn(idempotency)
            whenever(orderRepository.findById(1L)).thenReturn(existingOrder)

            val result = useCase.execute(command)

            assertThat(result.orderId).isEqualTo(1L)
            verify(orderRepository, never()).save(any())
        }

        @Test
        fun `멱등성 키가 null이면 항상 새 주문을 생성한다`() {
            val command = createCommand().copy(idempotencyKey = null)
            val savedOrder = createTestOrder(id = 1L)

            whenever(orderRepository.save(any())).thenReturn(savedOrder)

            val result = useCase.execute(command)

            assertThat(result.orderId).isEqualTo(1L)
            verify(orderIdempotencyRepository, never()).findByUserIdAndIdempotencyKey(any(), any())
            verify(orderIdempotencyRepository, never()).save(any())
            verify(inventoryReservationPort).reserve(any())
        }
    }

    @Nested
    @DisplayName("GetOrdersUseCase")
    inner class GetOrdersUseCaseTest {

        private val useCase = GetOrdersUseCase(orderRepository)

        @Test
        fun `주문 목록 조회 성공`() {
            val orders = listOf(createTestOrder(id = 1L), createTestOrder(id = 2L))
            whenever(orderRepository.findByUserId(1L)).thenReturn(orders)

            val result = useCase.execute(1L)

            assertThat(result).hasSize(2)
        }

        @Test
        fun `주문 목록 페이징 조회 성공`() {
            val pageable = PageRequest.of(0, 10)
            val orders = listOf(createTestOrder(id = 1L))
            val page = PageImpl(orders, pageable, 1)
            whenever(orderRepository.findByUserId(1L, pageable)).thenReturn(page)

            val result = useCase.execute(1L, pageable)

            assertThat(result.content).hasSize(1)
            assertThat(result.totalElements).isEqualTo(1)
        }
    }

    @Nested
    @DisplayName("GetOrderDetailUseCase")
    inner class GetOrderDetailUseCaseTest {

        private val useCase = GetOrderDetailUseCase(orderRepository)

        @Test
        fun `주문 상세 조회 성공`() {
            val order = createTestOrder(id = 1L, userId = 1L)
            whenever(orderRepository.findById(1L)).thenReturn(order)

            val result = useCase.execute(1L, 1L)

            assertThat(result.orderId).isEqualTo(1L)
            assertThat(result.userId).isEqualTo(1L)
        }

        @Test
        fun `주문이 존재하지 않으면 예외가 발생한다`() {
            whenever(orderRepository.findById(999L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(999L, 1L) }
                .isInstanceOf(NotFoundException::class.java)
        }

        @Test
        fun `다른 사용자의 주문 조회 시 ForbiddenException이 발생한다`() {
            val order = createTestOrder(id = 1L, userId = 1L)
            whenever(orderRepository.findById(1L)).thenReturn(order)

            assertThatThrownBy { useCase.execute(1L, 999L) }
                .isInstanceOf(ForbiddenException::class.java)
        }
    }

    @Nested
    @DisplayName("CancelOrderByUserUseCase")
    inner class CancelOrderByUserUseCaseTest {

        private val useCase = CancelOrderByUserUseCase(
            orderRepository,
            orderStatusHistoryRepository,
            integrationEventProducer,
        )

        @Test
        fun `사용자 요청으로 주문 취소 성공`() {
            val order = createTestOrder(id = 1L, status = OrderStatus.PAYMENT_PENDING)
            whenever(orderRepository.findById(1L)).thenReturn(order)
            whenever(orderRepository.save(any())).thenReturn(order)

            val command = CancelOrderCommand(1L, OrderCancelReason.USER_REQUEST)
            useCase.execute(command, messageContext)

            assertThat(order.status).isEqualTo(OrderStatus.CANCELLED)
            verify(orderStatusHistoryRepository).save(any())
            verify(integrationEventProducer).publish(any())
        }

        @Test
        fun `이미 취소된 주문이면 아무것도 하지 않는다`() {
            val order = createTestOrder(id = 1L, status = OrderStatus.CANCELLED)
            whenever(orderRepository.findById(1L)).thenReturn(order)

            val command = CancelOrderCommand(1L, OrderCancelReason.USER_REQUEST)
            useCase.execute(command, messageContext)

            verify(orderRepository, never()).save(any())
        }

        @Test
        fun `주문이 존재하지 않으면 예외가 발생한다`() {
            whenever(orderRepository.findById(999L)).thenReturn(null)

            val command = CancelOrderCommand(999L, OrderCancelReason.USER_REQUEST)
            assertThatThrownBy { useCase.execute(command, messageContext) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("MarkOrderPaymentCreatedUseCase")
    inner class MarkOrderPaymentCreatedUseCaseTest {

        private val useCase = MarkOrderPaymentCreatedUseCase(
            orderRepository,
            orderStatusHistoryRepository,
        )

        @Test
        fun `RESERVED 상태에서 PAYMENT_CREATED로 전이 성공`() {
            val order = createTestOrder(id = 1L, status = OrderStatus.RESERVED)
            whenever(orderRepository.findById(1L)).thenReturn(order)
            whenever(orderRepository.save(any())).thenReturn(order)

            val command = MarkOrderPaymentCreatedCommand(1L, "pay-123")
            useCase.execute(command, messageContext)

            assertThat(order.status).isEqualTo(OrderStatus.PAYMENT_CREATED)
            verify(orderStatusHistoryRepository).save(any())
        }

        @Test
        fun `CREATED 상태에서 먼저 RESERVED로 전이 후 PAYMENT_CREATED로 전이한다`() {
            val order = createTestOrder(id = 1L, status = OrderStatus.CREATED)
            whenever(orderRepository.findById(1L)).thenReturn(order)
            whenever(orderRepository.save(any())).thenReturn(order)

            val command = MarkOrderPaymentCreatedCommand(1L, "pay-123")
            useCase.execute(command, messageContext)

            assertThat(order.status).isEqualTo(OrderStatus.PAYMENT_CREATED)
        }

        @Test
        fun `이미 PAYMENT_PENDING이면 무시한다`() {
            val order = createTestOrder(id = 1L, status = OrderStatus.PAYMENT_PENDING)
            whenever(orderRepository.findById(1L)).thenReturn(order)

            val command = MarkOrderPaymentCreatedCommand(1L, "pay-123")
            useCase.execute(command, messageContext)

            verify(orderRepository, never()).save(any())
        }

        @Test
        fun `PAID 상태에서 호출 시 예외가 발생한다`() {
            val order = createTestOrder(id = 1L, status = OrderStatus.PAID)
            whenever(orderRepository.findById(1L)).thenReturn(order)

            val command = MarkOrderPaymentCreatedCommand(1L, "pay-123")
            assertThatThrownBy { useCase.execute(command, messageContext) }
                .isInstanceOf(InvalidOrderStatus::class.java)
        }

        @Test
        fun `주문이 존재하지 않으면 예외가 발생한다`() {
            whenever(orderRepository.findById(999L)).thenReturn(null)

            val command = MarkOrderPaymentCreatedCommand(999L, "pay-123")
            assertThatThrownBy { useCase.execute(command, messageContext) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("MarkOrderPaymentPendingUseCase")
    inner class MarkOrderPaymentPendingUseCaseTest {

        private val useCase = MarkOrderPaymentPendingUseCase(
            orderRepository,
            orderStatusHistoryRepository,
        )

        @Test
        fun `RESERVED 상태에서 PAYMENT_PENDING으로 전이 성공`() {
            val order = createTestOrder(id = 1L, status = OrderStatus.RESERVED)
            whenever(orderRepository.findById(1L)).thenReturn(order)
            whenever(orderRepository.save(any())).thenReturn(order)

            val command = MarkOrderPaymentPendingCommand(1L)
            useCase.execute(command)

            assertThat(order.status).isEqualTo(OrderStatus.PAYMENT_PENDING)
        }

        @Test
        fun `CREATED 상태에서 먼저 RESERVED로 전이 후 PAYMENT_PENDING으로 전이한다`() {
            val order = createTestOrder(id = 1L, status = OrderStatus.CREATED)
            whenever(orderRepository.findById(1L)).thenReturn(order)
            whenever(orderRepository.save(any())).thenReturn(order)

            val command = MarkOrderPaymentPendingCommand(1L)
            useCase.execute(command)

            assertThat(order.status).isEqualTo(OrderStatus.PAYMENT_PENDING)
        }

        @Test
        fun `이미 PAYMENT_PENDING이면 무시한다`() {
            val order = createTestOrder(id = 1L, status = OrderStatus.PAYMENT_PENDING)
            whenever(orderRepository.findById(1L)).thenReturn(order)

            val command = MarkOrderPaymentPendingCommand(1L)
            useCase.execute(command)

            verify(orderRepository, never()).save(any())
        }

        @Test
        fun `PAID 상태에서 호출 시 예외가 발생한다`() {
            val order = createTestOrder(id = 1L, status = OrderStatus.PAID)
            whenever(orderRepository.findById(1L)).thenReturn(order)

            val command = MarkOrderPaymentPendingCommand(1L)
            assertThatThrownBy { useCase.execute(command) }
                .isInstanceOf(InvalidOrderStatus::class.java)
        }

        @Test
        fun `주문이 존재하지 않으면 예외가 발생한다`() {
            whenever(orderRepository.findById(999L)).thenReturn(null)

            val command = MarkOrderPaymentPendingCommand(999L)
            assertThatThrownBy { useCase.execute(command) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("MarkOrderPaidUseCase")
    inner class MarkOrderPaidUseCaseTest {

        private val useCase = MarkOrderPaidUseCase(
            orderRepository,
            orderStatusHistoryRepository,
            integrationEventProducer,
        )

        @Test
        fun `PAYMENT_PENDING 상태에서 결제 완료 처리 성공`() {
            val order = createTestOrder(id = 1L, status = OrderStatus.PAYMENT_PENDING)
            whenever(orderRepository.findById(1L)).thenReturn(order)
            whenever(orderRepository.save(any())).thenReturn(order)

            val command = MarkOrderPaidCommand(1L, order.totalAmount.amount)
            useCase.execute(command, messageContext)

            assertThat(order.status).isEqualTo(OrderStatus.PAID)
            verify(integrationEventProducer).publish(any())
        }

        @Test
        fun `주문이 존재하지 않으면 예외가 발생한다`() {
            whenever(orderRepository.findById(999L)).thenReturn(null)

            val command = MarkOrderPaidCommand(999L, 10000)
            assertThatThrownBy { useCase.execute(command, messageContext) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("MarkOrderConfirmedUseCase")
    inner class MarkOrderConfirmedUseCaseTest {

        private val useCase = MarkOrderConfirmedUseCase(
            orderRepository,
            orderStatusHistoryRepository,
            userBehaviorEventProducer,
        )

        @Test
        fun `PAID 상태에서 재고 확정 처리 성공`() {
            val order = createTestOrder(id = 1L, status = OrderStatus.PAID)
            whenever(orderRepository.findById(1L)).thenReturn(order)
            whenever(orderRepository.save(any())).thenReturn(order)

            val command = MarkOrderConfirmedCommand(1L)
            useCase.execute(command)

            assertThat(order.status).isEqualTo(OrderStatus.CONFIRMED)
            verify(orderStatusHistoryRepository).save(any())
            verify(userBehaviorEventProducer).publish(any())
        }

        @Test
        fun `주문이 존재하지 않으면 예외가 발생한다`() {
            whenever(orderRepository.findById(999L)).thenReturn(null)

            val command = MarkOrderConfirmedCommand(999L)
            assertThatThrownBy { useCase.execute(command) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("CancelOrderByPaymentFailureUseCase")
    inner class CancelOrderByPaymentFailureUseCaseTest {

        private val useCase = CancelOrderByPaymentFailureUseCase(
            orderRepository,
            orderStatusHistoryRepository,
            integrationEventProducer,
        )

        @Test
        fun `결제 실패로 주문 취소 성공`() {
            val order = createTestOrder(id = 1L, status = OrderStatus.PAYMENT_PENDING)
            whenever(orderRepository.findById(1L)).thenReturn(order)
            whenever(orderRepository.save(any())).thenReturn(order)

            val command = CancelOrderCommand(1L, OrderCancelReason.PAYMENT_FAILED)
            useCase.execute(command, messageContext)

            assertThat(order.status).isEqualTo(OrderStatus.CANCELLED)
            verify(integrationEventProducer).publish(any())
        }

        @Test
        fun `이미 취소된 주문이면 무시한다`() {
            val order = createTestOrder(id = 1L, status = OrderStatus.CANCELLED)
            whenever(orderRepository.findById(1L)).thenReturn(order)

            val command = CancelOrderCommand(1L, OrderCancelReason.PAYMENT_FAILED)
            useCase.execute(command, messageContext)

            verify(orderRepository, never()).save(any())
        }

        @Test
        fun `주문이 존재하지 않으면 예외가 발생한다`() {
            whenever(orderRepository.findById(999L)).thenReturn(null)

            val command = CancelOrderCommand(999L, OrderCancelReason.PAYMENT_FAILED)
            assertThatThrownBy { useCase.execute(command, messageContext) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("CancelOrderByStockFailureUseCase")
    inner class CancelOrderByStockFailureUseCaseTest {

        private val useCase = CancelOrderByStockFailureUseCase(
            orderRepository,
            orderStatusHistoryRepository,
        )

        @Test
        fun `재고 예약 실패로 주문 실패 처리 성공`() {
            val order = createTestOrder(id = 1L, status = OrderStatus.CREATED)
            whenever(orderRepository.findById(1L)).thenReturn(order)
            whenever(orderRepository.save(any())).thenReturn(order)

            val command = MarkOrderFailedCommand(1L, "재고 부족")
            useCase.execute(command, messageContext)

            assertThat(order.status).isEqualTo(OrderStatus.FAILED)
        }

        @Test
        fun `이미 실패 처리된 주문이면 무시한다`() {
            val order = createTestOrder(id = 1L, status = OrderStatus.FAILED)
            whenever(orderRepository.findById(1L)).thenReturn(order)

            val command = MarkOrderFailedCommand(1L, "재고 부족")
            useCase.execute(command, messageContext)

            verify(orderRepository, never()).save(any())
        }

        @Test
        fun `주문이 존재하지 않으면 예외가 발생한다`() {
            whenever(orderRepository.findById(999L)).thenReturn(null)

            val command = MarkOrderFailedCommand(999L, "재고 부족")
            assertThatThrownBy { useCase.execute(command, messageContext) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("CancelOrderByStockConfirmFailureUseCase")
    inner class CancelOrderByStockConfirmFailureUseCaseTest {

        private val useCase = CancelOrderByStockConfirmFailureUseCase(
            orderRepository,
            orderStatusHistoryRepository,
            integrationEventProducer,
        )

        @Test
        fun `재고 확정 실패로 주문 취소 성공`() {
            val order = createTestOrder(id = 1L, status = OrderStatus.PAID)
            whenever(orderRepository.findById(1L)).thenReturn(order)
            whenever(orderRepository.save(any())).thenReturn(order)

            useCase.execute(1L, messageContext)

            assertThat(order.status).isEqualTo(OrderStatus.CANCELLED)
            verify(integrationEventProducer).publish(any())
        }

        @Test
        fun `이미 취소된 주문이면 무시한다`() {
            val order = createTestOrder(id = 1L, status = OrderStatus.CANCELLED)
            whenever(orderRepository.findById(1L)).thenReturn(order)

            useCase.execute(1L, messageContext)

            verify(orderRepository, never()).save(any())
        }

        @Test
        fun `주문이 존재하지 않으면 예외가 발생한다`() {
            whenever(orderRepository.findById(999L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(999L, messageContext) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("RefundOrderItemsUseCase")
    inner class RefundOrderItemsUseCaseTest {

        private val useCase = RefundOrderItemsUseCase(
            orderRepository,
            orderStatusHistoryRepository,
            integrationEventProducer,
        )

        @Test
        fun `환불 요청 성공`() {
            val order = createTestOrder(id = 1L, status = OrderStatus.CONFIRMED)
            whenever(orderRepository.findById(1L)).thenReturn(order)
            whenever(orderRepository.save(any())).thenReturn(order)

            val command = RefundOrderItemsCommand(1L, 1L, listOf(1L))
            val result = useCase.execute(command)

            assertThat(result.orderId).isEqualTo(1L)
            assertThat(result.refundAmount).isEqualTo(20000L)
            assertThat(result.refundedItemIds).containsExactly(1L)
            verify(integrationEventProducer).publish(any())
        }

        @Test
        fun `주문이 존재하지 않으면 예외가 발생한다`() {
            whenever(orderRepository.findById(999L)).thenReturn(null)

            val command = RefundOrderItemsCommand(999L, 1L, listOf(1L))
            assertThatThrownBy { useCase.execute(command) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("MarkRefundCompletedUseCase")
    inner class MarkRefundCompletedUseCaseTest {

        private val useCase = MarkRefundCompletedUseCase(
            orderRepository,
            orderStatusHistoryRepository,
        )

        @Test
        fun `환불 완료 처리 성공`() {
            val order = createTestOrder(id = 1L, status = OrderStatus.CONFIRMED)
            whenever(orderRepository.findById(1L)).thenReturn(order)
            whenever(orderRepository.save(any())).thenReturn(order)

            val command = MarkRefundCompletedCommand(1L, 10000L, false)
            useCase.execute(command, messageContext)

            verify(orderStatusHistoryRepository).save(any())
            verify(orderRepository).save(any())
        }

        @Test
        fun `주문이 존재하지 않으면 예외가 발생한다`() {
            whenever(orderRepository.findById(999L)).thenReturn(null)

            val command = MarkRefundCompletedCommand(999L, 10000L, true)
            assertThatThrownBy { useCase.execute(command, messageContext) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }
}
