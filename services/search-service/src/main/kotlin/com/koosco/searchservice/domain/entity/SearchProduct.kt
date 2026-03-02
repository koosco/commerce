package com.koosco.searchservice.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "search_product")
class SearchProduct(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(unique = true, nullable = false)
    val productId: Long,
    @Column(nullable = false)
    var name: String,
    var description: String? = null,
    @Column(nullable = false)
    var price: Long,
    @Column(nullable = false)
    var sellingPrice: Long,
    var categoryId: Long? = null,
    var categoryName: String? = null,
    var brandId: Long? = null,
    var brandName: String? = null,
    var thumbnailImageUrl: String? = null,
    @Column(nullable = false)
    var status: String,
    var averageRating: Double = 0.0,
    var reviewCount: Int = 0,
    var salesCount: Int = 0,
    var viewCount: Int = 0,
    var likeCount: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
