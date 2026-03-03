package com.koosco.catalogservice.api.response

import com.koosco.catalogservice.application.dto.CategoryInfo
import com.koosco.catalogservice.application.dto.CategoryTreeInfo
import com.koosco.catalogservice.application.result.BrandResult
import com.koosco.catalogservice.application.result.CategoryAttributeInfo
import com.koosco.catalogservice.application.result.DiscountPolicyResult
import com.koosco.catalogservice.application.result.ProductAttributeValueInfo
import com.koosco.catalogservice.application.result.ProductInfo
import com.koosco.catalogservice.application.result.ProductInfo.ProductOptionGroupInfo
import com.koosco.catalogservice.application.result.ProductInfo.ProductOptionInfo
import com.koosco.catalogservice.application.result.PromotionInfo
import com.koosco.catalogservice.application.result.PromotionPriceInfo
import com.koosco.catalogservice.application.result.ReviewResult
import com.koosco.catalogservice.application.result.SkuResult
import com.koosco.catalogservice.application.result.SnapResult
import com.koosco.catalogservice.domain.entity.Product
import com.koosco.catalogservice.domain.entity.ProductOption
import com.koosco.catalogservice.domain.entity.ProductOptionGroup
import com.koosco.catalogservice.domain.entity.ProductSku
import com.koosco.catalogservice.domain.enums.AttributeType
import com.koosco.catalogservice.domain.enums.ContentStatus
import com.koosco.catalogservice.domain.enums.DiscountType
import com.koosco.catalogservice.domain.enums.ProductStatus
import com.koosco.catalogservice.domain.enums.PromotionType
import com.koosco.catalogservice.domain.enums.SkuStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("API Response 매핑 테스트")
class ResponseMappingTest {

    private val now = LocalDateTime.now()

    @Nested
    @DisplayName("ProductListResponse는")
    inner class ProductListResponseTest {

        @Test
        fun `ProductInfo에서 매핑한다`() {
            val info = ProductInfo(
                id = 1L, name = "상품", description = "설명", price = 10000,
                sellingPrice = 9000, discountRate = 10, status = ProductStatus.ACTIVE,
                categoryId = 1L, thumbnailImageUrl = "http://img.jpg", brandId = 1L,
                brandName = "브랜드", averageRating = 4.5, reviewCount = 10,
                viewCount = 100, orderCount = 50, salesCount = 200, likeCount = 30,
            )

            val response = ProductListResponse.from(info)

            assertThat(response.id).isEqualTo(1L)
            assertThat(response.name).isEqualTo("상품")
            assertThat(response.originalPrice).isEqualTo(10000)
            assertThat(response.sellingPrice).isEqualTo(9000)
            assertThat(response.discountRate).isEqualTo(10)
            assertThat(response.brandName).isEqualTo("브랜드")
            assertThat(response.likeCount).isEqualTo(30)
        }
    }

    @Nested
    @DisplayName("ProductDetailResponse는")
    inner class ProductDetailResponseTest {

        @Test
        fun `ProductInfo에서 매핑한다`() {
            val optionInfo = ProductOptionInfo(id = 1L, name = "빨강", additionalPrice = 0, ordering = 0)
            val groupInfo = ProductOptionGroupInfo(id = 1L, name = "색상", ordering = 0, options = listOf(optionInfo))
            val info = ProductInfo(
                id = 1L, name = "상품", description = "설명", price = 10000,
                sellingPrice = 9000, discountRate = 10, status = ProductStatus.ACTIVE,
                categoryId = 1L, thumbnailImageUrl = "http://img.jpg", brandId = 1L,
                brandName = "브랜드", averageRating = 4.5, reviewCount = 10,
                viewCount = 100, orderCount = 50, salesCount = 200, likeCount = 30,
                optionGroups = listOf(groupInfo),
            )

            val response = ProductDetailResponse.from(info)

            assertThat(response.id).isEqualTo(1L)
            assertThat(response.description).isEqualTo("설명")
            assertThat(response.optionGroups).hasSize(1)
            assertThat(response.optionGroups.first().options).hasSize(1)
        }
    }

