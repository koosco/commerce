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

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true)
    val skus: MutableList<ProductSku> = mutableListOf(),

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true)
    val optionGroups: MutableList<ProductOptionGroup> = mutableListOf(),

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
        status: ProductStatus?,
        categoryId: Long?,
        thumbnailImageUrl: String?,
        brandId: Long?,
    ) {
        name?.let { this.name = it }
        description?.let { this.description = it }
        price?.let { this.price = it }
        status?.let { this.status = it }
        categoryId?.let { this.categoryId = it }
        thumbnailImageUrl?.let { this.thumbnailImageUrl = it }
        brandId?.let { this.brandId = it }
    }

    fun delete() {
        this.status = ProductStatus.DELETED
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
