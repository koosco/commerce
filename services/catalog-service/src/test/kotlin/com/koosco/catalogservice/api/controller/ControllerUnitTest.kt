package com.koosco.catalogservice.api.controller

import com.koosco.catalogservice.api.request.AddOptionRequest
import com.koosco.catalogservice.api.request.BrandCreateRequest
import com.koosco.catalogservice.api.request.BrandUpdateRequest
import com.koosco.catalogservice.api.request.CategoryCreateRequest
import com.koosco.catalogservice.api.request.CategoryTreeCreateRequest
import com.koosco.catalogservice.api.request.ChangeStatusRequest
import com.koosco.catalogservice.api.request.CreateCategoryAttributeRequest
import com.koosco.catalogservice.api.request.CreateDiscountPolicyRequest
import com.koosco.catalogservice.api.request.CreatePromotionRequest
import com.koosco.catalogservice.api.request.CreateReviewRequest
import com.koosco.catalogservice.api.request.CreateSnapRequest
import com.koosco.catalogservice.api.request.ProductCreateRequest
import com.koosco.catalogservice.api.request.ProductUpdateRequest
import com.koosco.catalogservice.api.request.SetProductAttributeValuesRequest
import com.koosco.catalogservice.api.request.UpdateCategoryAttributeRequest
import com.koosco.catalogservice.api.request.UpdateReviewRequest
import com.koosco.catalogservice.api.request.UpdateSnapRequest
import com.koosco.catalogservice.application.dto.CategoryInfo
import com.koosco.catalogservice.application.dto.CategoryTreeInfo
import com.koosco.catalogservice.application.result.BrandResult
import com.koosco.catalogservice.application.result.CategoryAttributeInfo
import com.koosco.catalogservice.application.result.DiscountPolicyResult
import com.koosco.catalogservice.application.result.ProductAttributeValueInfo
import com.koosco.catalogservice.application.result.ProductInfo
import com.koosco.catalogservice.application.result.PromotionInfo
import com.koosco.catalogservice.application.result.PromotionPriceInfo
import com.koosco.catalogservice.application.result.ReviewResult
import com.koosco.catalogservice.application.result.SnapResult
import com.koosco.catalogservice.application.usecase.brand.AddProductOptionUseCase
import com.koosco.catalogservice.application.usecase.product.ChangeProductStatusUseCase
import com.koosco.catalogservice.application.usecase.brand.CreateBrandUseCase
import com.koosco.catalogservice.application.usecase.category.CreateCategoryAttributeUseCase
import com.koosco.catalogservice.application.usecase.category.CreateCategoryTreeUseCase
import com.koosco.catalogservice.application.usecase.category.CreateCategoryUseCase
import com.koosco.catalogservice.application.usecase.discount.CreateDiscountPolicyUseCase
import com.koosco.catalogservice.application.usecase.product.CreateProductUseCase
import com.koosco.catalogservice.application.usecase.promotion.CreatePromotionUseCase
import com.koosco.catalogservice.application.usecase.review.CreateReviewUseCase
import com.koosco.catalogservice.application.usecase.snap.CreateSnapUseCase
import com.koosco.catalogservice.application.usecase.brand.DeleteBrandUseCase
import com.koosco.catalogservice.application.usecase.category.DeleteCategoryAttributeUseCase
import com.koosco.catalogservice.application.usecase.discount.DeleteDiscountPolicyUseCase
import com.koosco.catalogservice.application.usecase.product.DeleteProductUseCase
import com.koosco.catalogservice.application.usecase.review.DeleteReviewUseCase
import com.koosco.catalogservice.application.usecase.snap.DeleteSnapUseCase
import com.koosco.catalogservice.application.usecase.product.FindSkuUseCase
import com.koosco.catalogservice.application.usecase.brand.GetBrandsUseCase
import com.koosco.catalogservice.application.usecase.category.GetCategoryAttributesUseCase
import com.koosco.catalogservice.application.usecase.category.GetCategoryByIdUseCase
import com.koosco.catalogservice.application.usecase.category.GetCategoryListUseCase
import com.koosco.catalogservice.application.usecase.category.GetCategoryTreeUseCase
import com.koosco.catalogservice.application.usecase.discount.GetDiscountPoliciesUseCase
import com.koosco.catalogservice.application.usecase.product.GetProductAttributeValuesUseCase
import com.koosco.catalogservice.application.usecase.product.GetProductDetailUseCase
import com.koosco.catalogservice.application.usecase.product.GetProductListUseCase
import com.koosco.catalogservice.application.usecase.promotion.GetPromotionPriceUseCase
import com.koosco.catalogservice.application.usecase.promotion.GetPromotionsByProductUseCase
import com.koosco.catalogservice.application.usecase.review.GetReviewsByProductUseCase
import com.koosco.catalogservice.application.usecase.snap.GetSnapFeedUseCase
import com.koosco.catalogservice.application.usecase.product.RemoveProductOptionUseCase
import com.koosco.catalogservice.application.usecase.product.SetProductAttributeValuesUseCase
import com.koosco.catalogservice.application.usecase.product.ToggleProductLikeUseCase
import com.koosco.catalogservice.application.usecase.review.ToggleReviewLikeUseCase
import com.koosco.catalogservice.application.usecase.snap.ToggleSnapLikeUseCase
import com.koosco.catalogservice.application.usecase.brand.UpdateBrandUseCase
import com.koosco.catalogservice.application.usecase.category.UpdateCategoryAttributeUseCase
import com.koosco.catalogservice.application.usecase.product.UpdateProductUseCase
import com.koosco.catalogservice.application.usecase.review.UpdateReviewUseCase
import com.koosco.catalogservice.application.usecase.snap.UpdateSnapUseCase
import com.koosco.catalogservice.domain.entity.Product
import com.koosco.catalogservice.domain.entity.ProductSku
import com.koosco.catalogservice.domain.enums.AttributeType
import com.koosco.catalogservice.domain.enums.ContentStatus
import com.koosco.catalogservice.domain.enums.DiscountType
import com.koosco.catalogservice.domain.enums.ProductStatus
import com.koosco.catalogservice.domain.enums.PromotionType
import com.koosco.catalogservice.domain.enums.SkuStatus
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
@DisplayName("Controller 단위 테스트")
class ControllerUnitTest {

