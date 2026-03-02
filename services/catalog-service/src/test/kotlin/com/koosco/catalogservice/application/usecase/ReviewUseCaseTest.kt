package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.CreateReviewCommand
import com.koosco.catalogservice.application.command.DeleteReviewCommand
import com.koosco.catalogservice.application.command.UpdateReviewCommand
import com.koosco.catalogservice.application.port.CatalogIdempotencyRepository
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.application.port.ReviewLikeRepository
import com.koosco.catalogservice.application.port.ReviewRepository
import com.koosco.catalogservice.application.usecase.review.CreateReviewUseCase
import com.koosco.catalogservice.application.usecase.review.DeleteReviewUseCase
import com.koosco.catalogservice.application.usecase.review.GetReviewsByProductUseCase
import com.koosco.catalogservice.application.usecase.review.ToggleReviewLikeUseCase
import com.koosco.catalogservice.application.usecase.review.UpdateReviewUseCase
import com.koosco.catalogservice.domain.entity.Product
import com.koosco.catalogservice.domain.entity.Review
import com.koosco.catalogservice.domain.entity.ReviewLike
import com.koosco.catalogservice.domain.entity.ReviewLikeId
import com.koosco.catalogservice.domain.enums.ContentStatus
import com.koosco.catalogservice.domain.enums.ProductStatus
import com.koosco.common.core.exception.ForbiddenException
import com.koosco.common.core.exception.NotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

@ExtendWith(MockitoExtension::class)
@DisplayName("Review UseCase 테스트")
class ReviewUseCaseTest {

    @Mock lateinit var reviewRepository: ReviewRepository

    @Mock lateinit var productRepository: ProductRepository

    @Mock lateinit var catalogIdempotencyRepository: CatalogIdempotencyRepository

    @Mock lateinit var reviewLikeRepository: ReviewLikeRepository

    private fun createReview(id: Long = 1L, userId: Long = 1L): Review = Review(
        id = id,
        productId = 1L,
        userId = userId,
        title = "좋은 상품",
        content = "만족합니다",
        rating = 5,
    )

    private fun createProduct(): Product = Product(
        id = 1L,
        productCode = "TEST-001",
        name = "테스트",
        price = 10000,
        status = ProductStatus.ACTIVE,
    )

    @Nested
    @DisplayName("CreateReviewUseCase는")
    inner class CreateReviewUseCaseTest {

        @Test
        fun `리뷰를 생성한다`() {
            val useCase = CreateReviewUseCase(
                reviewRepository,
                productRepository,
                catalogIdempotencyRepository,
            )
            val command = CreateReviewCommand(1L, 1L, null, "좋아요", "내용", 5)

            whenever(reviewRepository.save(any())).thenAnswer { invocation ->
                val r = invocation.getArgument<Review>(0)
                Review(
                    id = 1L,
                    productId = r.productId,
                    userId = r.userId,
                    title = r.title,
                    content = r.content,
                    rating = r.rating,
                )
            }
            whenever(productRepository.findOrNull(1L)).thenReturn(createProduct())
            whenever(reviewRepository.calculateAverageRating(1L)).thenReturn(5.0)
            whenever(reviewRepository.countByProductId(1L)).thenReturn(1)

            val result = useCase.execute(command)

            assertThat(result.title).isEqualTo("좋아요")
        }

        @Test
        fun `이미지를 포함하여 리뷰를 생성한다`() {
            val useCase = CreateReviewUseCase(
                reviewRepository,
                productRepository,
                catalogIdempotencyRepository,
            )
            val command = CreateReviewCommand(1L, 1L, null, "좋아요", "내용", 5, listOf("http://img1.jpg"))

            whenever(reviewRepository.save(any())).thenAnswer { invocation ->
                val r = invocation.getArgument<Review>(0)
                Review(
                    id = 1L,
                    productId = r.productId,
                    userId = r.userId,
                    title = r.title,
                    content = r.content,
                    rating = r.rating,
                )
            }
            whenever(productRepository.findOrNull(1L)).thenReturn(createProduct())
            whenever(reviewRepository.calculateAverageRating(1L)).thenReturn(5.0)
            whenever(reviewRepository.countByProductId(1L)).thenReturn(1)

            val result = useCase.execute(command)

            assertThat(result.title).isEqualTo("좋아요")
        }

        @Test
        fun `멱등성 키가 있으면 기존 리뷰를 반환한다`() {
            val useCase = CreateReviewUseCase(
                reviewRepository,
                productRepository,
                catalogIdempotencyRepository,
            )
            val review = createReview()
            val existing = com.koosco.catalogservice.domain.entity.CatalogIdempotency.create("key-1", "REVIEW", 1L)

            whenever(catalogIdempotencyRepository.findByIdempotencyKeyAndResourceType("key-1", "REVIEW"))
                .thenReturn(existing)
            whenever(reviewRepository.findByIdOrNull(1L)).thenReturn(review)

            val result = useCase.execute(
                CreateReviewCommand(1L, 1L, null, "좋아요", "내용", 5),
                idempotencyKey = "key-1",
            )

            assertThat(result.reviewId).isEqualTo(1L)
        }

        @Test
        fun `멱등성 키가 있지만 리뷰가 없으면 예외를 던진다`() {
            val useCase = CreateReviewUseCase(
                reviewRepository,
                productRepository,
                catalogIdempotencyRepository,
            )
            val existing = com.koosco.catalogservice.domain.entity.CatalogIdempotency.create("key-1", "REVIEW", 1L)

            whenever(catalogIdempotencyRepository.findByIdempotencyKeyAndResourceType("key-1", "REVIEW"))
                .thenReturn(existing)
            whenever(reviewRepository.findByIdOrNull(1L)).thenReturn(null)

            assertThatThrownBy {
                useCase.execute(CreateReviewCommand(1L, 1L, null, "제목", "내용", 5), idempotencyKey = "key-1")
            }.isInstanceOf(NotFoundException::class.java)
        }

        @Test
        fun `멱등성 키와 함께 리뷰를 생성하면 멱등성 레코드를 저장한다`() {
            val useCase = CreateReviewUseCase(
                reviewRepository,
                productRepository,
                catalogIdempotencyRepository,
            )
            val command = CreateReviewCommand(1L, 1L, null, "좋아요", "내용", 5)

            whenever(catalogIdempotencyRepository.findByIdempotencyKeyAndResourceType("key-1", "REVIEW"))
                .thenReturn(null)
            whenever(reviewRepository.save(any())).thenAnswer { invocation ->
                val r = invocation.getArgument<Review>(0)
                Review(
                    id = 1L,
                    productId = r.productId,
                    userId = r.userId,
                    title = r.title,
                    content = r.content,
                    rating = r.rating,
                )
            }
            whenever(productRepository.findOrNull(1L)).thenReturn(createProduct())
            whenever(reviewRepository.calculateAverageRating(1L)).thenReturn(5.0)
            whenever(reviewRepository.countByProductId(1L)).thenReturn(1)
            whenever(catalogIdempotencyRepository.save(any())).thenAnswer { it.getArgument(0) }

            val result = useCase.execute(command, idempotencyKey = "key-1")

            assertThat(result.title).isEqualTo("좋아요")
        }
    }

