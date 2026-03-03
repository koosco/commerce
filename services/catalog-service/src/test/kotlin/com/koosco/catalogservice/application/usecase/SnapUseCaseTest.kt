package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.CreateSnapCommand
import com.koosco.catalogservice.application.command.DeleteSnapCommand
import com.koosco.catalogservice.application.command.UpdateSnapCommand
import com.koosco.catalogservice.application.port.CatalogIdempotencyRepository
import com.koosco.catalogservice.application.port.SnapLikeRepository
import com.koosco.catalogservice.application.port.SnapRepository
import com.koosco.catalogservice.application.usecase.snap.CreateSnapUseCase
import com.koosco.catalogservice.application.usecase.snap.DeleteSnapUseCase
import com.koosco.catalogservice.application.usecase.snap.GetSnapFeedUseCase
import com.koosco.catalogservice.application.usecase.snap.ToggleSnapLikeUseCase
import com.koosco.catalogservice.application.usecase.snap.UpdateSnapUseCase
import com.koosco.catalogservice.domain.entity.Snap
import com.koosco.catalogservice.domain.entity.SnapLike
import com.koosco.catalogservice.domain.entity.SnapLikeId
import com.koosco.catalogservice.domain.enums.ContentStatus
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
@DisplayName("Snap UseCase 테스트")
class SnapUseCaseTest {

    @Mock lateinit var snapRepository: SnapRepository

    @Mock lateinit var catalogIdempotencyRepository: CatalogIdempotencyRepository

    @Mock lateinit var snapLikeRepository: SnapLikeRepository

    private fun createSnap(id: Long = 1L, userId: Long = 1L): Snap = Snap(
        id = id,
        productId = 1L,
        userId = userId,
        caption = "캡션",
    )

    @Nested
    @DisplayName("CreateSnapUseCase는")
    inner class CreateSnapUseCaseTest {

        @Test
        fun `스냅을 생성한다`() {
            val useCase = CreateSnapUseCase(snapRepository, catalogIdempotencyRepository)
            val command = CreateSnapCommand(1L, 1L, "새 캡션")

            whenever(snapRepository.save(any())).thenAnswer { invocation ->
                val s = invocation.getArgument<Snap>(0)
                Snap(id = 1L, productId = s.productId, userId = s.userId, caption = s.caption)
            }

            val result = useCase.execute(command)

            assertThat(result.caption).isEqualTo("새 캡션")
        }

        @Test
        fun `이미지 URL을 포함하여 스냅을 생성한다`() {
            val useCase = CreateSnapUseCase(snapRepository, catalogIdempotencyRepository)
            val command = CreateSnapCommand(1L, 1L, "캡션", listOf("http://img1.jpg", "http://img2.jpg"))

            whenever(snapRepository.save(any())).thenAnswer { invocation ->
                val s = invocation.getArgument<Snap>(0)
                Snap(id = 1L, productId = s.productId, userId = s.userId, caption = s.caption).also {
                    s.images.forEach { img -> it.addImage(img.imageUrl, img.ordering) }
                }
            }

            val result = useCase.execute(command)

            assertThat(result.caption).isEqualTo("캡션")
        }

        @Test
        fun `멱등성 키가 있으면 기존 스냅을 반환한다`() {
            val useCase = CreateSnapUseCase(snapRepository, catalogIdempotencyRepository)
            val snap = createSnap()
            val existing = com.koosco.catalogservice.domain.entity.CatalogIdempotency.create("key-1", "SNAP", 1L)

            whenever(catalogIdempotencyRepository.findByIdempotencyKeyAndResourceType("key-1", "SNAP"))
                .thenReturn(existing)
            whenever(snapRepository.findByIdOrNull(1L)).thenReturn(snap)

            val result = useCase.execute(CreateSnapCommand(1L, 1L, "새 캡션"), idempotencyKey = "key-1")

            assertThat(result.snapId).isEqualTo(1L)
        }

        @Test
        fun `멱등성 키가 있지만 스냅이 없으면 예외를 던진다`() {
            val useCase = CreateSnapUseCase(snapRepository, catalogIdempotencyRepository)
            val existing = com.koosco.catalogservice.domain.entity.CatalogIdempotency.create("key-1", "SNAP", 1L)

            whenever(catalogIdempotencyRepository.findByIdempotencyKeyAndResourceType("key-1", "SNAP"))
                .thenReturn(existing)
            whenever(snapRepository.findByIdOrNull(1L)).thenReturn(null)

            assertThatThrownBy {
                useCase.execute(CreateSnapCommand(1L, 1L, "캡션"), idempotencyKey = "key-1")
            }.isInstanceOf(NotFoundException::class.java)
        }

        @Test
        fun `멱등성 키와 함께 스냅을 생성하면 멱등성 레코드를 저장한다`() {
            val useCase = CreateSnapUseCase(snapRepository, catalogIdempotencyRepository)
            val command = CreateSnapCommand(1L, 1L, "캡션")

            whenever(catalogIdempotencyRepository.findByIdempotencyKeyAndResourceType("key-1", "SNAP"))
                .thenReturn(null)
            whenever(snapRepository.save(any())).thenAnswer { invocation ->
                val s = invocation.getArgument<Snap>(0)
                Snap(id = 1L, productId = s.productId, userId = s.userId, caption = s.caption)
            }
            whenever(catalogIdempotencyRepository.save(any())).thenAnswer { it.getArgument(0) }

            val result = useCase.execute(command, idempotencyKey = "key-1")

            assertThat(result.caption).isEqualTo("캡션")
        }
    }

