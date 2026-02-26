package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.CreateReviewCommand
import com.koosco.catalogservice.application.port.CatalogIdempotencyRepository
import com.koosco.catalogservice.application.port.ReviewRepository
import com.koosco.catalogservice.application.result.ReviewResult
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.catalogservice.domain.entity.CatalogIdempotency
import com.koosco.catalogservice.domain.entity.Review
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import org.springframework.transaction.annotation.Transactional

@UseCase
class CreateReviewUseCase(
    private val reviewRepository: ReviewRepository,
    private val catalogIdempotencyRepository: CatalogIdempotencyRepository,
) {

    @Transactional
    fun execute(command: CreateReviewCommand, idempotencyKey: String? = null): ReviewResult {
        if (idempotencyKey != null) {
            val existing = catalogIdempotencyRepository.findByIdempotencyKeyAndResourceType(
                idempotencyKey,
                "REVIEW",
            )
            if (existing != null) {
                val review = reviewRepository.findByIdOrNull(existing.resourceId)
                    ?: throw NotFoundException(CatalogErrorCode.REVIEW_NOT_FOUND)
                return ReviewResult.from(review)
            }
        }

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

        if (idempotencyKey != null) {
            catalogIdempotencyRepository.save(
                CatalogIdempotency.create(idempotencyKey, "REVIEW", saved.id!!),
            )
        }

        return ReviewResult.from(saved)
    }
}
