package com.koosco.catalogservice.api.controller

import com.koosco.catalogservice.api.request.CreateCategoryAttributeRequest
import com.koosco.catalogservice.api.request.UpdateCategoryAttributeRequest
import com.koosco.catalogservice.api.response.CategoryAttributeResponse
import com.koosco.catalogservice.application.command.DeleteCategoryAttributeCommand
import com.koosco.catalogservice.application.command.GetCategoryAttributesCommand
import com.koosco.catalogservice.application.usecase.CreateCategoryAttributeUseCase
import com.koosco.catalogservice.application.usecase.DeleteCategoryAttributeUseCase
import com.koosco.catalogservice.application.usecase.GetCategoryAttributesUseCase
import com.koosco.catalogservice.application.usecase.UpdateCategoryAttributeUseCase
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
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Category Attribute", description = "카테고리 속성 관리 APIs")
@RestController
@RequestMapping("/api/categories")
class CategoryAttributeController(
    private val createCategoryAttributeUseCase: CreateCategoryAttributeUseCase,
    private val updateCategoryAttributeUseCase: UpdateCategoryAttributeUseCase,
    private val deleteCategoryAttributeUseCase: DeleteCategoryAttributeUseCase,
    private val getCategoryAttributesUseCase: GetCategoryAttributesUseCase,
) {

    @Operation(
        summary = "카테고리 속성 목록을 조회합니다.",
        description = "카테고리에 정의된 속성 목록을 조회합니다. 상위 카테고리 상속 여부를 선택할 수 있습니다.",
    )
    @GetMapping("/{categoryId}/attributes")
    fun getCategoryAttributes(
        @Parameter(description = "Category ID") @PathVariable categoryId: Long,
        @Parameter(description = "상위 카테고리 속성 상속 포함 여부")
        @RequestParam(required = false, defaultValue = "true") includeInherited: Boolean,
    ): ApiResponse<List<CategoryAttributeResponse>> {
        val command = GetCategoryAttributesCommand(
            categoryId = categoryId,
            includeInherited = includeInherited,
        )

        val result = getCategoryAttributesUseCase.execute(command)

        return ApiResponse.success(result.map { CategoryAttributeResponse.from(it) })
    }

    @Operation(
        summary = "카테고리 속성을 추가합니다.",
        description = "카테고리에 새로운 속성을 정의합니다. 관리자만 사용 가능합니다.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @PostMapping("/{categoryId}/attributes")
    @ResponseStatus(HttpStatus.CREATED)
    fun createCategoryAttribute(
        @Parameter(description = "Category ID") @PathVariable categoryId: Long,
        @Valid @RequestBody request: CreateCategoryAttributeRequest,
    ): ApiResponse<CategoryAttributeResponse> {
        val result = createCategoryAttributeUseCase.execute(request.toCommand(categoryId))

        return ApiResponse.success(CategoryAttributeResponse.from(result))
    }

    @Operation(
        summary = "카테고리 속성을 수정합니다.",
        description = "카테고리 속성 정보를 수정합니다. 관리자만 사용 가능합니다.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @PutMapping("/{categoryId}/attributes/{attributeId}")
    fun updateCategoryAttribute(
        @Parameter(description = "Category ID") @PathVariable categoryId: Long,
        @Parameter(description = "Attribute ID") @PathVariable attributeId: Long,
        @Valid @RequestBody request: UpdateCategoryAttributeRequest,
    ): ApiResponse<CategoryAttributeResponse> {
        val result = updateCategoryAttributeUseCase.execute(request.toCommand(attributeId))

        return ApiResponse.success(CategoryAttributeResponse.from(result))
    }

    @Operation(
        summary = "카테고리 속성을 삭제합니다.",
        description = "카테고리 속성을 삭제합니다. 관리자만 사용 가능합니다.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @DeleteMapping("/{categoryId}/attributes/{attributeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCategoryAttribute(
        @Parameter(description = "Category ID") @PathVariable categoryId: Long,
        @Parameter(description = "Attribute ID") @PathVariable attributeId: Long,
    ): ApiResponse<Any> {
        deleteCategoryAttributeUseCase.execute(DeleteCategoryAttributeCommand(attributeId))

        return ApiResponse.success()
    }
}
