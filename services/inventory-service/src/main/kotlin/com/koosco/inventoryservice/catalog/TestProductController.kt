package com.koosco.inventoryservice.catalog

import com.koosco.common.core.response.ApiResponse
import com.koosco.inventoryservice.inventory.application.contract.inbound.catalog.ProductSkuCreatedEvent
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.*

/**
 * fileName       : TestProductController
 * author         : koo
 * date           : 2025. 12. 27. 오후 6:52
 * description    : Integration Event 발행 테스트를 위한 Controller, local profile only
 */
@Profile("local")
@RestController
@RequestMapping("/api/products/test")
class TestProductController(
    private val productService: TestProductService,
    private val productConsumer: TestProductConsumer,
) {

    @PostMapping("/sku/created")
    fun productSkuCreated(): ApiResponse<List<ProductSkuCreatedEvent>> {
        val productSkuCreatedEvents = productService.execute()

        return ApiResponse.success(productSkuCreatedEvents)
    }

    @GetMapping
    fun getReceivedEvents(): ApiResponse<Map<String, List<Any>>> {
        val events = productConsumer.getAllReceivedEvents()

        return ApiResponse.success(events)
    }

    @DeleteMapping("/clear")
    fun clearReceivedEvents(): ApiResponse<Unit> {
        productConsumer.clearReceivedEvents()
        return ApiResponse.success(Unit)
    }
}
