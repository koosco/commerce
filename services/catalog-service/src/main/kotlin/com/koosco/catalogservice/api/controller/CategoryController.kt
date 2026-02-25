package com.koosco.catalogservice.api.controller

import com.koosco.catalogservice.api.request.CategoryCreateRequest
import com.koosco.catalogservice.api.request.CategoryTreeCreateRequest
import com.koosco.catalogservice.api.response.CategoryResponse
import com.koosco.catalogservice.api.response.CategoryTreeResponse
import com.koosco.catalogservice.application.command.GetCategoryListCommand
import com.koosco.catalogservice.application.usecase.CreateCategoryTreeUseCase
import com.koosco.catalogservice.application.usecase.CreateCategoryUseCase
import com.koosco.catalogservice.application.usecase.GetCategoryByIdUseCase
import com.koosco.catalogservice.application.usecase.GetCategoryListUseCase
import com.koosco.catalogservice.application.usecase.GetCategoryTreeUseCase
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Category", description = "Category management APIs")
@RestController
@RequestMapping("/api/categories")
class CategoryController(
    private val getCategoryByIdUseCase: GetCategoryByIdUseCase,
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

        val response = getCategoryListUseCase.execute(command).map { CategoryResponse.Companion.from(it) }

        return ApiResponse.Companion.success(response)
    }

    @Operation(summary = "카테고리 단건 조회", description = "카테고리 ID로 단건 조회합니다.")
    @GetMapping("/{categoryId}")
    fun getCategory(@PathVariable categoryId: Long): ApiResponse<CategoryResponse> {
        val categoryInfo = getCategoryByIdUseCase.execute(categoryId)
        return ApiResponse.Companion.success(CategoryResponse.Companion.from(categoryInfo))
    }

    @Operation(summary = "카테고리 트리 조회", description = "카테고리를 계층 트리 형태로 조회합니다.")
    @GetMapping("/tree")
    fun getCategoryTree(): ApiResponse<List<CategoryTreeResponse>> {
        val response = getCategoryTreeUseCase.execute().map {
            CategoryTreeResponse.Companion.from(it)
        }

        return ApiResponse.Companion.success(response)
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

        return ApiResponse.Companion.success(CategoryResponse.Companion.from(categoryInfo))
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

        return ApiResponse.Companion.success(CategoryTreeResponse.Companion.from(categoryTreeInfo))
    }
}
