package com.koosco.catalogservice.api.request

import com.koosco.catalogservice.domain.enums.AttributeType
import com.koosco.catalogservice.domain.enums.DiscountType
import com.koosco.catalogservice.domain.enums.ProductStatus
import com.koosco.catalogservice.domain.enums.PromotionType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("API Request 매핑 테스트")
class RequestMappingTest {

    @Nested
    @DisplayName("ProductCreateRequest는")
    inner class ProductCreateRequestTest {

        @Test
        fun `Command로 변환한다`() {
            val request = ProductCreateRequest(
                name = "상품",
                description = "설명",
                price = 10000,
                status = ProductStatus.ACTIVE,
                categoryId = 1L,
                thumbnailImageUrl = "http://img.jpg",
                brandId = 1L,
                optionGroups = listOf(
                    ProductCreateRequest.ProductOptionGroup(
                        name = "색상",
                        ordering = 0,
                        options = listOf(
                            ProductCreateRequest.ProductOption("빨강", 0, 0),
                        ),
                    ),
                ),
            )

            val command = request.toCommand()

            assertThat(command.name).isEqualTo("상품")
            assertThat(command.description).isEqualTo("설명")
            assertThat(command.price).isEqualTo(10000)
            assertThat(command.status).isEqualTo(ProductStatus.ACTIVE)
            assertThat(command.categoryId).isEqualTo(1L)
            assertThat(command.thumbnailImageUrl).isEqualTo("http://img.jpg")
            assertThat(command.brandId).isEqualTo(1L)
            assertThat(command.optionGroups).hasSize(1)
            assertThat(command.optionGroups.first().name).isEqualTo("색상")
            assertThat(command.optionGroups.first().ordering).isEqualTo(0)
            assertThat(command.optionGroups.first().options).hasSize(1)
            assertThat(command.optionGroups.first().options.first().name).isEqualTo("빨강")
            assertThat(command.optionGroups.first().options.first().additionalPrice).isEqualTo(0)
            assertThat(command.optionGroups.first().options.first().ordering).isEqualTo(0)
        }

        @Test
        fun `ProductOptionGroup 프로퍼티 접근이 가능하다`() {
            val option = ProductCreateRequest.ProductOption("빨강", 0, 0)
            val group = ProductCreateRequest.ProductOptionGroup("색상", 0, listOf(option))
            assertThat(group.name).isEqualTo("색상")
            assertThat(group.ordering).isEqualTo(0)
            assertThat(group.options).hasSize(1)
            assertThat(group.toString()).contains("색상")
            assertThat(group).isEqualTo(ProductCreateRequest.ProductOptionGroup("색상", 0, listOf(option)))
        }

        @Test
        fun `ProductOption 프로퍼티 접근이 가능하다`() {
            val option = ProductCreateRequest.ProductOption("빨강", 500, 1)
            assertThat(option.name).isEqualTo("빨강")
            assertThat(option.additionalPrice).isEqualTo(500)
            assertThat(option.ordering).isEqualTo(1)
            assertThat(option.toString()).contains("빨강")
            assertThat(option).isEqualTo(ProductCreateRequest.ProductOption("빨강", 500, 1))
        }
    }

    @Nested
    @DisplayName("ProductUpdateRequest는")
    inner class ProductUpdateRequestTest {

        @Test
        fun `Command로 변환한다`() {
            val request = ProductUpdateRequest(
                name = "변경",
                description = "설명",
                price = 20000,
                categoryId = 2L,
                thumbnailImageUrl = "http://new.jpg",
                brandId = 3L,
            )

            val command = request.toCommand(1L)

            assertThat(command.productId).isEqualTo(1L)
            assertThat(command.name).isEqualTo("변경")
            assertThat(command.price).isEqualTo(20000)
        }
    }

    @Nested
    @DisplayName("ChangeStatusRequest는")
    inner class ChangeStatusRequestTest {

        @Test
        fun `Command로 변환한다`() {
            val request = ChangeStatusRequest(status = ProductStatus.SUSPENDED)

            val command = request.toCommand(1L)

            assertThat(command.productId).isEqualTo(1L)
            assertThat(command.status).isEqualTo(ProductStatus.SUSPENDED)
        }
    }

