package com.koosco.catalogservice.application.port

import com.koosco.catalogservice.domain.entity.Review
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ReviewRepository {

    fun save(review: Review): Review

    fun findByIdOrNull(reviewId: Long): Review?

    fun findByProductId(productId: Long, pageable: Pageable): Page<Review>

    fun calculateAverageRating(productId: Long): Double

    fun countByProductId(productId: Long): Int
}
