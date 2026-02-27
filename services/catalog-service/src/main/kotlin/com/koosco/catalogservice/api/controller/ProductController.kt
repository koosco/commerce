package com.koosco.catalogservice.api.controller

import com.koosco.catalogservice.api.request.AddOptionRequest
import com.koosco.catalogservice.api.request.ChangeStatusRequest
import com.koosco.catalogservice.api.request.ProductCreateRequest
import com.koosco.catalogservice.api.request.ProductUpdateRequest
import com.koosco.catalogservice.api.response.ProductDetailResponse
import com.koosco.catalogservice.api.response.ProductListResponse
import com.koosco.catalogservice.api.response.SkuResponse
import com.koosco.catalogservice.application.command.DeleteProductCommand
import com.koosco.catalogservice.application.command.FindSkuCommand
import com.koosco.catalogservice.application.command.GetProductDetailCommand
import com.koosco.catalogservice.application.command.GetProductListCommand
import com.koosco.catalogservice.application.command.RemoveProductOptionCommand
import com.koosco.catalogservice.application.usecase.AddProductOptionUseCase
import com.koosco.catalogservice.application.usecase.ChangeProductStatusUseCase
import com.koosco.catalogservice.application.usecase.CreateProductUseCase
import com.koosco.catalogservice.application.usecase.DeleteProductUseCase
import com.koosco.catalogservice.application.usecase.FindSkuUseCase
import com.koosco.catalogservice.application.usecase.GetProductDetailUseCase
import com.koosco.catalogservice.application.usecase.GetProductListUseCase
import com.koosco.catalogservice.application.usecase.RemoveProductOptionUseCase
import com.koosco.catalogservice.application.usecase.UpdateProductUseCase
import com.koosco.catalogservice.domain.enums.SortStrategy
import com.koosco.common.core.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Product", description = "Product management APIs")
@RestController
@RequestMapping("/api/products")
class ProductController(
    private val getProductListUseCase: GetProductListUseCase,
    private val getProductDetailUseCase: GetProductDetailUseCase,
    private val createProductUseCase: CreateProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val findSkuUseCase: FindSkuUseCase,
    private val changeProductStatusUseCase: ChangeProductStatusUseCase,
    private val addProductOptionUseCase: AddProductOptionUseCase,
    private val removeProductOptionUseCase: RemoveProductOptionUseCase,
) {
    @Operation(
        summary = "상품 리스트를 조회합니다.",
        description = "필터링 조건에 따라 상품을 페이징처리하여 조회합니다. " +
            "속성 필터링: attr.{attributeId}={value} 형식으로 전달합니다. 예: attr.1=빨강&attr.2=XL",
    )
    @GetMapping
    fun getProducts(
        @Parameter(description = "카테고리 ID") @RequestParam(required = false) categoryId: Long?,
        @Parameter(description = "이름 또는 상품 설명") @RequestParam(required = false) keyword: String?,
        @Parameter(description = "브랜드 ID") @RequestParam(required = false) brandId: Long?,
        @Parameter(description = "최소 가격") @RequestParam(required = false) minPrice: Long?,
        @Parameter(description = "최대 가격") @RequestParam(required = false) maxPrice: Long?,
        @Parameter(description = "정렬 (RECOMMENDED, LATEST, PRICE_ASC, PRICE_DESC, POPULARITY)")
        @RequestParam(required = false, defaultValue = "LATEST") sort: SortStrategy,
        @Parameter(description = "페이징 파라미터 (page, size)") @PageableDefault(size = 20) pageable: Pageable,
        @Parameter(hidden = true) @RequestParam allRequestParams: Map<String, String>,
    ): ApiResponse<Page<ProductListResponse>> {
        val attributeFilters = allRequestParams
            .filter { it.key.startsWith("attr.") }
            .mapKeys { it.key.removePrefix("attr.").toLongOrNull() }
            .filterKeys { it != null }
            .mapKeys { it.key!! }

        val command = GetProductListCommand(
            categoryId = categoryId,
            keyword = keyword,
            brandId = brandId,
            minPrice = minPrice,
            maxPrice = maxPrice,
            sort = sort,
            pageable = pageable,
            attributeFilters = attributeFilters,
        )

        return ApiResponse.success(
            getProductListUseCase.execute(command).map {
                ProductListResponse.from(it)
            },
        )
    }

    @Operation(summary = "상품 상세를 조회합니다.", description = "옵션을 포함하여 상품을 조회합니다.")
    @GetMapping("/{productId}")
    fun getProduct(
        @Parameter(description = "Product ID") @PathVariable productId: Long,
    ): ApiResponse<ProductDetailResponse> {
        val command = GetProductDetailCommand(productId = productId)
        val productInfo = getProductDetailUseCase.execute(command)

        return ApiResponse.Companion.success(ProductDetailResponse.Companion.from(productInfo))
    }

    @Operation(
        summary = "옵션 조합으로 SKU를 조회합니다.",
        description = "사용자가 선택한 옵션 조합에 해당하는 SKU 정보(가격, 재고 등)를 조회합니다. " +
            "예시: GET /api/products/2/skus?Volume=100ml&Package=Single",
    )
    @GetMapping("/{productId}/skus")
    fun findSku(
        @Parameter(description = "Product ID") @PathVariable productId: Long,
        @Parameter(description = "옵션 조합 (예: Volume=100ml&Package=Single)")
        @RequestParam allRequestParams: Map<String, String>,
    ): ApiResponse<SkuResponse> {
        // 옵션이 비어있는지 검증
        if (allRequestParams.isEmpty()) {
            throw IllegalArgumentException("옵션을 선택해주세요")
        }

        val command = FindSkuCommand(
            productId = productId,
            options = allRequestParams,
        )
        val sku = findSkuUseCase.execute(command)

        return ApiResponse.Companion.success(SkuResponse.Companion.from(sku))
    }

    @Operation(
        summary = "새로운 상품을 추가합니다.",
        description = "상품 옵션과 함께 상품을 생성합니다. 판매자만 등록이 가능합니다.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createProduct(
        @Valid @RequestBody request: ProductCreateRequest,
        @RequestHeader("Idempotency-Key", required = false) idempotencyKey: String?,
    ): ApiResponse<ProductDetailResponse> {
        val productInfo = createProductUseCase.execute(request.toCommand(), idempotencyKey)

        return ApiResponse.Companion.success(ProductDetailResponse.Companion.from(productInfo))
    }

    @Operation(
        summary = "상품 정보를 업데이트합니다.",
        description = "상품 정보를 업데이트합니다. 판매자만 수정이 가능합니다.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @PutMapping("/{productId}")
    fun updateProduct(
        @Parameter(description = "Product ID") @PathVariable productId: Long,
        @Valid @RequestBody request: ProductUpdateRequest,
    ): ApiResponse<Any> {
        updateProductUseCase.execute(request.toCommand(productId))

        return ApiResponse.Companion.success()
    }

    @Operation(
        summary = "상품 상태를 변경합니다.",
        description = "상품의 상태를 변경합니다. 허용된 상태 전이만 가능합니다. (DRAFT→ACTIVE, ACTIVE→SUSPENDED 등)",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @PatchMapping("/{productId}/status")
    fun changeProductStatus(
        @Parameter(description = "Product ID") @PathVariable productId: Long,
        @Valid @RequestBody request: ChangeStatusRequest,
    ): ApiResponse<Any> {
        changeProductStatusUseCase.execute(request.toCommand(productId))

        return ApiResponse.Companion.success()
    }

    @Operation(
        summary = "상품 옵션을 추가합니다.",
        description = "기존 옵션 그룹에 새로운 옵션 값을 추가하고 누락된 SKU 조합을 자동 생성합니다.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @PostMapping("/{productId}/options")
    fun addProductOption(
        @Parameter(description = "Product ID") @PathVariable productId: Long,
        @Valid @RequestBody request: AddOptionRequest,
    ): ApiResponse<ProductDetailResponse> {
        val productInfo = addProductOptionUseCase.execute(request.toCommand(productId))

        return ApiResponse.Companion.success(ProductDetailResponse.Companion.from(productInfo))
    }

    @Operation(
        summary = "상품 옵션을 제거합니다.",
        description = "옵션을 제거하고 해당 옵션이 포함된 SKU를 비활성화합니다.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @DeleteMapping("/{productId}/options/{optionId}")
    fun removeProductOption(
        @Parameter(description = "Product ID") @PathVariable productId: Long,
        @Parameter(description = "Option ID") @PathVariable optionId: Long,
    ): ApiResponse<ProductDetailResponse> {
        val productInfo = removeProductOptionUseCase.execute(
            RemoveProductOptionCommand(productId = productId, optionId = optionId),
        )

        return ApiResponse.Companion.success(ProductDetailResponse.Companion.from(productInfo))
    }

    @Operation(
        summary = "상품을 삭제합니다.",
        description = "상품을 삭제합니다. 판매자만 삭제가 가능합니다.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteProduct(@Parameter(description = "Product ID") @PathVariable productId: Long): ApiResponse<Any> {
        deleteProductUseCase.execute(DeleteProductCommand(productId = productId))

        return ApiResponse.Companion.success()
    }
}