    private val now = LocalDateTime.now()

    private fun sampleProductInfo() = ProductInfo(
        id = 1L, name = "상품", description = "설명", price = 10000,
        sellingPrice = 9000, discountRate = 10, status = ProductStatus.ACTIVE,
        categoryId = 1L, thumbnailImageUrl = "http://img.jpg", brandId = 1L,
    )

    private fun sampleBrandResult() = BrandResult(id = 1L, name = "Nike", logoImageUrl = "http://logo.jpg")

    private fun sampleCategoryInfo() = CategoryInfo(id = 1L, name = "전자제품", parentId = null, depth = 0, ordering = 0)

    private fun sampleCategoryTreeInfo() = CategoryTreeInfo(
        id = 1L,
        name = "전자제품",
        depth = 0,
        children = emptyList(),
    )

    private fun sampleReviewResult() = ReviewResult(
        reviewId = 1L, productId = 1L, userId = 1L, orderItemId = null,
        title = "좋아요", content = "내용", rating = 5,
        status = ContentStatus.VISIBLE, likeCount = 10,
        imageUrls = listOf("http://img.jpg"), createdAt = now, updatedAt = now,
    )

    private fun sampleSnapResult() = SnapResult(
        snapId = 1L, productId = 1L, userId = 1L, caption = "캡션",
        status = ContentStatus.VISIBLE, likeCount = 5,
        imageUrls = listOf("http://img.jpg"), createdAt = now, updatedAt = now,
    )

    private fun sampleDiscountPolicyResult() = DiscountPolicyResult(
        id = 1L, productId = 1L, name = "할인", discountType = DiscountType.RATE,
        discountValue = 10, startAt = now.minusDays(1), endAt = now.plusDays(1),
        active = true, createdAt = now,
    )

