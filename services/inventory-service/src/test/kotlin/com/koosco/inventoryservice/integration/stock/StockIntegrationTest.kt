package com.koosco.inventoryservice.integration.stock

import com.koosco.common.core.test.IntegrationTestBase
import com.koosco.inventoryservice.application.command.GetInventoryCommand
import com.koosco.inventoryservice.application.command.InitStockCommand
import com.koosco.inventoryservice.application.usecase.GetInventoryUseCase
import com.koosco.inventoryservice.application.usecase.InitializeStockUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.UUID

/**
 * Integration test for stock initialization and query using real Redis + MariaDB containers.
 *
 * Verifies:
 * - Stock initialization stores data in Redis correctly
 * - Stock query returns accurate total/reserved/available values
 * - Duplicate initialization is rejected with proper error
 */
@SpringBootTest
@ActiveProfiles("test")
class StockIntegrationTest : IntegrationTestBase() {

    @Autowired
    private lateinit var initializeStockUseCase: InitializeStockUseCase

    @Autowired
    private lateinit var getInventoryUseCase: GetInventoryUseCase

    @Test
    fun `should initialize stock and query it correctly`() {
        // given
        val skuId = "TEST-SKU-INIT-${UUID.randomUUID()}"
        val initialQuantity = 100

        // when
        initializeStockUseCase.execute(InitStockCommand(skuId, initialQuantity))

        // then
        val result = getInventoryUseCase.execute(GetInventoryCommand(skuId))
        assertEquals(skuId, result.skuId)
        assertEquals(initialQuantity, result.totalStock)
        assertEquals(0, result.reservedStock)
        assertEquals(initialQuantity, result.availableStock)
    }

    @Test
    fun `should reject duplicate stock initialization`() {
        // given
        val skuId = "TEST-SKU-DUP-${UUID.randomUUID()}"
        val initialQuantity = 50
        initializeStockUseCase.execute(InitStockCommand(skuId, initialQuantity))

        // when & then
        assertThrows(IllegalStateException::class.java) {
            initializeStockUseCase.execute(InitStockCommand(skuId, initialQuantity))
        }
    }
}
