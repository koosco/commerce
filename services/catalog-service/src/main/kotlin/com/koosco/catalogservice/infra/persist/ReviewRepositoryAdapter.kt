package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.application.port.ReviewRepository
import com.koosco.catalogservice.domain.entity.Review
import com.koosco.catalogservice.domain.enums.ContentStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class ReviewRepositoryAdapter(private val jpaReviewRepository: JpaReviewRepository) : ReviewRepository {

    override fun save(review: Review): Review = jpaReviewRepository.save(review)

    override fun findByIdOrNull(reviewId: Long): Review? = jpaReviewRepository.findByIdOrNull(reviewId)

    override fun findByProductId(productId: Long, pageable: Pageable): Page<Review> =
        jpaReviewRepository.findByProductIdAndStatusNot(productId, ContentStatus.DELETED, pageable)
}
