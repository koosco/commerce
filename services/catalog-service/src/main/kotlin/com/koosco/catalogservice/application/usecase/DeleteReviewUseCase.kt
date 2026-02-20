package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.DeleteReviewCommand
import com.koosco.catalogservice.application.port.ReviewRepository
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.ForbiddenException
import com.koosco.common.core.exception.NotFoundException
import org.springframework.transaction.annotation.Transactional

@UseCase
class DeleteReviewUseCase(private val reviewRepository: ReviewRepository) {

    @Transactional
    fun execute(command: DeleteReviewCommand) {
        val review = reviewRepository.findByIdOrNull(command.reviewId)
            ?: throw NotFoundException(CatalogErrorCode.REVIEW_NOT_FOUND)

        if (review.userId != command.userId) {
            throw ForbiddenException(CatalogErrorCode.FORBIDDEN)
        }

        review.softDelete()
    }
}
