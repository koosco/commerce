package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.application.port.ReviewLikeRepository
import com.koosco.catalogservice.domain.entity.ReviewLike
import com.koosco.catalogservice.domain.entity.ReviewLikeId
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class ReviewLikeRepositoryAdapter(private val jpaReviewLikeRepository: JpaReviewLikeRepository) : ReviewLikeRepository {

    override fun findById(id: ReviewLikeId): ReviewLike? = jpaReviewLikeRepository.findByIdOrNull(id)

    override fun save(like: ReviewLike): ReviewLike = jpaReviewLikeRepository.save(like)

    override fun delete(like: ReviewLike) = jpaReviewLikeRepository.delete(like)
}