    private fun samplePromotionInfo() = PromotionInfo(
        id = 1L, productId = 1L, discountPrice = 8000,
        startAt = now.minusDays(1), endAt = now.plusDays(1),
        type = PromotionType.CAMPAIGN, priority = 0,
        description = "캠페인", active = true,
    )

    // --- BrandController ---

    @Nested
    @DisplayName("BrandController는")
    inner class BrandControllerTest {

        @Mock lateinit var createBrandUseCase: CreateBrandUseCase

        @Mock lateinit var getBrandsUseCase: GetBrandsUseCase

        @Mock lateinit var updateBrandUseCase: UpdateBrandUseCase

        @Mock lateinit var deleteBrandUseCase: DeleteBrandUseCase

        @Test
        fun `브랜드 목록을 조회한다`() {
            val controller =
                BrandController(createBrandUseCase, getBrandsUseCase, updateBrandUseCase, deleteBrandUseCase)
            whenever(getBrandsUseCase.getAll()).thenReturn(listOf(sampleBrandResult()))

            val response = controller.getBrands()

            assertThat(response.data).hasSize(1)
        }

        @Test
        fun `브랜드 상세를 조회한다`() {
            val controller =
                BrandController(createBrandUseCase, getBrandsUseCase, updateBrandUseCase, deleteBrandUseCase)
            whenever(getBrandsUseCase.getById(1L)).thenReturn(sampleBrandResult())

            val response = controller.getBrand(1L)

            assertThat(response.data!!.name).isEqualTo("Nike")
        }

        @Test
        fun `브랜드를 생성한다`() {
            val controller =
                BrandController(createBrandUseCase, getBrandsUseCase, updateBrandUseCase, deleteBrandUseCase)
            val request = BrandCreateRequest(name = "Nike", logoImageUrl = "http://logo.jpg")
            whenever(createBrandUseCase.execute(any(), anyOrNull())).thenReturn(sampleBrandResult())

            val response = controller.createBrand(1L, request, null)

            assertThat(response.data!!.name).isEqualTo("Nike")
        }

        @Test
        fun `브랜드를 수정한다`() {
            val controller =
                BrandController(createBrandUseCase, getBrandsUseCase, updateBrandUseCase, deleteBrandUseCase)
            val request = BrandUpdateRequest(name = "Puma", logoImageUrl = null)

            val response = controller.updateBrand(1L, 1L, request)

            assertThat(response).isNotNull
        }

        @Test
        fun `브랜드를 삭제한다`() {
            val controller =
                BrandController(createBrandUseCase, getBrandsUseCase, updateBrandUseCase, deleteBrandUseCase)

            val response = controller.deleteBrand(1L, 1L)

            assertThat(response).isNotNull
        }
    }

    // --- CategoryController ---