    @Nested
    @DisplayName("GetSnapFeedUseCase는")
    inner class GetSnapFeedUseCaseTest {

        @Test
        fun `스냅 피드를 조회한다`() {
            val useCase = GetSnapFeedUseCase(snapRepository)
            val page = PageImpl(listOf(createSnap()))

            whenever(snapRepository.findAll(PageRequest.of(0, 10))).thenReturn(page)

            val result = useCase.execute(PageRequest.of(0, 10))

            assertThat(result.content).hasSize(1)
        }
    }

    @Nested
    @DisplayName("UpdateSnapUseCase는")
    inner class UpdateSnapUseCaseTest {

        @Test
        fun `스냅을 수정한다`() {
            val useCase = UpdateSnapUseCase(snapRepository)
            val snap = createSnap()

            whenever(snapRepository.findByIdOrNull(1L)).thenReturn(snap)

            val result = useCase.execute(UpdateSnapCommand(1L, 1L, "변경된 캡션"))

            assertThat(result.caption).isEqualTo("변경된 캡션")
        }

        @Test
        fun `다른 사용자가 수정하면 ForbiddenException을 던진다`() {
            val useCase = UpdateSnapUseCase(snapRepository)
            val snap = createSnap(userId = 1L)

            whenever(snapRepository.findByIdOrNull(1L)).thenReturn(snap)

            assertThatThrownBy { useCase.execute(UpdateSnapCommand(1L, 999L, "변경")) }
                .isInstanceOf(ForbiddenException::class.java)
        }

        @Test
        fun `스냅이 없으면 예외를 던진다`() {
            val useCase = UpdateSnapUseCase(snapRepository)

            whenever(snapRepository.findByIdOrNull(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(UpdateSnapCommand(1L, 1L, "변경")) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("DeleteSnapUseCase는")
    inner class DeleteSnapUseCaseTest {

        @Test
        fun `스냅을 소프트 삭제한다`() {
            val useCase = DeleteSnapUseCase(snapRepository)
            val snap = createSnap()

            whenever(snapRepository.findByIdOrNull(1L)).thenReturn(snap)

            useCase.execute(DeleteSnapCommand(1L, 1L))

            assertThat(snap.status).isEqualTo(ContentStatus.DELETED)
        }

        @Test
        fun `다른 사용자가 삭제하면 ForbiddenException을 던진다`() {
            val useCase = DeleteSnapUseCase(snapRepository)
            val snap = createSnap(userId = 1L)

            whenever(snapRepository.findByIdOrNull(1L)).thenReturn(snap)

            assertThatThrownBy { useCase.execute(DeleteSnapCommand(1L, 999L)) }
                .isInstanceOf(ForbiddenException::class.java)
        }
    }

    @Nested
    @DisplayName("ToggleSnapLikeUseCase는")
    inner class ToggleSnapLikeUseCaseTest {

        @Test
        fun `좋아요를 추가한다`() {
            val useCase = ToggleSnapLikeUseCase(
                snapRepository,
                snapLikeRepository,
                catalogIdempotencyRepository,
            )
            val snap = createSnap()

            whenever(snapRepository.findByIdOrNull(1L)).thenReturn(snap)
            whenever(snapLikeRepository.findById(SnapLikeId(1L, 1L))).thenReturn(null)
            whenever(snapLikeRepository.save(any())).thenReturn(SnapLike(1L, 1L))

            val liked = useCase.execute(1L, 1L)

            assertThat(liked).isTrue()
            assertThat(snap.likeCount).isEqualTo(1)
        }

        @Test
        fun `좋아요를 취소한다`() {
            val useCase = ToggleSnapLikeUseCase(
                snapRepository,
                snapLikeRepository,
                catalogIdempotencyRepository,
            )
            val snap = createSnap()
            snap.likeCount = 1
            val existing = SnapLike(1L, 1L)

            whenever(snapRepository.findByIdOrNull(1L)).thenReturn(snap)
            whenever(snapLikeRepository.findById(SnapLikeId(1L, 1L))).thenReturn(existing)

            val liked = useCase.execute(1L, 1L)

            assertThat(liked).isFalse()
            assertThat(snap.likeCount).isEqualTo(0)
        }

        @Test
        fun `스냅이 없으면 예외를 던진다`() {
            val useCase = ToggleSnapLikeUseCase(
                snapRepository,
                snapLikeRepository,
                catalogIdempotencyRepository,
            )

            whenever(snapRepository.findByIdOrNull(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(1L, 1L) }
                .isInstanceOf(NotFoundException::class.java)
        }

        @Test
        fun `멱등성 키가 있고 이미 처리되었으면 기존 결과를 반환한다`() {
            val useCase = ToggleSnapLikeUseCase(
                snapRepository,
                snapLikeRepository,
                catalogIdempotencyRepository,
            )
            val existing = com.koosco.catalogservice.domain.entity.CatalogIdempotency.create("key-1", "SNAP_LIKE", 1L)

            whenever(catalogIdempotencyRepository.findByIdempotencyKeyAndResourceType("key-1", "SNAP_LIKE"))
                .thenReturn(existing)

            val result = useCase.execute(1L, 1L, "key-1")

            assertThat(result).isTrue()
        }

        @Test
        fun `멱등성 키와 함께 좋아요를 추가하면 멱등성 레코드를 저장한다`() {
            val useCase = ToggleSnapLikeUseCase(
                snapRepository,
                snapLikeRepository,
                catalogIdempotencyRepository,
            )
            val snap = createSnap()

            whenever(catalogIdempotencyRepository.findByIdempotencyKeyAndResourceType("key-1", "SNAP_LIKE"))
                .thenReturn(null)
            whenever(snapRepository.findByIdOrNull(1L)).thenReturn(snap)
            whenever(snapLikeRepository.findById(SnapLikeId(1L, 1L))).thenReturn(null)
            whenever(snapLikeRepository.save(any())).thenReturn(SnapLike(1L, 1L))
            whenever(catalogIdempotencyRepository.save(any())).thenAnswer { it.getArgument(0) }

            val result = useCase.execute(1L, 1L, "key-1")

            assertThat(result).isTrue()
        }
    }
}
