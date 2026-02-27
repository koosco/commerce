package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.domain.entity.Review
import com.koosco.catalogservice.domain.enums.ContentStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface JpaReviewRepository : JpaRepository<Review, Long> {

    fun findByProductIdAndStatusNot(productId: Long, status: ContentStatus, pageable: Pageable): Page<Review>

    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.productId = :productId AND r.status <> 'DELETED'")
    fun calculateAverageRating(productId: Long): Double

    fun countByProductIdAndStatusNot(productId: Long, status: ContentStatus): Int
}
