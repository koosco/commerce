package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.UpdateReviewCommand
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.application.port.ReviewRepository
import com.koosco.catalogservice.application.result.ReviewResult
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.ForbiddenException
import com.koosco.common.core.exception.NotFoundException
import org.springframework.transaction.annotation.Transactional

@UseCase
class UpdateReviewUseCase(
    private val reviewRepository: ReviewRepository,
    private val productRepository: ProductRepository,
) {

    @Transactional
    fun execute(command: UpdateReviewCommand): ReviewResult {
        val review = reviewRepository.findByIdOrNull(command.reviewId)
            ?: throw NotFoundException(CatalogErrorCode.REVIEW_NOT_FOUND)

        if (review.userId != command.userId) {
            throw ForbiddenException(CatalogErrorCode.FORBIDDEN)
        }

        val oldRating = review.rating

        review.update(
            title = command.title,
            content = command.content,
            rating = command.rating,
        )

        if (command.rating != null && command.rating != oldRating) {
            updateProductReviewStatistics(review.productId)
        }

        return ReviewResult.from(review)
    }

    private fun updateProductReviewStatistics(productId: Long) {
        val product = productRepository.findOrNull(productId)
            ?: throw NotFoundException(CatalogErrorCode.PRODUCT_NOT_FOUND)
        val averageRating = reviewRepository.calculateAverageRating(productId)
        val reviewCount = reviewRepository.countByProductId(productId)
        product.updateReviewStatistics(averageRating, reviewCount)
    }
}
