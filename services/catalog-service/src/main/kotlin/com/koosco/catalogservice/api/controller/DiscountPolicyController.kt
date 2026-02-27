package com.koosco.catalogservice.api.controller

import com.koosco.catalogservice.api.request.CreateDiscountPolicyRequest
import com.koosco.catalogservice.api.response.DiscountPolicyResponse
import com.koosco.catalogservice.application.command.DeleteDiscountPolicyCommand
import com.koosco.catalogservice.application.command.GetDiscountPoliciesCommand
import com.koosco.catalogservice.application.usecase.CreateDiscountPolicyUseCase
import com.koosco.catalogservice.application.usecase.DeleteDiscountPolicyUseCase
import com.koosco.catalogservice.application.usecase.GetDiscountPoliciesUseCase
import com.koosco.common.core.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Discount Policy", description = "상품 할인 정책 관리 API")
@RestController
@RequestMapping("/api/products/{productId}/discounts")
class DiscountPolicyController(
    private val createDiscountPolicyUseCase: CreateDiscountPolicyUseCase,
    private val getDiscountPoliciesUseCase: GetDiscountPoliciesUseCase,
    private val deleteDiscountPolicyUseCase: DeleteDiscountPolicyUseCase,
) {

    @Operation(
        summary = "상품의 할인 정책 목록을 조회합니다.",
        description = "해당 상품에 등록된 모든 할인 정책을 조회합니다.",
    )
    @GetMapping
    fun getDiscountPolicies(
        @Parameter(description = "Product ID") @PathVariable productId: Long,
    ): ApiResponse<List<DiscountPolicyResponse>> {
        val results = getDiscountPoliciesUseCase.execute(GetDiscountPoliciesCommand(productId))

        return ApiResponse.success(results.map { DiscountPolicyResponse.from(it) })
    }

    @Operation(
        summary = "상품에 할인 정책을 추가합니다.",
        description = "정률 또는 정액 할인 정책을 생성합니다. 기간 설정이 필수입니다.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createDiscountPolicy(
        @Parameter(description = "Product ID") @PathVariable productId: Long,
        @Valid @RequestBody request: CreateDiscountPolicyRequest,
    ): ApiResponse<DiscountPolicyResponse> {
        val result = createDiscountPolicyUseCase.execute(request.toCommand(productId))

        return ApiResponse.success(DiscountPolicyResponse.from(result))
    }

    @Operation(
        summary = "할인 정책을 삭제합니다.",
        description = "해당 상품의 특정 할인 정책을 삭제합니다.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @DeleteMapping("/{discountPolicyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteDiscountPolicy(
        @Parameter(description = "Product ID") @PathVariable productId: Long,
        @Parameter(description = "Discount Policy ID") @PathVariable discountPolicyId: Long,
    ): ApiResponse<Any> {
        deleteDiscountPolicyUseCase.execute(
            DeleteDiscountPolicyCommand(productId = productId, discountPolicyId = discountPolicyId),
        )

        return ApiResponse.success()
    }
}