    @Nested
    @DisplayName("ProductOptionGroupResponse는")
    inner class ProductOptionGroupResponseTest {

        @Test
        fun `ProductOptionGroup 엔티티에서 매핑한다`() {
            val group = ProductOptionGroup(id = 1L, name = "색상", ordering = 0)
            val option = ProductOption(id = 1L, name = "빨강", additionalPrice = 0, ordering = 0)
            group.addOption(option)

            val response = ProductOptionGroupResponse.from(group)

            assertThat(response.id).isEqualTo(1L)
            assertThat(response.name).isEqualTo("색상")
            assertThat(response.options).hasSize(1)
        }

        @Test
        fun `ProductOptionGroupInfo에서 매핑한다`() {
            val optionInfo = ProductOptionInfo(id = 1L, name = "빨강", additionalPrice = 0, ordering = 0)
            val groupInfo = ProductOptionGroupInfo(id = 1L, name = "색상", ordering = 0, options = listOf(optionInfo))

            val response = ProductOptionGroupResponse.from(groupInfo)

            assertThat(response.id).isEqualTo(1L)
            assertThat(response.options).hasSize(1)
        }
    }

    @Nested
    @DisplayName("ProductOptionResponse는")
    inner class ProductOptionResponseTest {

        @Test
        fun `ProductOption 엔티티에서 매핑한다`() {
            val option = ProductOption(id = 1L, name = "빨강", additionalPrice = 500, ordering = 0)

            val response = ProductOptionResponse.from(option)

            assertThat(response.id).isEqualTo(1L)
            assertThat(response.name).isEqualTo("빨강")
            assertThat(response.additionalPrice).isEqualTo(500)
        }

        @Test
        fun `ProductOptionInfo에서 매핑한다`() {
            val info = ProductOptionInfo(id = 1L, name = "빨강", additionalPrice = 500, ordering = 0)

            val response = ProductOptionResponse.from(info)

            assertThat(response.id).isEqualTo(1L)
            assertThat(response.name).isEqualTo("빨강")
            assertThat(response.additionalPrice).isEqualTo(500)
        }
    }

    @Nested
    @DisplayName("CategoryResponse는")
    inner class CategoryResponseTest {

        @Test
        fun `CategoryInfo에서 매핑한다`() {
            val info = CategoryInfo(id = 1L, name = "전자제품", parentId = null, depth = 0, ordering = 0)

            val response = CategoryResponse.from(info)

            assertThat(response.id).isEqualTo(1L)
            assertThat(response.name).isEqualTo("전자제품")
            assertThat(response.parentId).isNull()
        }
    }

    @Nested
    @DisplayName("CategoryTreeResponse는")
    inner class CategoryTreeResponseTest {

        @Test
        fun `CategoryTreeInfo에서 매핑한다`() {
            val childInfo = CategoryTreeInfo(id = 2L, name = "스마트폰", depth = 1, children = emptyList())
            val info = CategoryTreeInfo(id = 1L, name = "전자제품", depth = 0, children = listOf(childInfo))

            val response = CategoryTreeResponse.from(info)

            assertThat(response.id).isEqualTo(1L)
            assertThat(response.children).hasSize(1)
            assertThat(response.children.first().name).isEqualTo("스마트폰")
        }
    }

    @Nested
    @DisplayName("BrandResponse는")
    inner class BrandResponseTest {

        @Test
        fun `BrandResult에서 매핑한다`() {
            val result = BrandResult(id = 1L, name = "Nike", logoImageUrl = "http://logo.jpg")

            val response = BrandResponse.from(result)

            assertThat(response.id).isEqualTo(1L)
            assertThat(response.name).isEqualTo("Nike")
            assertThat(response.logoImageUrl).isEqualTo("http://logo.jpg")
        }
    }

    @Nested
    @DisplayName("ReviewResponse는")
    inner class ReviewResponseTest {

        @Test
        fun `ReviewResult에서 매핑한다`() {
            val result = ReviewResult(
                reviewId = 1L, productId = 1L, userId = 1L, orderItemId = null,
                title = "좋은 상품", content = "만족합니다", rating = 5,
                status = ContentStatus.VISIBLE, likeCount = 10,
                imageUrls = listOf("http://img.jpg"), createdAt = now, updatedAt = now,
            )

            val response = ReviewResponse.from(result)

            assertThat(response.reviewId).isEqualTo(1L)
            assertThat(response.title).isEqualTo("좋은 상품")
            assertThat(response.rating).isEqualTo(5)
            assertThat(response.imageUrls).hasSize(1)
        }
    }

    @Nested
    @DisplayName("LikeToggleResponse는")
    inner class LikeToggleResponseTest {

        @Test
        fun `좋아요 상태를 담는다`() {
            val response = LikeToggleResponse(liked = true)

            assertThat(response.liked).isTrue()
        }
    }

