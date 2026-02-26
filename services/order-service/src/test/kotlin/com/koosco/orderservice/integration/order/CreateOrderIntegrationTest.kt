package com.koosco.orderservice.integration.order

import com.koosco.common.core.test.IntegrationTestBase
import com.koosco.orderservice.application.command.CreateOrderCommand
import com.koosco.orderservice.application.usecase.CreateOrderUseCase
import com.koosco.orderservice.application.usecase.GetOrderDetailUseCase
import com.koosco.orderservice.domain.enums.OrderStatus
import com.koosco.orderservice.domain.vo.Money
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

/**
 * Integration test for order creation using real MariaDB + Kafka containers.
 *
 * Verifies:
 * - Order is persisted correctly with all fields
 * - Order starts in CREATED status
 * - Order items are saved correctly
 * - Idempotency key prevents duplicate order creation
 */
@SpringBootTest
@ActiveProfiles("test")
class CreateOrderIntegrationTest : IntegrationTestBase() {

    @Autowired
    private lateinit var createOrderUseCase: CreateOrderUseCase

    @Autowired
    private lateinit var getOrderDetailUseCase: GetOrderDetailUseCase

    @Test
    fun `should create order and persist it in database`() {
        // given
        val userId = 1L
        val command = CreateOrderCommand(
            userId = userId,
            idempotencyKey = null,
            items = listOf(
                CreateOrderCommand.OrderItemCommand(
                    skuId = 100L,
                    productId = 10L,
                    brandId = 1L,
                    titleSnapshot = "Test Product",
                    optionSnapshot = "Color: Red",
                    quantity = 2,
                    unitPrice = Money(10000L),
                ),
            ),
            discountAmount = Money(0L),
            shippingFee = Money(3000L),
            shippingAddress = CreateOrderCommand.ShippingAddressCommand(
                recipient = "John Doe",
                phone = "010-1234-5678",
                zipCode = "12345",
                address = "Seoul, Korea",
                addressDetail = "Apt 101",
            ),
        )

        // when
        val result = createOrderUseCase.execute(command)

        // then
        assertNotNull(result.orderId)
        assertTrue(result.orderNo.startsWith("ORD-"))
        assertEquals(OrderStatus.CREATED, result.status)
        assertEquals(23000L, result.totalAmount) // 10000 * 2 + 3000 shipping

        // verify persistence using GetOrderDetailUseCase (handles transaction scope)
        val detail = getOrderDetailUseCase.execute(result.orderId)
        assertNotNull(detail)
        assertEquals(userId, detail.userId)
        assertEquals(1, detail.items.size)
        assertEquals(2, detail.items[0].qty)
    }

    @Test
    fun `should return same order for duplicate idempotency key`() {
        // given
        val userId = 2L
        val idempotencyKey = "test-idempotency-key-001"
        val command = CreateOrderCommand(
            userId = userId,
            idempotencyKey = idempotencyKey,
            items = listOf(
                CreateOrderCommand.OrderItemCommand(
                    skuId = 200L,
                    productId = 20L,
                    brandId = 2L,
                    titleSnapshot = "Another Product",
                    optionSnapshot = null,
                    quantity = 1,
                    unitPrice = Money(5000L),
                ),
            ),
            discountAmount = Money(0L),
            shippingFee = Money(0L),
            shippingAddress = CreateOrderCommand.ShippingAddressCommand(
                recipient = "Jane Doe",
                phone = "010-9876-5432",
                zipCode = "54321",
                address = "Busan, Korea",
                addressDetail = "Apt 202",
            ),
        )

        // when
        val firstResult = createOrderUseCase.execute(command)
        val secondResult = createOrderUseCase.execute(command)

        // then
        assertEquals(firstResult.orderId, secondResult.orderId)
        assertEquals(firstResult.orderNo, secondResult.orderNo)
    }
}
