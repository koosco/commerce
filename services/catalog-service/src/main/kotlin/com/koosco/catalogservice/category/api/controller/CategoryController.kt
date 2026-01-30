package com.koosco.catalogservice.category.api.controller

import com.koosco.catalogservice.category.api.CategoryCreateRequest
import com.koosco.catalogservice.category.api.CategoryResponse
import com.koosco.catalogservice.category.api.CategoryTreeCreateRequest
import com.koosco.catalogservice.category.api.CategoryTreeResponse
import com.koosco.catalogservice.category.application.dto.GetCategoryListCommand
import com.koosco.catalogservice.category.application.usecase.*
import com.koosco.common.core.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@Tag(name = "Category", description = "Category management APIs")
@RestController
@RequestMapping("/api/categories")
class CategoryController(
    private val getCategoryListUseCase: GetCategoryListUseCase,
    private val getCategoryTreeUseCase: GetCategoryTreeUseCase,
    private val createCategoryUseCase: CreateCategoryUseCase,
    private val createCategoryTreeUseCase: CreateCategoryTreeUseCase,
) {
    @Operation(
        summary = "카테고리 목록 조회",
        description = "카테고리 목록을 조회합니다. parentId를 입력하지 않을 경우 최상위 카테고리를 조회합니다.",
    )
    @GetMapping
    fun getCategories(
        @Parameter(
            description = "Parent category ID (null for root categories)",
        ) @RequestParam(required = false) parentId: Long?,
    ): ApiResponse<List<CategoryResponse>> {
        val command = GetCategoryListCommand(parentId = parentId)

        val response = getCategoryListUseCase.execute(command).map { CategoryResponse.from(it) }

        return ApiResponse.success(response)
    }

    @Operation(summary = "카테고리 트리 조회", description = "카테고리를 계층 트리 형태로 조회합니다.")
    @GetMapping("/tree")
    fun getCategoryTree(): ApiResponse<List<CategoryTreeResponse>> {
        val response = getCategoryTreeUseCase.execute().map {
            CategoryTreeResponse.from(it)
        }

        return ApiResponse.success(response)
    }

    @Operation(
        summary = "새로운 카테고리를 생성합니다.",
        description = "새로운 카테고리를 생성합니다. 관리자만 사용 가능합니다.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createCategory(@Valid @RequestBody request: CategoryCreateRequest): ApiResponse<CategoryResponse> {
        val command = request.toCommand()

        val categoryInfo = createCategoryUseCase.execute(command)

        return ApiResponse.success(CategoryResponse.from(categoryInfo))
    }

    @Operation(
        summary = "카테고리 트리를 생성합니다.",
        description = "계층 구조를 가진 카테고리 트리를 한 번에 생성합니다. 관리자만 사용 가능합니다.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @PostMapping("/tree")
    @ResponseStatus(HttpStatus.CREATED)
    fun createCategoryTree(@Valid @RequestBody request: CategoryTreeCreateRequest): ApiResponse<CategoryTreeResponse> {
        val command = request.toCommand()

        val categoryTreeInfo = createCategoryTreeUseCase.execute(command)

        return ApiResponse.success(CategoryTreeResponse.from(categoryTreeInfo))
    }
}