    @Nested
    @DisplayName("GetReviewsByProductUseCase는")
    inner class GetReviewsByProductUseCaseTest {

        @Test
        fun `상품 리뷰 목록을 조회한다`() {
            val useCase = GetReviewsByProductUseCase(reviewRepository)
            val reviews = listOf(createReview())
            val page = PageImpl(reviews)

            whenever(reviewRepository.findByProductId(1L, PageRequest.of(0, 10))).thenReturn(page)

            val result = useCase.execute(1L, PageRequest.of(0, 10))

            assertThat(result.content).hasSize(1)
        }
    }

    @Nested
    @DisplayName("UpdateReviewUseCase는")
    inner class UpdateReviewUseCaseTest {

        @Test
        fun `리뷰를 수정한다`() {
            val useCase = UpdateReviewUseCase(reviewRepository, productRepository)
            val review = createReview()
            val command = UpdateReviewCommand(1L, 1L, "수정 제목", null, null)

            whenever(reviewRepository.findByIdOrNull(1L)).thenReturn(review)

            val result = useCase.execute(command)

            assertThat(result.title).isEqualTo("수정 제목")
        }

        @Test
        fun `다른 사용자가 수정하면 ForbiddenException을 던진다`() {
            val useCase = UpdateReviewUseCase(reviewRepository, productRepository)
            val review = createReview(userId = 1L)
            val command = UpdateReviewCommand(1L, 999L, "수정 제목", null, null)

            whenever(reviewRepository.findByIdOrNull(1L)).thenReturn(review)

            assertThatThrownBy { useCase.execute(command) }
                .isInstanceOf(ForbiddenException::class.java)
        }

        @Test
        fun `평점이 변경되면 상품 통계를 업데이트한다`() {
            val useCase = UpdateReviewUseCase(reviewRepository, productRepository)
            val review = createReview()
            val product = createProduct()
            val command = UpdateReviewCommand(1L, 1L, null, null, 3)

            whenever(reviewRepository.findByIdOrNull(1L)).thenReturn(review)
            whenever(productRepository.findOrNull(1L)).thenReturn(product)
            whenever(reviewRepository.calculateAverageRating(1L)).thenReturn(3.0)
            whenever(reviewRepository.countByProductId(1L)).thenReturn(1)

            useCase.execute(command)

            assertThat(product.averageRating).isEqualTo(3.0)
        }

        @Test
        fun `리뷰가 없으면 예외를 던진다`() {
            val useCase = UpdateReviewUseCase(reviewRepository, productRepository)

            whenever(reviewRepository.findByIdOrNull(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(UpdateReviewCommand(1L, 1L, null, null, null)) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("DeleteReviewUseCase는")
    inner class DeleteReviewUseCaseTest {

        @Test
        fun `리뷰를 소프트 삭제한다`() {
            val useCase = DeleteReviewUseCase(reviewRepository, productRepository)
            val review = createReview()
            val product = createProduct()

            whenever(reviewRepository.findByIdOrNull(1L)).thenReturn(review)
            whenever(productRepository.findOrNull(1L)).thenReturn(product)
            whenever(reviewRepository.calculateAverageRating(1L)).thenReturn(0.0)
            whenever(reviewRepository.countByProductId(1L)).thenReturn(0)

            useCase.execute(DeleteReviewCommand(1L, 1L))

            assertThat(review.status).isEqualTo(ContentStatus.DELETED)
        }

        @Test
        fun `다른 사용자가 삭제하면 ForbiddenException을 던진다`() {
            val useCase = DeleteReviewUseCase(reviewRepository, productRepository)
            val review = createReview(userId = 1L)

            whenever(reviewRepository.findByIdOrNull(1L)).thenReturn(review)

            assertThatThrownBy { useCase.execute(DeleteReviewCommand(1L, 999L)) }
                .isInstanceOf(ForbiddenException::class.java)
        }
    }

    @Nested
    @DisplayName("ToggleReviewLikeUseCase는")
    inner class ToggleReviewLikeUseCaseTest {

        @Test
        fun `좋아요를 추가한다`() {
            val useCase = ToggleReviewLikeUseCase(
                reviewRepository,
                reviewLikeRepository,
                catalogIdempotencyRepository,
            )
            val review = createReview()

            whenever(reviewRepository.findByIdOrNull(1L)).thenReturn(review)
            whenever(reviewLikeRepository.findById(ReviewLikeId(1L, 1L))).thenReturn(null)
            whenever(reviewLikeRepository.save(any())).thenReturn(ReviewLike(1L, 1L))

            val liked = useCase.execute(1L, 1L)

            assertThat(liked).isTrue()
            assertThat(review.likeCount).isEqualTo(1)
        }

        @Test
        fun `좋아요를 취소한다`() {
            val useCase = ToggleReviewLikeUseCase(
                reviewRepository,
                reviewLikeRepository,
                catalogIdempotencyRepository,
            )
            val review = createReview()
            review.likeCount = 1
            val existing = ReviewLike(1L, 1L)

            whenever(reviewRepository.findByIdOrNull(1L)).thenReturn(review)
            whenever(reviewLikeRepository.findById(ReviewLikeId(1L, 1L))).thenReturn(existing)

            val liked = useCase.execute(1L, 1L)

            assertThat(liked).isFalse()
            assertThat(review.likeCount).isEqualTo(0)
        }

        @Test
        fun `리뷰가 없으면 예외를 던진다`() {
            val useCase = ToggleReviewLikeUseCase(
                reviewRepository,
                reviewLikeRepository,
                catalogIdempotencyRepository,
            )

            whenever(reviewRepository.findByIdOrNull(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(1L, 1L) }
                .isInstanceOf(NotFoundException::class.java)
        }

        @Test
        fun `멱등성 키가 있으면 기존 결과를 반환한다`() {
            val useCase = ToggleReviewLikeUseCase(
                reviewRepository,
                reviewLikeRepository,
                catalogIdempotencyRepository,
            )
            val existing = com.koosco.catalogservice.domain.entity.CatalogIdempotency.create("key-1", "REVIEW_LIKE", 1L)

            whenever(catalogIdempotencyRepository.findByIdempotencyKeyAndResourceType("key-1", "REVIEW_LIKE"))
                .thenReturn(existing)

            val result = useCase.execute(1L, 1L, "key-1")

            assertThat(result).isTrue()
        }

        @Test
        fun `멱등성 키와 함께 좋아요를 추가하면 멱등성 레코드를 저장한다`() {
            val useCase = ToggleReviewLikeUseCase(
                reviewRepository,
                reviewLikeRepository,
                catalogIdempotencyRepository,
            )
            val review = createReview()

            whenever(catalogIdempotencyRepository.findByIdempotencyKeyAndResourceType("key-1", "REVIEW_LIKE"))
                .thenReturn(null)
            whenever(reviewRepository.findByIdOrNull(1L)).thenReturn(review)
            whenever(reviewLikeRepository.findById(ReviewLikeId(1L, 1L))).thenReturn(null)
            whenever(reviewLikeRepository.save(any())).thenReturn(ReviewLike(1L, 1L))
            whenever(catalogIdempotencyRepository.save(any())).thenAnswer { it.getArgument(0) }

            val result = useCase.execute(1L, 1L, "key-1")

            assertThat(result).isTrue()
        }
    }
}