    @Nested
    @DisplayName("SnapResponse는")
    inner class SnapResponseTest {

        @Test
        fun `SnapResult에서 매핑한다`() {
            val result = SnapResult(
                snapId = 1L, productId = 1L, userId = 1L, caption = "캡션",
                status = ContentStatus.VISIBLE, likeCount = 5,
                imageUrls = listOf("http://img.jpg"), createdAt = now, updatedAt = now,
            )

            val response = SnapResponse.from(result)

            assertThat(response.snapId).isEqualTo(1L)
            assertThat(response.caption).isEqualTo("캡션")
            assertThat(response.likeCount).isEqualTo(5)
        }
    }

    @Nested
    @DisplayName("DiscountPolicyResponse는")
    inner class DiscountPolicyResponseTest {

        @Test
        fun `DiscountPolicyResult에서 매핑한다`() {
            val result = DiscountPolicyResult(
                id = 1L, productId = 1L, name = "할인", discountType = DiscountType.RATE,
                discountValue = 10, startAt = now.minusDays(1), endAt = now.plusDays(1),
                active = true, createdAt = now,
            )

            val response = DiscountPolicyResponse.from(result)

            assertThat(response.id).isEqualTo(1L)
            assertThat(response.name).isEqualTo("할인")
            assertThat(response.active).isTrue()
        }
    }

    @Nested
    @DisplayName("PromotionResponse는")
    inner class PromotionResponseTest {

        @Test
        fun `PromotionInfo에서 매핑한다`() {
            val info = PromotionInfo(
                id = 1L, productId = 1L, discountPrice = 8000,
                startAt = now.minusDays(1), endAt = now.plusDays(1),
                type = PromotionType.CAMPAIGN, priority = 0,
                description = "캠페인", active = true,
            )

            val response = PromotionResponse.from(info)

            assertThat(response.id).isEqualTo(1L)
            assertThat(response.discountPrice).isEqualTo(8000)
            assertThat(response.active).isTrue()
        }
    }

    @Nested
    @DisplayName("PromotionPriceResponse는")
    inner class PromotionPriceResponseTest {

        @Test
        fun `PromotionPriceInfo에서 매핑한다`() {
            val info = PromotionPriceInfo(
                productId = 1L,
                originalPrice = 10000,
                discountPrice = 8000,
                finalPrice = 8000,
                hasActivePromotion = true,
            )

            val response = PromotionPriceResponse.from(info)

            assertThat(response.productId).isEqualTo(1L)
            assertThat(response.finalPrice).isEqualTo(8000)
            assertThat(response.hasActivePromotion).isTrue()
        }
    }

    @Nested
    @DisplayName("SkuResponse는")
    inner class SkuResponseTest {

        @Test
        fun `SkuResult에서 매핑한다`() {
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

            val skuResult = SkuResult(sku = sku, available = true)
            val response = SkuResponse.from(skuResult)

            assertThat(response.skuId).isEqualTo("SKU-001")
            assertThat(response.productId).isEqualTo(1L)
            assertThat(response.price).isEqualTo(10000)
            assertThat(response.optionValues).containsEntry("색상", "빨강")
            assertThat(response.available).isTrue()
        }
    }

    @Nested
    @DisplayName("CategoryAttributeResponse는")
    inner class CategoryAttributeResponseTest {

        @Test
        fun `CategoryAttributeInfo에서 매핑한다`() {
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

            val response = CategoryAttributeResponse.from(info)

            assertThat(response.id).isEqualTo(1L)
            assertThat(response.name).isEqualTo("색상")
            assertThat(response.required).isTrue()
            assertThat(response.options).hasSize(2)
        }
    }

    @Nested
    @DisplayName("ProductAttributeValueResponse는")
    inner class ProductAttributeValueResponseTest {

        @Test
        fun `ProductAttributeValueInfo에서 매핑한다`() {
            val info = ProductAttributeValueInfo(
                id = 1L,
                attributeId = 2L,
                attributeName = "색상",
                type = AttributeType.STRING,
                value = "빨강",
            )

            val response = ProductAttributeValueResponse.from(info)

            assertThat(response.id).isEqualTo(1L)
            assertThat(response.attributeName).isEqualTo("색상")
            assertThat(response.value).isEqualTo("빨강")
        }
    }
}
