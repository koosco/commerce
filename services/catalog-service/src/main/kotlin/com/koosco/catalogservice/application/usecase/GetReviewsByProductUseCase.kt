package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.port.ReviewRepository
import com.koosco.catalogservice.application.result.ReviewResult
import com.koosco.common.core.annotation.UseCase
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.transaction.annotation.Transactional

@UseCase
class GetReviewsByProductUseCase(private val reviewRepository: ReviewRepository) {

    @Transactional(readOnly = true)
    fun execute(productId: Long, pageable: Pageable): Page<ReviewResult> =
        reviewRepository.findByProductId(productId, pageable)
            .map { ReviewResult.from(it) }
}
