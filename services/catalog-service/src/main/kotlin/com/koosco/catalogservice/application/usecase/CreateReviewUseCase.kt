package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.CreateReviewCommand
import com.koosco.catalogservice.application.port.ReviewRepository
import com.koosco.catalogservice.application.result.ReviewResult
import com.koosco.catalogservice.domain.entity.Review
import com.koosco.common.core.annotation.UseCase
import org.springframework.transaction.annotation.Transactional

@UseCase
class CreateReviewUseCase(private val reviewRepository: ReviewRepository) {

    @Transactional
    fun execute(command: CreateReviewCommand): ReviewResult {
        val review = Review.create(
            productId = command.productId,
            userId = command.userId,
            orderItemId = command.orderItemId,
            title = command.title,
            content = command.content,
            rating = command.rating,
        )

        command.imageUrls.forEachIndexed { index, url ->
            review.addImage(url, index)
        }

        val saved = reviewRepository.save(review)
        return ReviewResult.from(saved)
    }
}
