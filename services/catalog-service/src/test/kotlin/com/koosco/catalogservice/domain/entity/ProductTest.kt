package com.koosco.catalogservice.domain.entity

import com.koosco.catalogservice.domain.enums.DiscountType
import com.koosco.catalogservice.domain.enums.ProductStatus
import com.koosco.catalogservice.domain.enums.SkuStatus
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("Product 도메인 테스트")
class ProductTest {

    private fun createProduct(
        status: ProductStatus = ProductStatus.ACTIVE,
        price: Long = 10000,
        name: String = "테스트 상품",
    ): Product = Product(
        id = 1L,
        productCode = "TEST-20250101-ABCD",
        name = name,
        price = price,
        status = status,
    )

    private fun createSku(product: Product): ProductSku = ProductSku(
        id = 1L,
        skuId = "SKU-001",
        product = product,
        price = product.price,
        optionValues = """{"색상":"빨강"}""",
        status = SkuStatus.ACTIVE,
    )

    @Nested
    @DisplayName("changeStatus 메서드는")
    inner class ChangeStatusTest {

        @Test
        fun `DRAFT에서 ACTIVE로 전환할 수 있다`() {
            val product = createProduct(status = ProductStatus.DRAFT)
            product.skus.add(createSku(product))

            product.changeStatus(ProductStatus.ACTIVE)

            assertThat(product.status).isEqualTo(ProductStatus.ACTIVE)
        }

        @Test
        fun `ACTIVE에서 SUSPENDED로 전환할 수 있다`() {
            val product = createProduct(status = ProductStatus.ACTIVE)

            product.changeStatus(ProductStatus.SUSPENDED)

            assertThat(product.status).isEqualTo(ProductStatus.SUSPENDED)
        }

        @Test
        fun `ACTIVE에서 DELETED로 전환할 수 있다`() {
            val product = createProduct(status = ProductStatus.ACTIVE)

            product.changeStatus(ProductStatus.DELETED)

            assertThat(product.status).isEqualTo(ProductStatus.DELETED)
        }

        @Test
        fun `SUSPENDED에서 ACTIVE로 전환할 수 있다`() {
            val product = createProduct(status = ProductStatus.SUSPENDED)
            product.skus.add(createSku(product))

            product.changeStatus(ProductStatus.ACTIVE)

            assertThat(product.status).isEqualTo(ProductStatus.ACTIVE)
        }

        @Test
        fun `DELETED에서 다른 상태로 전환할 수 없다`() {
            val product = createProduct(status = ProductStatus.DELETED)

            assertThatThrownBy { product.changeStatus(ProductStatus.ACTIVE) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `DRAFT에서 SUSPENDED로 전환할 수 없다`() {
            val product = createProduct(status = ProductStatus.DRAFT)

            assertThatThrownBy { product.changeStatus(ProductStatus.SUSPENDED) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `ACTIVE로 전환 시 이름이 비어있으면 예외를 던진다`() {
            val product = createProduct(status = ProductStatus.DRAFT, name = "")
            product.skus.add(createSku(product))

            assertThatThrownBy { product.changeStatus(ProductStatus.ACTIVE) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `ACTIVE로 전환 시 가격이 0이면 예외를 던진다`() {
            val product = createProduct(status = ProductStatus.DRAFT, price = 0)
            product.skus.add(createSku(product))

            assertThatThrownBy { product.changeStatus(ProductStatus.ACTIVE) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `ACTIVE로 전환 시 SKU가 없으면 예외를 던진다`() {
            val product = createProduct(status = ProductStatus.DRAFT)

            assertThatThrownBy { product.changeStatus(ProductStatus.ACTIVE) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }
    }

    @Nested
    @DisplayName("update 메서드는")
    inner class UpdateTest {

        @Test
        fun `이름을 변경한다`() {
            val product = createProduct()

            product.update(
                name = "변경된 이름",
                description = null,
                price = null,
                categoryId = null,
                thumbnailImageUrl = null,
                brandId = null,
            )

            assertThat(product.name).isEqualTo("변경된 이름")
        }

        @Test
        fun `null 필드는 변경하지 않는다`() {
            val product = createProduct()
            val originalName = product.name

            product.update(
                name = null,
                description = null,
                price = null,
                categoryId = null,
                thumbnailImageUrl = null,
                brandId = null,
            )

            assertThat(product.name).isEqualTo(originalName)
        }

        @Test
        fun `여러 필드를 동시에 변경한다`() {
            val product = createProduct()

            product.update(
                name = "새 이름",
                description = "새 설명",
                price = 20000,
                categoryId = 5L,
                thumbnailImageUrl = "http://img.jpg",
                brandId = 3L,
            )

            assertThat(product.name).isEqualTo("새 이름")
            assertThat(product.description).isEqualTo("새 설명")
            assertThat(product.price).isEqualTo(20000)
            assertThat(product.categoryId).isEqualTo(5L)
            assertThat(product.thumbnailImageUrl).isEqualTo("http://img.jpg")
            assertThat(product.brandId).isEqualTo(3L)
        }
    }

    @Nested
    @DisplayName("calculateSellingPrice 메서드는")
    inner class CalculateSellingPriceTest {

        @Test
        fun `할인 정책이 없으면 원래 가격을 반환한다`() {
            val product = createProduct(price = 10000)

            val sellingPrice = product.calculateSellingPrice()

            assertThat(sellingPrice).isEqualTo(10000)
        }

        @Test
        fun `활성 할인 정책 중 가장 유리한 할인을 적용한다`() {
            val product = createProduct(price = 10000)
            val now = LocalDateTime.now()

            val policy1 = DiscountPolicy(
                id = 1L,
                product = product,
                name = "10% 할인",
                discountType = DiscountType.RATE,
                discountValue = 10,
                startAt = now.minusDays(1),
                endAt = now.plusDays(1),
            )
            val policy2 = DiscountPolicy(
                id = 2L,
                product = product,
                name = "20% 할인",
                discountType = DiscountType.RATE,
                discountValue = 20,
                startAt = now.minusDays(1),
                endAt = now.plusDays(1),
            )

            product.discountPolicies.addAll(listOf(policy1, policy2))

            val sellingPrice = product.calculateSellingPrice(now)

            assertThat(sellingPrice).isEqualTo(8000) // 20% 할인
        }

        @Test
        fun `만료된 할인 정책은 적용되지 않는다`() {
            val product = createProduct(price = 10000)
            val now = LocalDateTime.now()

            val expiredPolicy = DiscountPolicy(
                id = 1L,
                product = product,
                name = "만료된 할인",
                discountType = DiscountType.RATE,
                discountValue = 50,
                startAt = now.minusDays(10),
                endAt = now.minusDays(1),
            )

            product.discountPolicies.add(expiredPolicy)

            val sellingPrice = product.calculateSellingPrice(now)

            assertThat(sellingPrice).isEqualTo(10000)
        }
    }

    @Nested
    @DisplayName("calculateDiscountRate 메서드는")
    inner class CalculateDiscountRateTest {

        @Test
        fun `할인이 없으면 0을 반환한다`() {
            val product = createProduct(price = 10000)

            val rate = product.calculateDiscountRate()

            assertThat(rate).isEqualTo(0)
        }

        @Test
        fun `가격이 0이면 0을 반환한다`() {
            val product = createProduct(price = 0)

            val rate = product.calculateDiscountRate()

            assertThat(rate).isEqualTo(0)
        }

        @Test
        fun `할인율을 정확히 계산한다`() {
            val product = createProduct(price = 10000)
            val now = LocalDateTime.now()

            val policy = DiscountPolicy(
                id = 1L,
                product = product,
                name = "30% 할인",
                discountType = DiscountType.RATE,
                discountValue = 30,
                startAt = now.minusDays(1),
                endAt = now.plusDays(1),
            )

            product.discountPolicies.add(policy)

            val rate = product.calculateDiscountRate(now)

            assertThat(rate).isEqualTo(30)
        }
    }

    @Nested
    @DisplayName("delete 메서드는")
    inner class DeleteTest {

        @Test
        fun `DRAFT 상태에서 바로 DELETED로 변경한다`() {
            val product = createProduct(status = ProductStatus.DRAFT)

            product.delete()

            assertThat(product.status).isEqualTo(ProductStatus.DELETED)
        }

        @Test
        fun `ACTIVE 상태에서 DELETED로 변경한다`() {
            val product = createProduct(status = ProductStatus.ACTIVE)

            product.delete()

            assertThat(product.status).isEqualTo(ProductStatus.DELETED)
        }

        @Test
        fun `SUSPENDED 상태에서 DELETED로 변경한다`() {
            val product = createProduct(status = ProductStatus.SUSPENDED)

            product.delete()

            assertThat(product.status).isEqualTo(ProductStatus.DELETED)
        }
    }

    @Nested
    @DisplayName("addSkus 메서드는")
    inner class AddSkusTest {

        @Test
        fun `SKU를 추가하고 Product를 참조하도록 설정한다`() {
            val product = createProduct()
            val sku = ProductSku(
                skuId = "SKU-002",
                product = product,
                price = 10000,
                optionValues = "{}",
            )

            product.addSkus(listOf(sku))

            assertThat(product.skus).hasSize(1)
            assertThat(product.skus.first().product).isEqualTo(product)
        }
    }

    @Nested
    @DisplayName("통계 관련 메서드는")
    inner class StatisticsTest {

        @Test
        fun `updateReviewStatistics가 평점과 리뷰 수를 갱신한다`() {
            val product = createProduct()

            product.updateReviewStatistics(4.5, 10)

            assertThat(product.averageRating).isEqualTo(4.5)
            assertThat(product.reviewCount).isEqualTo(10)
        }

        @Test
        fun `incrementViewCount가 조회수를 증가시킨다`() {
            val product = createProduct()

            product.incrementViewCount()

            assertThat(product.viewCount).isEqualTo(1)
        }

        @Test
        fun `incrementOrderCount가 주문수를 증가시킨다`() {
            val product = createProduct()

            product.incrementOrderCount()

            assertThat(product.orderCount).isEqualTo(1)
        }

        @Test
        fun `incrementSalesCount가 판매수를 증가시킨다`() {
            val product = createProduct()

            product.incrementSalesCount(5)

            assertThat(product.salesCount).isEqualTo(5)
        }

        @Test
        fun `decrementSalesCount가 판매수를 감소시키되 0 미만으로 내려가지 않는다`() {
            val product = createProduct()
            product.incrementSalesCount(3)

            product.decrementSalesCount(5)

            assertThat(product.salesCount).isEqualTo(0)
        }
    }

    @Nested
    @DisplayName("create companion 메서드는")
    inner class CreateTest {

        @Test
        fun `상품을 생성한다`() {
            val product = Product.create(
                name = "새 상품",
                description = "설명",
                price = 5000,
                status = ProductStatus.DRAFT,
                categoryId = 1L,
                categoryCode = "electronics",
                thumbnailImageUrl = null,
                brandId = null,
                optionGroupSpecs = emptyList(),
            )

            assertThat(product.name).isEqualTo("새 상품")
            assertThat(product.price).isEqualTo(5000)
            assertThat(product.status).isEqualTo(ProductStatus.DRAFT)
            assertThat(product.productCode).startsWith("ELEC-")
        }

        @Test
        fun `카테고리 코드가 없으면 PRD 접두사를 사용한다`() {
            val product = Product.create(
                name = "상품",
                description = null,
                price = 1000,
                status = ProductStatus.DRAFT,
                categoryId = null,
                categoryCode = null,
                thumbnailImageUrl = null,
                brandId = null,
                optionGroupSpecs = emptyList(),
            )

            assertThat(product.productCode).startsWith("PRD-")
        }
    }
}
