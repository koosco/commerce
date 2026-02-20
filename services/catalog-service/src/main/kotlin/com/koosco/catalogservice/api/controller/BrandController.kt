package com.koosco.catalogservice.api.controller

import com.koosco.catalogservice.api.request.BrandCreateRequest
import com.koosco.catalogservice.api.request.BrandUpdateRequest
import com.koosco.catalogservice.api.response.BrandResponse
import com.koosco.catalogservice.application.command.DeleteBrandCommand
import com.koosco.catalogservice.application.usecase.CreateBrandUseCase
import com.koosco.catalogservice.application.usecase.DeleteBrandUseCase
import com.koosco.catalogservice.application.usecase.GetBrandsUseCase
import com.koosco.catalogservice.application.usecase.UpdateBrandUseCase
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
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Brand", description = "Brand management APIs")
@RestController
@RequestMapping("/api/brands")
class BrandController(
    private val createBrandUseCase: CreateBrandUseCase,
    private val getBrandsUseCase: GetBrandsUseCase,
    private val updateBrandUseCase: UpdateBrandUseCase,
    private val deleteBrandUseCase: DeleteBrandUseCase,
) {
    @Operation(summary = "브랜드 목록을 조회합니다.")
    @GetMapping
    fun getBrands(): ApiResponse<List<BrandResponse>> {
        val brands = getBrandsUseCase.getAll().map { BrandResponse.from(it) }
        return ApiResponse.success(brands)
    }

    @Operation(summary = "브랜드 상세를 조회합니다.")
    @GetMapping("/{brandId}")
    fun getBrand(@Parameter(description = "Brand ID") @PathVariable brandId: Long): ApiResponse<BrandResponse> {
        val brand = getBrandsUseCase.getById(brandId)
        return ApiResponse.success(BrandResponse.from(brand))
    }

    @Operation(
        summary = "새로운 브랜드를 등록합니다.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createBrand(@Valid @RequestBody request: BrandCreateRequest): ApiResponse<BrandResponse> {
        val result = createBrandUseCase.execute(request.toCommand())
        return ApiResponse.success(BrandResponse.from(result))
    }

    @Operation(
        summary = "브랜드 정보를 수정합니다.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @PutMapping("/{brandId}")
    fun updateBrand(
        @Parameter(description = "Brand ID") @PathVariable brandId: Long,
        @Valid @RequestBody request: BrandUpdateRequest,
    ): ApiResponse<Any> {
        updateBrandUseCase.execute(request.toCommand(brandId))
        return ApiResponse.success()
    }

    @Operation(
        summary = "브랜드를 삭제합니다.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @DeleteMapping("/{brandId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteBrand(@Parameter(description = "Brand ID") @PathVariable brandId: Long): ApiResponse<Any> {
        deleteBrandUseCase.execute(DeleteBrandCommand(brandId))
        return ApiResponse.success()
    }
}
