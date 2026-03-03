package com.koosco.searchservice.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(
    name = "search_product",
    indexes = [
        Index(name = "idx_search_product_category_id", columnList = "category_id"),
        Index(name = "idx_search_product_brand_id", columnList = "brand_id"),
        Index(name = "idx_search_product_status", columnList = "status"),
    ],
)
class SearchProduct(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "product_id", nullable = false, unique = true)
    val productId: Long,
    @Column(nullable = false)
    var name: String,
    @Column(columnDefinition = "TEXT")
    var description: String? = null,
    @Column(nullable = false)
    var price: Long,
    @Column(name = "selling_price", nullable = false)
    var sellingPrice: Long,
    @Column(name = "category_id")
    var categoryId: Long? = null,
    @Column(name = "category_name")
    var categoryName: String? = null,
    @Column(name = "brand_id")
    var brandId: Long? = null,
    @Column(name = "brand_name")
    var brandName: String? = null,
    @Column(name = "thumbnail_image_url", length = 500)
    var thumbnailImageUrl: String? = null,
    @Column(nullable = false, length = 20)
    var status: String,
    @Column(name = "average_rating", nullable = false)
    var averageRating: Double = 0.0,
    @Column(name = "review_count", nullable = false)
    var reviewCount: Int = 0,
    @Column(name = "sales_count", nullable = false)
    var salesCount: Int = 0,
    @Column(name = "view_count", nullable = false)
    var viewCount: Int = 0,
    @Column(name = "like_count", nullable = false)
    var likeCount: Int = 0,
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }

    fun updateFrom(
        name: String,
        description: String?,
        price: Long,
        sellingPrice: Long,
        categoryId: Long?,
        categoryName: String?,
        brandId: Long?,
        brandName: String?,
        thumbnailImageUrl: String?,
        status: String,
    ) {
        this.name = name
        this.description = description
        this.price = price
        this.sellingPrice = sellingPrice
        this.categoryId = categoryId
        this.categoryName = categoryName
        this.brandId = brandId
        this.brandName = brandName
        this.thumbnailImageUrl = thumbnailImageUrl
        this.status = status
    }
}
