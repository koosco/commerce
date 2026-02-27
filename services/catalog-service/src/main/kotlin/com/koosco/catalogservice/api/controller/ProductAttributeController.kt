package com.koosco.catalogservice.api.controller

import com.koosco.catalogservice.api.request.SetProductAttributeValuesRequest
import com.koosco.catalogservice.api.response.ProductAttributeValueResponse
import com.koosco.catalogservice.application.usecase.GetProductAttributeValuesUseCase
import com.koosco.catalogservice.application.usecase.SetProductAttributeValuesUseCase
import com.koosco.common.core.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Product Attribute", description = "상품 속성 값 관리 APIs")
@RestController
@RequestMapping("/api/products")
class ProductAttributeController(
    private val setProductAttributeValuesUseCase: SetProductAttributeValuesUseCase,
    private val getProductAttributeValuesUseCase: GetProductAttributeValuesUseCase,
) {

    @Operation(
        summary = "상품의 속성 값을 조회합니다.",
        description = "상품에 설정된 속성 값 목록을 조회합니다.",
    )
    @GetMapping("/{productId}/attributes")
    fun getProductAttributes(
        @Parameter(description = "Product ID") @PathVariable productId: Long,
    ): ApiResponse<List<ProductAttributeValueResponse>> {
        val result = getProductAttributeValuesUseCase.execute(productId)

        return ApiResponse.success(result.map { ProductAttributeValueResponse.from(it) })
    }

    @Operation(
        summary = "상품의 속성 값을 설정합니다.",
        description = "상품에 속성 값을 설정합니다. 기존 속성 값은 모두 교체됩니다. 판매자만 사용 가능합니다.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @PutMapping("/{productId}/attributes")
    fun setProductAttributes(
        @Parameter(description = "Product ID") @PathVariable productId: Long,
        @Valid @RequestBody request: SetProductAttributeValuesRequest,
    ): ApiResponse<List<ProductAttributeValueResponse>> {
        val result = setProductAttributeValuesUseCase.execute(request.toCommand(productId))

        return ApiResponse.success(result.map { ProductAttributeValueResponse.from(it) })
    }
}
