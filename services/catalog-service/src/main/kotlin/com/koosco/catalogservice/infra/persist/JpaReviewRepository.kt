package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.domain.entity.Review
import com.koosco.catalogservice.domain.enums.ContentStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface JpaReviewRepository : JpaRepository<Review, Long> {

    fun findByProductIdAndStatusNot(productId: Long, status: ContentStatus, pageable: Pageable): Page<Review>
}
