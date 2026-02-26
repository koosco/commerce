package com.koosco.catalogservice.api.controller

import com.koosco.catalogservice.api.request.CreatePromotionRequest
import com.koosco.catalogservice.api.response.PromotionPriceResponse
import com.koosco.catalogservice.api.response.PromotionResponse
import com.koosco.catalogservice.application.command.GetPromotionPriceCommand
import com.koosco.catalogservice.application.command.GetPromotionsByProductCommand
import com.koosco.catalogservice.application.usecase.CreatePromotionUseCase
import com.koosco.catalogservice.application.usecase.GetPromotionPriceUseCase
import com.koosco.catalogservice.application.usecase.GetPromotionsByProductUseCase
import com.koosco.common.core.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Promotion", description = "Promotion management APIs")
@RestController
@RequestMapping("/api/promotions")
class PromotionController(
    private val createPromotionUseCase: CreatePromotionUseCase,
    private val getPromotionsByProductUseCase: GetPromotionsByProductUseCase,
    private val getPromotionPriceUseCase: GetPromotionPriceUseCase,
) {

    @Operation(
        summary = "프로모션을 등록합니다.",
        description = "상품에 대한 할인 프로모션을 Insert-only 방식으로 등록합니다.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createPromotion(@Valid @RequestBody request: CreatePromotionRequest): ApiResponse<PromotionResponse> {
        val promotionInfo = createPromotionUseCase.execute(request.toCommand())
        return ApiResponse.success(PromotionResponse.from(promotionInfo))
    }

    @Operation(
        summary = "상품의 활성 프로모션 목록을 조회합니다.",
        description = "현재 시점에 활성화된 프로모션을 우선순위 순으로 조회합니다.",
    )
    @GetMapping("/products/{productId}")
    fun getPromotionsByProduct(
        @Parameter(description = "Product ID") @PathVariable productId: Long,
    ): ApiResponse<List<PromotionResponse>> {
        val promotions = getPromotionsByProductUseCase.execute(
            GetPromotionsByProductCommand(productId),
        )
        return ApiResponse.success(promotions.map { PromotionResponse.from(it) })
    }

    @Operation(
        summary = "상품의 프로모션 적용 가격을 조회합니다.",
        description = "원래 가격과 프로모션 할인가를 포함한 최종 가격 정보를 반환합니다.",
    )
    @GetMapping("/products/{productId}/price")
    fun getPromotionPrice(
        @Parameter(description = "Product ID") @PathVariable productId: Long,
    ): ApiResponse<PromotionPriceResponse> {
        val priceInfo = getPromotionPriceUseCase.execute(
            GetPromotionPriceCommand(productId),
        )
        return ApiResponse.success(PromotionPriceResponse.from(priceInfo))
    }
}
