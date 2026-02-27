package com.koosco.catalogservice.domain.entity

import com.koosco.catalogservice.domain.enums.ProductStatus
import com.koosco.catalogservice.domain.vo.OptionGroupCreateSpec
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Entity
@Table(name = "products")
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "product_code", nullable = false, unique = true, length = 50)
    val productCode: String,

    @Column(nullable = false)
    var name: String,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    /**
     * 상품 상세 페이지에 노출되는 기본 가격
     */
    @Column(nullable = false)
    var price: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: ProductStatus = ProductStatus.ACTIVE,

    @Column(name = "category_id")
    var categoryId: Long? = null,

    @Column(name = "thumbnail_image_url", length = 500)
    var thumbnailImageUrl: String? = null,

    @Column(name = "brand_id")
    var brandId: Long? = null,

    @Column(name = "average_rating", nullable = false)
    var averageRating: Double = 0.0,

    @Column(name = "review_count", nullable = false)
    var reviewCount: Int = 0,

    @Column(name = "view_count", nullable = false)
    var viewCount: Long = 0,

    @Column(name = "order_count", nullable = false)
    var orderCount: Long = 0,

    @Column(name = "sales_count", nullable = false)
    var salesCount: Long = 0,

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true)
    val skus: MutableList<ProductSku> = mutableListOf(),

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true)
    val optionGroups: MutableList<ProductOptionGroup> = mutableListOf(),

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true)
    val discountPolicies: MutableList<DiscountPolicy> = mutableListOf(),

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }

    fun update(
        name: String?,
        description: String?,
        price: Long?,
        categoryId: Long?,
        thumbnailImageUrl: String?,
        brandId: Long?,
    ) {
        name?.let { this.name = it }
        description?.let { this.description = it }
        price?.let { this.price = it }
        categoryId?.let { this.categoryId = it }
        thumbnailImageUrl?.let { this.thumbnailImageUrl = it }
        brandId?.let { this.brandId = it }
    }

    fun changeStatus(newStatus: ProductStatus) {
        require(status.canTransitionTo(newStatus)) {
            "상품 상태를 ${status.name}에서 ${newStatus.name}(으)로 변경할 수 없습니다."
        }

        if (newStatus == ProductStatus.ACTIVE) {
            validateForActivation()
        }

        this.status = newStatus
    }

    fun updateReviewStatistics(averageRating: Double, reviewCount: Int) {
        this.averageRating = averageRating
        this.reviewCount = reviewCount
    }

    fun incrementViewCount() {
        this.viewCount++
    }

    fun incrementOrderCount() {
        this.orderCount++
    }

    fun incrementSalesCount(quantity: Int) {
        this.salesCount += quantity
    }

    fun decrementSalesCount(quantity: Int) {
        this.salesCount = maxOf(0, this.salesCount - quantity)
    }

    fun delete() {
        if (status == ProductStatus.DRAFT) {
            this.status = ProductStatus.DELETED
            return
        }
        changeStatus(ProductStatus.DELETED)
    }

    private fun validateForActivation() {
        require(name.isNotBlank()) { "상품명이 비어있습니다." }
        require(price > 0) { "가격은 0보다 커야 합니다." }
        require(skus.isNotEmpty()) { "SKU가 1개 이상 있어야 활성화할 수 있습니다." }
    }

    /**
     * 적용 가능한 할인 정책 중 가장 유리한(할인 금액이 큰) 할인을 적용하여 판매가를 계산한다.
     * 적용 가능한 할인이 없으면 원래 가격을 반환한다.
     */
    fun calculateSellingPrice(now: LocalDateTime = LocalDateTime.now()): Long {
        val bestDiscount = discountPolicies
            .filter { it.isActiveAt(now) }
            .maxByOrNull { it.calculateDiscountAmount(price) }
            ?: return price

        return bestDiscount.calculateSellingPrice(price)
    }

    /**
     * 현재 적용 중인 최적 할인율(%)을 반환한다. 할인이 없으면 0.
     */
    fun calculateDiscountRate(now: LocalDateTime = LocalDateTime.now()): Int {
        val sellingPrice = calculateSellingPrice(now)
        if (sellingPrice >= price || price == 0L) return 0
        return ((price - sellingPrice) * 100 / price).toInt()
    }

    fun addSkus(skus: List<ProductSku>) {
        this.skus.addAll(skus)
        skus.forEach { sku ->
            sku.product = this
        }
    }

    companion object {
        fun create(
            name: String,
            description: String?,
            price: Long,
            status: ProductStatus,
            categoryId: Long?,
            categoryCode: String?,
            thumbnailImageUrl: String?,
            brandId: Long?,
            optionGroupSpecs: List<OptionGroupCreateSpec>,
        ): Product {
            val productCode = generate(categoryCode)

            val product = Product(
                productCode = productCode,
                name = name,
                description = description,
                price = price,
                status = status,
                categoryId = categoryId,
                thumbnailImageUrl = thumbnailImageUrl,
                brandId = brandId,
            )

            optionGroupSpecs.forEach { groupSpec ->
                val optionGroup =
                    ProductOptionGroup.create(
                        groupSpec.name,
                        groupSpec.ordering,
                        groupSpec.options,
                    )

                product.optionGroups.add(optionGroup)
                optionGroup.product = product
            }

            return product
        }

        fun generate(categoryCode: String?): String {
            // Category code에서 prefix 추출 (예: "electronics" -> "ELEC")
            val prefix = categoryCode?.uppercase()?.take(4) ?: "PRD"

            val date =
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            val shortId = UUID.randomUUID().toString().substring(0, 4).uppercase()

            return "$prefix-$date-$shortId"
        }
    }
}