    @Nested
    @DisplayName("AddOptionRequest는")
    inner class AddOptionRequestTest {

        @Test
        fun `Command로 변환한다`() {
            val request = AddOptionRequest(
                optionGroupId = 1L,
                options = listOf(
                    AddOptionRequest.OptionValueRequest("초록", 500, 2),
                ),
            )

            val command = request.toCommand(1L)

            assertThat(command.productId).isEqualTo(1L)
            assertThat(command.optionGroupId).isEqualTo(1L)
            assertThat(command.options).hasSize(1)
            assertThat(command.options.first().name).isEqualTo("초록")
            assertThat(command.options.first().additionalPrice).isEqualTo(500)
        }
    }

    @Nested
    @DisplayName("CategoryCreateRequest는")
    inner class CategoryCreateRequestTest {

        @Test
        fun `Command로 변환한다`() {
            val request = CategoryCreateRequest(name = "전자제품", parentId = null, ordering = 0)

            val command = request.toCommand()

            assertThat(command.name).isEqualTo("전자제품")
            assertThat(command.parentId).isNull()
            assertThat(command.ordering).isEqualTo(0)
        }

        @Test
        fun `프로퍼티 접근이 가능하다`() {
            val request = CategoryCreateRequest("전자제품", null, 0)
            assertThat(request.name).isEqualTo("전자제품")
            assertThat(request.parentId).isNull()
            assertThat(request.ordering).isEqualTo(0)
            assertThat(request.toString()).contains("전자제품")
            assertThat(request).isEqualTo(CategoryCreateRequest("전자제품", null, 0))
        }
    }

    @Nested
    @DisplayName("CategoryTreeCreateRequest는")
    inner class CategoryTreeCreateRequestTest {

        @Test
        fun `Command로 변환한다`() {
            val request = CategoryTreeCreateRequest(
                name = "패션",
                ordering = 0,
                children = listOf(CategoryTreeCreateRequest("남성")),
            )

            val command = request.toCommand()

            assertThat(command.name).isEqualTo("패션")
            assertThat(command.children).hasSize(1)
            assertThat(command.children.first().name).isEqualTo("남성")
        }
    }

    @Nested
    @DisplayName("BrandCreateRequest는")
    inner class BrandCreateRequestTest {

        @Test
        fun `Command로 변환한다`() {
            val request = BrandCreateRequest(name = "Nike", logoImageUrl = "http://logo.jpg")

            val command = request.toCommand()

            assertThat(command.name).isEqualTo("Nike")
            assertThat(command.logoImageUrl).isEqualTo("http://logo.jpg")
        }
    }

    @Nested
    @DisplayName("BrandUpdateRequest는")
    inner class BrandUpdateRequestTest {

        @Test
        fun `Command로 변환한다`() {
            val request = BrandUpdateRequest(name = "Puma", logoImageUrl = null)

            val command = request.toCommand(1L)

            assertThat(command.brandId).isEqualTo(1L)
            assertThat(command.name).isEqualTo("Puma")
        }
    }

    @Nested
    @DisplayName("CreateReviewRequest는")
    inner class CreateReviewRequestTest {

        @Test
        fun `Command로 변환한다`() {
            val request = CreateReviewRequest(
                productId = 1L,
                orderItemId = null,
                title = "좋아요",
                content = "내용",
                rating = 5,
                imageUrls = listOf("http://img.jpg"),
            )

            val command = request.toCommand(1L)

            assertThat(command.productId).isEqualTo(1L)
            assertThat(command.userId).isEqualTo(1L)
            assertThat(command.orderItemId).isNull()
            assertThat(command.title).isEqualTo("좋아요")
            assertThat(command.content).isEqualTo("내용")
            assertThat(command.rating).isEqualTo(5)
            assertThat(command.imageUrls).hasSize(1)
        }

        @Test
        fun `프로퍼티 접근이 가능하다`() {
            val request = CreateReviewRequest(1L, null, "좋아요", "내용", 5, listOf("http://img.jpg"))
            assertThat(request.productId).isEqualTo(1L)
            assertThat(request.orderItemId).isNull()
            assertThat(request.title).isEqualTo("좋아요")
            assertThat(request.content).isEqualTo("내용")
            assertThat(request.rating).isEqualTo(5)
            assertThat(request.imageUrls).hasSize(1)
            assertThat(request.toString()).contains("좋아요")
            assertThat(request).isEqualTo(CreateReviewRequest(1L, null, "좋아요", "내용", 5, listOf("http://img.jpg")))
        }
    }

