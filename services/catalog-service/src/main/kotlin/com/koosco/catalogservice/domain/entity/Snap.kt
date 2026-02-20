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
    name = "content_snap",
    indexes = [
        Index(name = "idx_snap_product", columnList = "product_id"),
        Index(name = "idx_snap_user", columnList = "user_id"),
    ],
)
class Snap(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "snap_id")
    val id: Long? = null,

    @Column(name = "product_id", nullable = false)
    val productId: Long,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(columnDefinition = "TEXT")
    var caption: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ContentStatus = ContentStatus.VISIBLE,

    @Column(name = "like_count", nullable = false)
    var likeCount: Int = 0,

    @OneToMany(
        mappedBy = "snap",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    val images: MutableList<SnapImage> = mutableListOf(),

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun create(productId: Long, userId: Long, caption: String?): Snap = Snap(
            productId = productId,
            userId = userId,
            caption = caption,
        )
    }

    fun update(caption: String?) {
        caption?.let { this.caption = it }
        this.updatedAt = LocalDateTime.now()
    }

    fun softDelete() {
        this.status = ContentStatus.DELETED
        this.updatedAt = LocalDateTime.now()
    }

    fun addImage(imageUrl: String, ordering: Int = 0) {
        images.add(SnapImage(snap = this, imageUrl = imageUrl, ordering = ordering))
    }
}