    @Nested
    @DisplayName("CategoryController는")
    inner class CategoryControllerTest {

        @Mock lateinit var getCategoryByIdUseCase: GetCategoryByIdUseCase

        @Mock lateinit var getCategoryListUseCase: GetCategoryListUseCase

        @Mock lateinit var getCategoryTreeUseCase: GetCategoryTreeUseCase

        @Mock lateinit var createCategoryUseCase: CreateCategoryUseCase

        @Mock lateinit var createCategoryTreeUseCase: CreateCategoryTreeUseCase

        @Test
        fun `카테고리 목록을 조회한다`() {
            val controller = CategoryController(
                getCategoryByIdUseCase,
                getCategoryListUseCase,
                getCategoryTreeUseCase,
                createCategoryUseCase,
                createCategoryTreeUseCase,
            )
            whenever(getCategoryListUseCase.execute(any())).thenReturn(listOf(sampleCategoryInfo()))

            val response = controller.getCategories(null)

            assertThat(response.data).hasSize(1)
        }

        @Test
        fun `카테고리 단건 조회한다`() {
            val controller = CategoryController(
                getCategoryByIdUseCase,
                getCategoryListUseCase,
                getCategoryTreeUseCase,
                createCategoryUseCase,
                createCategoryTreeUseCase,
            )
            whenever(getCategoryByIdUseCase.execute(1L)).thenReturn(sampleCategoryInfo())

            val response = controller.getCategory(1L)

            assertThat(response.data!!.name).isEqualTo("전자제품")
        }

        @Test
        fun `카테고리 트리를 조회한다`() {
            val controller = CategoryController(
                getCategoryByIdUseCase,
                getCategoryListUseCase,
                getCategoryTreeUseCase,
                createCategoryUseCase,
                createCategoryTreeUseCase,
            )
            whenever(getCategoryTreeUseCase.execute()).thenReturn(listOf(sampleCategoryTreeInfo()))

            val response = controller.getCategoryTree()

            assertThat(response.data).hasSize(1)
        }

        @Test
        fun `카테고리를 생성한다`() {
            val controller = CategoryController(
                getCategoryByIdUseCase,
                getCategoryListUseCase,
                getCategoryTreeUseCase,
                createCategoryUseCase,
                createCategoryTreeUseCase,
            )
            val request = CategoryCreateRequest(name = "전자제품", parentId = null, ordering = 0)
            whenever(createCategoryUseCase.execute(any(), anyOrNull())).thenReturn(sampleCategoryInfo())

            val response = controller.createCategory(1L, request, null)

            assertThat(response.data!!.name).isEqualTo("전자제품")
        }

        @Test
        fun `카테고리 트리를 생성한다`() {
            val controller = CategoryController(
                getCategoryByIdUseCase,
                getCategoryListUseCase,
                getCategoryTreeUseCase,
                createCategoryUseCase,
                createCategoryTreeUseCase,
            )
            val request = CategoryTreeCreateRequest(name = "패션")
            whenever(createCategoryTreeUseCase.execute(any(), anyOrNull())).thenReturn(sampleCategoryTreeInfo())

            val response = controller.createCategoryTree(1L, request, null)

            assertThat(response.data!!.name).isEqualTo("전자제품")
        }
    }

    // --- ProductController ---