    @Nested
    @DisplayName("UpdateReviewRequest는")
    inner class UpdateReviewRequestTest {

        @Test
        fun `Command로 변환한다`() {
            val request = UpdateReviewRequest(title = "수정", content = null, rating = 3)

            val command = request.toCommand(1L, 2L)

            assertThat(command.reviewId).isEqualTo(1L)
            assertThat(command.userId).isEqualTo(2L)
            assertThat(command.title).isEqualTo("수정")
            assertThat(command.rating).isEqualTo(3)
        }
    }

    @Nested
    @DisplayName("CreateSnapRequest는")
    inner class CreateSnapRequestTest {

        @Test
        fun `Command로 변환한다`() {
            val request = CreateSnapRequest(productId = 1L, caption = "캡션", imageUrls = listOf("http://img.jpg"))

            val command = request.toCommand(2L)

            assertThat(command.productId).isEqualTo(1L)
            assertThat(command.userId).isEqualTo(2L)
            assertThat(command.caption).isEqualTo("캡션")
            assertThat(command.imageUrls).hasSize(1)
        }
    }

    @Nested
    @DisplayName("UpdateSnapRequest는")
    inner class UpdateSnapRequestTest {

        @Test
        fun `Command로 변환한다`() {
            val request = UpdateSnapRequest(caption = "변경된 캡션")

            val command = request.toCommand(1L, 2L)

            assertThat(command.snapId).isEqualTo(1L)
            assertThat(command.userId).isEqualTo(2L)
            assertThat(command.caption).isEqualTo("변경된 캡션")
        }
    }

    @Nested
    @DisplayName("SearchClickRequest는")
    inner class SearchClickRequestTest {

        @Test
        fun `Command로 변환한다`() {
            val request = SearchClickRequest(
                searchQuery = "스마트폰",
                clickedProductId = 1L,
                clickPosition = 3,
                totalResults = 50,
            )

            val command = request.toCommand(1L)

            assertThat(command.userId).isEqualTo(1L)
            assertThat(command.searchQuery).isEqualTo("스마트폰")
            assertThat(command.clickedProductId).isEqualTo(1L)
            assertThat(command.clickPosition).isEqualTo(3)
            assertThat(command.totalResults).isEqualTo(50)
        }

        @Test
        fun `프로퍼티 접근이 가능하다`() {
            val request = SearchClickRequest("스마트폰", 1L, 3, 50)
            assertThat(request.searchQuery).isEqualTo("스마트폰")
            assertThat(request.clickedProductId).isEqualTo(1L)
            assertThat(request.clickPosition).isEqualTo(3)
            assertThat(request.totalResults).isEqualTo(50)
            assertThat(request.toString()).contains("스마트폰")
            assertThat(request).isEqualTo(SearchClickRequest("스마트폰", 1L, 3, 50))
            assertThat(request.hashCode()).isEqualTo(SearchClickRequest("스마트폰", 1L, 3, 50).hashCode())
        }
    }

    @Nested
    @DisplayName("CreateDiscountPolicyRequest는")
    inner class CreateDiscountPolicyRequestTest {

        @Test
        fun `Command로 변환한다`() {
            val now = LocalDateTime.now()
            val request = CreateDiscountPolicyRequest(
                name = "할인",
                discountType = DiscountType.RATE,
                discountValue = 10,
                startAt = now,
                endAt = now.plusDays(1),
            )

            val command = request.toCommand(1L)

            assertThat(command.productId).isEqualTo(1L)
            assertThat(command.name).isEqualTo("할인")
            assertThat(command.discountType).isEqualTo(DiscountType.RATE)
            assertThat(command.discountValue).isEqualTo(10)
            assertThat(command.startAt).isEqualTo(now)
            assertThat(command.endAt).isEqualTo(now.plusDays(1))
        }

        @Test
        fun `프로퍼티 접근이 가능하다`() {
            val now = LocalDateTime.now()
            val request = CreateDiscountPolicyRequest("할인", DiscountType.RATE, 10, now, now.plusDays(1))
            assertThat(request.name).isEqualTo("할인")
            assertThat(request.discountType).isEqualTo(DiscountType.RATE)
            assertThat(request.discountValue).isEqualTo(10)
            assertThat(request.startAt).isEqualTo(now)
            assertThat(request.endAt).isEqualTo(now.plusDays(1))
            assertThat(request.toString()).contains("할인")
            assertThat(
                request,
            ).isEqualTo(CreateDiscountPolicyRequest("할인", DiscountType.RATE, 10, now, now.plusDays(1)))
        }
    }

