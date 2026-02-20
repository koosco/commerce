package com.koosco.catalogservice.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "content_review_like")
@IdClass(ReviewLikeId::class)
class ReviewLike(

    @Id
    @Column(name = "review_id")
    val reviewId: Long,

    @Id
    @Column(name = "user_id")
    val userId: Long,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