    @Nested
    @DisplayName("ProductController는")
    inner class ProductControllerUnitTest {

        @Mock lateinit var getProductListUseCase: GetProductListUseCase

        @Mock lateinit var getProductDetailUseCase: GetProductDetailUseCase

        @Mock lateinit var createProductUseCase: CreateProductUseCase

        @Mock lateinit var updateProductUseCase: UpdateProductUseCase

        @Mock lateinit var deleteProductUseCase: DeleteProductUseCase

        @Mock lateinit var findSkuUseCase: FindSkuUseCase

        @Mock lateinit var changeProductStatusUseCase: ChangeProductStatusUseCase

        @Mock lateinit var addProductOptionUseCase: AddProductOptionUseCase

        @Mock lateinit var removeProductOptionUseCase: RemoveProductOptionUseCase

        @Mock lateinit var toggleProductLikeUseCase: ToggleProductLikeUseCase

        private fun controller() = ProductController(
            getProductListUseCase, getProductDetailUseCase, createProductUseCase,
            updateProductUseCase, deleteProductUseCase, findSkuUseCase,
            changeProductStatusUseCase, addProductOptionUseCase,
            removeProductOptionUseCase, toggleProductLikeUseCase,
        )

        @Test
        fun `상품 목록을 조회한다`() {
            whenever(getProductListUseCase.execute(any())).thenReturn(PageImpl(listOf(sampleProductInfo())))

            val response = controller().getProducts(
                categoryId = null, keyword = null, brandId = null,
                minPrice = null, maxPrice = null,
                sort = com.koosco.catalogservice.domain.enums.SortStrategy.LATEST,
                pageable = PageRequest.of(0, 20),
                allRequestParams = mapOf("attr.1" to "빨강"),
                userId = null,
            )

            assertThat(response.data!!.content).hasSize(1)
        }

        @Test
        fun `상품 상세를 조회한다`() {
            whenever(getProductDetailUseCase.execute(any())).thenReturn(sampleProductInfo())

            val response = controller().getProduct(1L, null)

            assertThat(response.data!!.name).isEqualTo("상품")
        }

        @Test
        fun `SKU를 조회한다`() {
            val product = Product(
                id = 1L,
                productCode = "TEST-001",
                name = "테스트",
                price = 10000,
                status = ProductStatus.ACTIVE,
            )
            val sku = ProductSku(
                id = 1L,
                skuId = "SKU-001",
                product = product,
                price = 10000,
                optionValues = """{"색상":"빨강"}""",
                status = SkuStatus.ACTIVE,
            )
            whenever(findSkuUseCase.execute(any())).thenReturn(sku)

            val response = controller().findSku(1L, mapOf("색상" to "빨강"))

            assertThat(response.data!!.skuId).isEqualTo("SKU-001")
        }

        @Test
        fun `SKU 조회 시 옵션이 비어있으면 예외를 던진다`() {
            assertThatThrownBy { controller().findSku(1L, emptyMap()) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `상품을 생성한다`() {
            whenever(createProductUseCase.execute(any(), anyOrNull())).thenReturn(sampleProductInfo())

            val request = ProductCreateRequest(
                name = "상품",
                description = "설명",
                price = 10000,
                status = ProductStatus.ACTIVE,
                categoryId = 1L,
                thumbnailImageUrl = "http://img.jpg",
                brandId = 1L,
                optionGroups = emptyList(),
            )

            val response = controller().createProduct(1L, request, null)

            assertThat(response.data!!.name).isEqualTo("상품")
        }

        @Test
        fun `상품을 수정한다`() {
            val request = ProductUpdateRequest(
                name = "변경",
                description = "설명",
                price = 20000,
                categoryId = 2L,
                thumbnailImageUrl = "http://new.jpg",
                brandId = 3L,
            )

            val response = controller().updateProduct(1L, 1L, request)

            assertThat(response).isNotNull
        }

        @Test
        fun `상품 상태를 변경한다`() {
            val request = ChangeStatusRequest(status = ProductStatus.SUSPENDED)

            val response = controller().changeProductStatus(1L, 1L, request)

            assertThat(response).isNotNull
        }

        @Test
        fun `옵션을 추가한다`() {
            whenever(addProductOptionUseCase.execute(any())).thenReturn(sampleProductInfo())

            val request = AddOptionRequest(
                optionGroupId = 1L,
                options = listOf(AddOptionRequest.OptionValueRequest("초록", 500, 2)),
            )

            val response = controller().addProductOption(1L, 1L, request)

            assertThat(response.data).isNotNull
        }

        @Test
        fun `옵션을 제거한다`() {
            whenever(removeProductOptionUseCase.execute(any())).thenReturn(sampleProductInfo())

            val response = controller().removeProductOption(1L, 1L, 1L)

            assertThat(response.data).isNotNull
        }

        @Test
        fun `상품을 삭제한다`() {
            val response = controller().deleteProduct(1L, 1L)

            assertThat(response).isNotNull
        }

        @Test
        fun `좋아요를 토글한다`() {
            whenever(toggleProductLikeUseCase.execute(any(), any(), anyOrNull())).thenReturn(true)

            val response = controller().toggleProductLike(1L, 1L, null)

            assertThat(response.data!!.liked).isTrue()
        }
    }

    // --- ReviewController ---

    @Nested
    @DisplayName("ReviewController는")
    inner class ReviewControllerUnitTest {

        @Mock lateinit var createReviewUseCase: CreateReviewUseCase

        @Mock lateinit var getReviewsByProductUseCase: GetReviewsByProductUseCase

        @Mock lateinit var updateReviewUseCase: UpdateReviewUseCase

        @Mock lateinit var deleteReviewUseCase: DeleteReviewUseCase

        @Mock lateinit var toggleReviewLikeUseCase: ToggleReviewLikeUseCase

        private fun controller() = ReviewController(
            createReviewUseCase,
            getReviewsByProductUseCase,
            updateReviewUseCase,
            deleteReviewUseCase,
            toggleReviewLikeUseCase,
        )

        @Test
        fun `리뷰를 작성한다`() {
            val request = CreateReviewRequest(
                productId = 1L,
                orderItemId = null,
                title = "좋아요",
                content = "내용",
                rating = 5,
                imageUrls = listOf("http://img.jpg"),
            )
            whenever(createReviewUseCase.execute(any(), anyOrNull())).thenReturn(sampleReviewResult())

            val response = controller().createReview(1L, request, null)

            assertThat(response.data!!.title).isEqualTo("좋아요")
        }

        @Test
        fun `상품별 리뷰를 조회한다`() {
            whenever(getReviewsByProductUseCase.execute(any(), any()))
                .thenReturn(PageImpl(listOf(sampleReviewResult())))

            val response = controller().getReviewsByProduct(1L, PageRequest.of(0, 20))

            assertThat(response.data!!.content).hasSize(1)
        }

        @Test
        fun `리뷰를 수정한다`() {
            val request = UpdateReviewRequest(title = "수정", content = null, rating = 3)
            whenever(updateReviewUseCase.execute(any())).thenReturn(sampleReviewResult())

            val response = controller().updateReview(1L, 1L, request)

            assertThat(response.data).isNotNull
        }

        @Test
        fun `리뷰를 삭제한다`() {
            val response = controller().deleteReview(1L, 1L)

            assertThat(response).isNotNull
        }

        @Test
        fun `리뷰 좋아요를 토글한다`() {
            whenever(toggleReviewLikeUseCase.execute(any(), any(), anyOrNull())).thenReturn(true)

            val response = controller().toggleReviewLike(1L, 1L, null)

            assertThat(response.data!!.liked).isTrue()
        }
    }

    // --- SnapController ---

    @Nested
    @DisplayName("SnapController는")
    inner class SnapControllerUnitTest {

        @Mock lateinit var createSnapUseCase: CreateSnapUseCase

        @Mock lateinit var getSnapFeedUseCase: GetSnapFeedUseCase

        @Mock lateinit var updateSnapUseCase: UpdateSnapUseCase

        @Mock lateinit var deleteSnapUseCase: DeleteSnapUseCase

        @Mock lateinit var toggleSnapLikeUseCase: ToggleSnapLikeUseCase

        private fun controller() = SnapController(
            createSnapUseCase,
            getSnapFeedUseCase,
            updateSnapUseCase,
            deleteSnapUseCase,
            toggleSnapLikeUseCase,
        )

        @Test
        fun `스냅을 작성한다`() {
            val request = CreateSnapRequest(productId = 1L, caption = "캡션", imageUrls = listOf("http://img.jpg"))
            whenever(createSnapUseCase.execute(any(), anyOrNull())).thenReturn(sampleSnapResult())

            val response = controller().createSnap(1L, request, null)

            assertThat(response.data!!.caption).isEqualTo("캡션")
        }

        @Test
        fun `스냅 피드를 조회한다`() {
            whenever(getSnapFeedUseCase.execute(any())).thenReturn(PageImpl(listOf(sampleSnapResult())))

            val response = controller().getSnapFeed(PageRequest.of(0, 20))

            assertThat(response.data!!.content).hasSize(1)
        }

        @Test
        fun `스냅을 수정한다`() {
            val request = UpdateSnapRequest(caption = "변경된 캡션")
            whenever(updateSnapUseCase.execute(any())).thenReturn(sampleSnapResult())

            val response = controller().updateSnap(1L, 1L, request)

            assertThat(response.data).isNotNull
        }

        @Test
        fun `스냅을 삭제한다`() {
            val response = controller().deleteSnap(1L, 1L)

            assertThat(response).isNotNull
        }

        @Test
        fun `스냅 좋아요를 토글한다`() {
            whenever(toggleSnapLikeUseCase.execute(any(), any(), anyOrNull())).thenReturn(true)

            val response = controller().toggleSnapLike(1L, 1L, null)

            assertThat(response.data!!.liked).isTrue()
        }
    }

    // --- DiscountPolicyController ---

    @Nested
    @DisplayName("DiscountPolicyController는")
    inner class DiscountPolicyControllerUnitTest {

        @Mock lateinit var createDiscountPolicyUseCase: CreateDiscountPolicyUseCase

        @Mock lateinit var getDiscountPoliciesUseCase: GetDiscountPoliciesUseCase

        @Mock lateinit var deleteDiscountPolicyUseCase: DeleteDiscountPolicyUseCase

        private fun controller() = DiscountPolicyController(
            createDiscountPolicyUseCase,
            getDiscountPoliciesUseCase,
            deleteDiscountPolicyUseCase,
        )

        @Test
        fun `할인 정책 목록을 조회한다`() {
            whenever(getDiscountPoliciesUseCase.execute(any())).thenReturn(listOf(sampleDiscountPolicyResult()))

            val response = controller().getDiscountPolicies(1L)

            assertThat(response.data).hasSize(1)
        }

        @Test
        fun `할인 정책을 생성한다`() {
            val request = CreateDiscountPolicyRequest(
                name = "할인",
                discountType = DiscountType.RATE,
                discountValue = 10,
                startAt = now,
                endAt = now.plusDays(1),
            )
            whenever(createDiscountPolicyUseCase.execute(any())).thenReturn(sampleDiscountPolicyResult())

            val response = controller().createDiscountPolicy(1L, request)

            assertThat(response.data!!.name).isEqualTo("할인")
        }

        @Test
        fun `할인 정책을 삭제한다`() {
            val response = controller().deleteDiscountPolicy(1L, 1L)

            assertThat(response).isNotNull
        }
    }

    // --- PromotionController ---

    @Nested
    @DisplayName("PromotionController는")
    inner class PromotionControllerUnitTest {

        @Mock lateinit var createPromotionUseCase: CreatePromotionUseCase

        @Mock lateinit var getPromotionsByProductUseCase: GetPromotionsByProductUseCase

        @Mock lateinit var getPromotionPriceUseCase: GetPromotionPriceUseCase

        private fun controller() = PromotionController(
            createPromotionUseCase,
            getPromotionsByProductUseCase,
            getPromotionPriceUseCase,
        )

        @Test
        fun `프로모션을 생성한다`() {
            val request = CreatePromotionRequest(
                productId = 1L,
                discountPrice = 8000,
                startAt = now,
                endAt = now.plusDays(1),
                type = PromotionType.CAMPAIGN,
                priority = 0,
                description = "캠페인",
            )
            whenever(createPromotionUseCase.execute(any())).thenReturn(samplePromotionInfo())

            val response = controller().createPromotion(request)

            assertThat(response.data!!.discountPrice).isEqualTo(8000)
        }

        @Test
        fun `상품의 프로모션을 조회한다`() {
            whenever(getPromotionsByProductUseCase.execute(any())).thenReturn(listOf(samplePromotionInfo()))

            val response = controller().getPromotionsByProduct(1L)

            assertThat(response.data).hasSize(1)
        }

        @Test
        fun `프로모션 가격을 조회한다`() {
            val priceInfo = PromotionPriceInfo(
                productId = 1L,
                originalPrice = 10000,
                discountPrice = 8000,
                finalPrice = 8000,
                hasActivePromotion = true,
            )
            whenever(getPromotionPriceUseCase.execute(any())).thenReturn(priceInfo)

            val response = controller().getPromotionPrice(1L)

            assertThat(response.data!!.finalPrice).isEqualTo(8000)
        }
    }

    // --- CategoryAttributeController ---

    @Nested
    @DisplayName("CategoryAttributeController는")
    inner class CategoryAttributeControllerUnitTest {

        @Mock lateinit var createCategoryAttributeUseCase: CreateCategoryAttributeUseCase

        @Mock lateinit var updateCategoryAttributeUseCase: UpdateCategoryAttributeUseCase

        @Mock lateinit var deleteCategoryAttributeUseCase: DeleteCategoryAttributeUseCase

        @Mock lateinit var getCategoryAttributesUseCase: GetCategoryAttributesUseCase

        private fun controller() = CategoryAttributeController(
            createCategoryAttributeUseCase,
            updateCategoryAttributeUseCase,
            deleteCategoryAttributeUseCase,
            getCategoryAttributesUseCase,
        )

        @Test
        fun `카테고리 속성 목록을 조회한다`() {
            val info = CategoryAttributeInfo(
                id = 1L,
                categoryId = 1L,
                name = "색상",
                type = AttributeType.STRING,
                required = true,
                options = listOf("빨강", "파랑"),
                ordering = 0,
                inherited = false,
            )
            whenever(getCategoryAttributesUseCase.execute(any())).thenReturn(listOf(info))

            val response = controller().getCategoryAttributes(1L, true)

            assertThat(response.data).hasSize(1)
        }

        @Test
        fun `카테고리 속성을 생성한다`() {
            val info = CategoryAttributeInfo(
                id = 1L,
                categoryId = 1L,
                name = "색상",
                type = AttributeType.STRING,
                required = true,
                options = listOf("빨강"),
                ordering = 0,
                inherited = false,
            )
            val request = CreateCategoryAttributeRequest(
                name = "색상",
                type = AttributeType.STRING,
                required = true,
                options = "빨강,파랑",
                ordering = 0,
            )
            whenever(createCategoryAttributeUseCase.execute(any())).thenReturn(info)

            val response = controller().createCategoryAttribute(1L, request)

            assertThat(response.data!!.name).isEqualTo("색상")
        }

        @Test
        fun `카테고리 속성을 수정한다`() {
            val info = CategoryAttributeInfo(
                id = 1L,
                categoryId = 1L,
                name = "변경",
                type = AttributeType.STRING,
                required = false,
                options = emptyList(),
                ordering = 1,
                inherited = false,
            )
            val request = UpdateCategoryAttributeRequest(name = "변경", required = false, options = null, ordering = 1)
            whenever(updateCategoryAttributeUseCase.execute(any())).thenReturn(info)

            val response = controller().updateCategoryAttribute(1L, 1L, request)

            assertThat(response.data!!.name).isEqualTo("변경")
        }

        @Test
        fun `카테고리 속성을 삭제한다`() {
            val response = controller().deleteCategoryAttribute(1L, 1L)

            assertThat(response).isNotNull
        }
    }

    // --- ProductAttributeController ---

    @Nested
    @DisplayName("ProductAttributeController는")
    inner class ProductAttributeControllerUnitTest {

        @Mock lateinit var setProductAttributeValuesUseCase: SetProductAttributeValuesUseCase

        @Mock lateinit var getProductAttributeValuesUseCase: GetProductAttributeValuesUseCase

        private fun controller() = ProductAttributeController(
            setProductAttributeValuesUseCase,
            getProductAttributeValuesUseCase,
        )

        @Test
        fun `상품 속성 값을 조회한다`() {
            val info = ProductAttributeValueInfo(
                id = 1L,
                attributeId = 2L,
                attributeName = "색상",
                type = AttributeType.STRING,
                value = "빨강",
            )
            whenever(getProductAttributeValuesUseCase.execute(any())).thenReturn(listOf(info))

            val response = controller().getProductAttributes(1L)

            assertThat(response.data).hasSize(1)
        }

        @Test
        fun `상품 속성 값을 설정한다`() {
            val info = ProductAttributeValueInfo(
                id = 1L,
                attributeId = 2L,
                attributeName = "색상",
                type = AttributeType.STRING,
                value = "빨강",
            )
            val request = SetProductAttributeValuesRequest(
                attributes = listOf(
                    SetProductAttributeValuesRequest.AttributeValueRequest(attributeId = 1L, value = "빨강"),
                ),
            )
            whenever(setProductAttributeValuesUseCase.execute(any())).thenReturn(listOf(info))

            val response = controller().setProductAttributes(1L, request)

            assertThat(response.data).hasSize(1)
        }
    }
}
