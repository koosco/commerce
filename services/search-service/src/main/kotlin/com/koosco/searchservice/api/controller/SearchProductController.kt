package com.koosco.searchservice.api.controller

import com.koosco.common.core.response.ApiResponse
import com.koosco.searchservice.api.response.SearchProductResponse
import com.koosco.searchservice.application.command.SearchProductCommand
import com.koosco.searchservice.application.usecase.search.SearchProductUseCase
import com.koosco.searchservice.domain.enums.SearchSortStrategy
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Search", description = "Product search APIs")
@RestController
@RequestMapping("/api/search")
class SearchProductController(private val searchProductUseCase: SearchProductUseCase) {

    @Operation(
        summary = "상품을 검색합니다.",
        description = "FULLTEXT 인덱스를 활용하여 키워드 기반 상품 검색을 수행합니다. " +
            "카테고리, 브랜드, 가격 범위 필터링 및 다양한 정렬 전략을 지원합니다.",
    )
    @GetMapping("/products")
    fun searchProducts(
        @Parameter(description = "검색 키워드") @RequestParam(required = false) keyword: String?,
        @Parameter(description = "카테고리 ID") @RequestParam(required = false) categoryId: Long?,
        @Parameter(description = "브랜드 ID") @RequestParam(required = false) brandId: Long?,
        @Parameter(description = "최소 가격") @RequestParam(required = false) minPrice: Long?,
        @Parameter(description = "최대 가격") @RequestParam(required = false) maxPrice: Long?,
        @Parameter(description = "상품 상태 (기본값: ACTIVE)") @RequestParam(required = false, defaultValue = "ACTIVE")
        status: String,
        @Parameter(description = "정렬 (RELEVANCE, LATEST, PRICE_ASC, PRICE_DESC, POPULARITY)")
        @RequestParam(required = false, defaultValue = "RELEVANCE") sort: SearchSortStrategy,
        @Parameter(description = "페이징 파라미터 (page, size)") @PageableDefault(size = 20) pageable: Pageable,
    ): ApiResponse<Page<SearchProductResponse>> {
        val command = SearchProductCommand(
            keyword = keyword,
            categoryId = categoryId,
            brandId = brandId,
            minPrice = minPrice,
            maxPrice = maxPrice,
            status = status,
            sort = sort,
            pageable = pageable,
        )

        return ApiResponse.success(
            searchProductUseCase.execute(command).map { SearchProductResponse.from(it) },
        )
    }
}
