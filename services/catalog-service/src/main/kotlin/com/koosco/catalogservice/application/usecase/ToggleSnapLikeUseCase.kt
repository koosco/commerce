package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.port.SnapLikeRepository
import com.koosco.catalogservice.application.port.SnapRepository
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.catalogservice.domain.entity.SnapLike
import com.koosco.catalogservice.domain.entity.SnapLikeId
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import org.springframework.transaction.annotation.Transactional

@UseCase
class ToggleSnapLikeUseCase(
    private val snapRepository: SnapRepository,
    private val snapLikeRepository: SnapLikeRepository,
) {

    @Transactional
    fun execute(snapId: Long, userId: Long): Boolean {
        val snap = snapRepository.findByIdOrNull(snapId)
            ?: throw NotFoundException(CatalogErrorCode.SNAP_NOT_FOUND)

        val existing = snapLikeRepository.findById(SnapLikeId(snapId, userId))

        return if (existing != null) {
            snapLikeRepository.delete(existing)
            snap.likeCount = maxOf(0, snap.likeCount - 1)
            false
        } else {
            snapLikeRepository.save(SnapLike(snapId = snapId, userId = userId))
            snap.likeCount += 1
            true
        }
    }
}