    @Nested
    @DisplayName("CreatePromotionRequest는")
    inner class CreatePromotionRequestTest {

        @Test
        fun `Command로 변환한다`() {
            val now = LocalDateTime.now()
            val request = CreatePromotionRequest(
                productId = 1L,
                discountPrice = 8000,
                startAt = now,
                endAt = now.plusDays(1),
                type = PromotionType.CAMPAIGN,
                priority = 0,
                description = "캠페인",
            )

            val command = request.toCommand()

            assertThat(command.productId).isEqualTo(1L)
            assertThat(command.discountPrice).isEqualTo(8000)
            assertThat(command.startAt).isEqualTo(now)
            assertThat(command.endAt).isEqualTo(now.plusDays(1))
            assertThat(command.type).isEqualTo(PromotionType.CAMPAIGN)
            assertThat(command.priority).isEqualTo(0)
            assertThat(command.description).isEqualTo("캠페인")
        }

        @Test
        fun `프로퍼티 접근이 가능하다`() {
            val now = LocalDateTime.now()
            val request = CreatePromotionRequest(1L, 8000, now, now.plusDays(1), PromotionType.CAMPAIGN, 0, "캠페인")
            assertThat(request.productId).isEqualTo(1L)
            assertThat(request.discountPrice).isEqualTo(8000)
            assertThat(request.type).isEqualTo(PromotionType.CAMPAIGN)
            assertThat(request.priority).isEqualTo(0)
            assertThat(request.description).isEqualTo("캠페인")
            assertThat(request.toString()).contains("캠페인")
            assertThat(request).isEqualTo(
                CreatePromotionRequest(1L, 8000, now, now.plusDays(1), PromotionType.CAMPAIGN, 0, "캠페인"),
            )
        }
    }

    @Nested
    @DisplayName("CreateCategoryAttributeRequest는")
    inner class CreateCategoryAttributeRequestTest {

        @Test
        fun `Command로 변환한다`() {
            val request = CreateCategoryAttributeRequest(
                name = "색상",
                type = AttributeType.STRING,
                required = true,
                options = "빨강,파랑",
                ordering = 0,
            )

            val command = request.toCommand(1L)

            assertThat(command.categoryId).isEqualTo(1L)
            assertThat(command.name).isEqualTo("색상")
            assertThat(command.type).isEqualTo(AttributeType.STRING)
            assertThat(command.required).isTrue()
            assertThat(command.options).isEqualTo("빨강,파랑")
            assertThat(command.ordering).isEqualTo(0)
        }

        @Test
        fun `프로퍼티 접근이 가능하다`() {
            val request = CreateCategoryAttributeRequest("색상", AttributeType.STRING, true, "빨강,파랑", 0)
            assertThat(request.name).isEqualTo("색상")
            assertThat(request.type).isEqualTo(AttributeType.STRING)
            assertThat(request.required).isTrue()
            assertThat(request.options).isEqualTo("빨강,파랑")
            assertThat(request.ordering).isEqualTo(0)
            assertThat(request.toString()).contains("색상")
            assertThat(request).isEqualTo(CreateCategoryAttributeRequest("색상", AttributeType.STRING, true, "빨강,파랑", 0))
        }
    }

    @Nested
    @DisplayName("UpdateCategoryAttributeRequest는")
    inner class UpdateCategoryAttributeRequestTest {

        @Test
        fun `Command로 변환한다`() {
            val request = UpdateCategoryAttributeRequest(name = "변경", required = false, options = null, ordering = 1)

            val command = request.toCommand(1L)

            assertThat(command.attributeId).isEqualTo(1L)
            assertThat(command.name).isEqualTo("변경")
        }
    }

    @Nested
    @DisplayName("SetProductAttributeValuesRequest는")
    inner class SetProductAttributeValuesRequestTest {

        @Test
        fun `Command로 변환한다`() {
            val request = SetProductAttributeValuesRequest(
                attributes = listOf(
                    SetProductAttributeValuesRequest.AttributeValueRequest(attributeId = 1L, value = "빨강"),
                ),
            )

            val command = request.toCommand(1L)

            assertThat(command.productId).isEqualTo(1L)
            assertThat(command.attributes).hasSize(1)
            assertThat(command.attributes.first().attributeId).isEqualTo(1L)
        }
    }
}
