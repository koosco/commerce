package com.koosco.catalogservice.api.controller

import com.koosco.catalogservice.api.request.CreateReviewRequest
import com.koosco.catalogservice.api.request.UpdateReviewRequest
import com.koosco.catalogservice.api.response.LikeToggleResponse
import com.koosco.catalogservice.api.response.ReviewResponse
import com.koosco.catalogservice.application.command.DeleteReviewCommand
import com.koosco.catalogservice.application.usecase.CreateReviewUseCase
import com.koosco.catalogservice.application.usecase.DeleteReviewUseCase
import com.koosco.catalogservice.application.usecase.GetReviewsByProductUseCase
import com.koosco.catalogservice.application.usecase.ToggleReviewLikeUseCase
import com.koosco.catalogservice.application.usecase.UpdateReviewUseCase
import com.koosco.common.core.response.ApiResponse
import com.koosco.commonsecurity.resolver.AuthId
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Reviews", description = "Review management API")
@RestController
@RequestMapping("/api")
class ReviewController(
    private val createReviewUseCase: CreateReviewUseCase,
    private val getReviewsByProductUseCase: GetReviewsByProductUseCase,
    private val updateReviewUseCase: UpdateReviewUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase,
    private val toggleReviewLikeUseCase: ToggleReviewLikeUseCase,
) {

    @Operation(summary = "리뷰 작성")
    @PostMapping("/reviews")
    fun createReview(
        @AuthId userId: Long,
        @Valid @RequestBody request: CreateReviewRequest,
        @RequestHeader("Idempotency-Key", required = false) idempotencyKey: String?,
    ): ApiResponse<ReviewResponse> {
        val result = createReviewUseCase.execute(request.toCommand(userId), idempotencyKey)
        return ApiResponse.Companion.success(ReviewResponse.from(result))
    }

    @Operation(summary = "상품별 리뷰 조회")
    @GetMapping("/products/{productId}/reviews")
    fun getReviewsByProduct(
        @PathVariable productId: Long,
        @PageableDefault(size = 20, sort = ["createdAt"]) pageable: Pageable,
    ): ApiResponse<Page<ReviewResponse>> {
        val result = getReviewsByProductUseCase.execute(productId, pageable)
        return ApiResponse.Companion.success(result.map { ReviewResponse.from(it) })
    }

    @Operation(summary = "리뷰 수정")
    @PutMapping("/reviews/{reviewId}")
    fun updateReview(
        @AuthId userId: Long,
        @PathVariable reviewId: Long,
        @Valid @RequestBody request: UpdateReviewRequest,
    ): ApiResponse<ReviewResponse> {
        val result = updateReviewUseCase.execute(request.toCommand(reviewId, userId))
        return ApiResponse.Companion.success(ReviewResponse.from(result))
    }

    @Operation(summary = "리뷰 삭제")
    @DeleteMapping("/reviews/{reviewId}")
    fun deleteReview(@AuthId userId: Long, @PathVariable reviewId: Long): ApiResponse<Unit> {
        deleteReviewUseCase.execute(DeleteReviewCommand(reviewId, userId))
        return ApiResponse.Companion.success(Unit)
    }

    @Operation(summary = "리뷰 좋아요 토글")
    @PostMapping("/reviews/{reviewId}/like")
    fun toggleReviewLike(@AuthId userId: Long, @PathVariable reviewId: Long): ApiResponse<LikeToggleResponse> {
        val liked = toggleReviewLikeUseCase.execute(reviewId, userId)
        return ApiResponse.Companion.success(LikeToggleResponse(liked))
    }
}
