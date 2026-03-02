package com.koosco.catalogservice.api.controller

import com.koosco.catalogservice.api.response.SkuInfoResponse
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.common.core.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Internal Product", description = "Internal Product APIs for inter-service communication")
@RestController
@RequestMapping("/internal/products")
class InternalProductController(private val productRepository: ProductRepository) {

    @Operation(
        summary = "SKU 정보 일괄 조회 (내부 API)",
        description = "SKU PK ID 목록으로 SKU 정보를 조회합니다. 주문 생성 시 가격/상품 검증에 사용됩니다.",
    )
    @GetMapping("/skus")
    fun getSkuInfos(
        @Parameter(description = "SKU PK ID 목록") @RequestParam skuIds: List<Long>,
    ): ApiResponse<List<SkuInfoResponse>> {
        val responses = skuIds.mapNotNull { skuId ->
            val product = productRepository.findBySkuPkId(skuId) ?: return@mapNotNull null
            val sku = product.skus.find { it.id == skuId } ?: return@mapNotNull null
            SkuInfoResponse.from(sku, product)
        }

        return ApiResponse.success(responses)
    }
}
