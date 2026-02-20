package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.port.ReviewLikeRepository
import com.koosco.catalogservice.application.port.ReviewRepository
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.catalogservice.domain.entity.ReviewLike
import com.koosco.catalogservice.domain.entity.ReviewLikeId
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import org.springframework.transaction.annotation.Transactional

@UseCase
class ToggleReviewLikeUseCase(
    private val reviewRepository: ReviewRepository,
    private val reviewLikeRepository: ReviewLikeRepository,
) {

    @Transactional
    fun execute(reviewId: Long, userId: Long): Boolean {
        val review = reviewRepository.findByIdOrNull(reviewId)
            ?: throw NotFoundException(CatalogErrorCode.REVIEW_NOT_FOUND)

        val existing = reviewLikeRepository.findById(ReviewLikeId(reviewId, userId))

        return if (existing != null) {
            reviewLikeRepository.delete(existing)
            review.likeCount = maxOf(0, review.likeCount - 1)
            false
        } else {
            reviewLikeRepository.save(ReviewLike(reviewId = reviewId, userId = userId))
            review.likeCount += 1
            true
        }
    }
}
