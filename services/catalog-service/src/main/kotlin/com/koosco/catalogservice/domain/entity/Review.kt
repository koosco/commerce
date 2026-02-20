package com.koosco.catalogservice.domain.entity

import com.koosco.catalogservice.domain.enums.ContentStatus
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(
    name = "content_review",
    indexes = [
        Index(name = "idx_review_product", columnList = "product_id"),
        Index(name = "idx_review_user", columnList = "user_id"),
    ],
)
class Review(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    val id: Long? = null,

    @Column(name = "product_id", nullable = false)
    val productId: Long,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "order_item_id")
    val orderItemId: Long? = null,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String,

    @Column(nullable = false)
    var rating: Int,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ContentStatus = ContentStatus.VISIBLE,

    @Column(name = "like_count", nullable = false)
    var likeCount: Int = 0,

    @OneToMany(
        mappedBy = "review",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    val images: MutableList<ReviewImage> = mutableListOf(),

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun create(
            productId: Long,
            userId: Long,
            orderItemId: Long?,
            title: String,
            content: String,
            rating: Int,
        ): Review {
            require(rating in 1..5) { "평점은 1~5 사이여야 합니다." }
            return Review(
                productId = productId,
                userId = userId,
                orderItemId = orderItemId,
                title = title,
                content = content,
                rating = rating,
            )
        }
    }

    fun update(title: String?, content: String?, rating: Int?) {
        title?.let { this.title = it }
        content?.let { this.content = it }
        rating?.let {
            require(it in 1..5) { "평점은 1~5 사이여야 합니다." }
            this.rating = it
        }
        this.updatedAt = LocalDateTime.now()
    }

    fun softDelete() {
        this.status = ContentStatus.DELETED
        this.updatedAt = LocalDateTime.now()
    }

    fun addImage(imageUrl: String, ordering: Int = 0) {
        images.add(ReviewImage(review = this, imageUrl = imageUrl, ordering = ordering))
    }
}
