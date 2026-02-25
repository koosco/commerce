package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.CreateSnapCommand
import com.koosco.catalogservice.application.port.CatalogIdempotencyRepository
import com.koosco.catalogservice.application.port.SnapRepository
import com.koosco.catalogservice.application.result.SnapResult
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.catalogservice.domain.entity.CatalogIdempotency
import com.koosco.catalogservice.domain.entity.Snap
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import org.springframework.transaction.annotation.Transactional

@UseCase
class CreateSnapUseCase(
    private val snapRepository: SnapRepository,
    private val catalogIdempotencyRepository: CatalogIdempotencyRepository,
) {

    @Transactional
    fun execute(command: CreateSnapCommand): SnapResult {
        if (command.idempotencyKey != null) {
            val existing = catalogIdempotencyRepository.findByIdempotencyKeyAndResourceType(
                command.idempotencyKey,
                "SNAP",
            )
            if (existing != null) {
                val snap = snapRepository.findByIdOrNull(existing.resourceId)
                    ?: throw NotFoundException(CatalogErrorCode.SNAP_NOT_FOUND)
                return SnapResult.from(snap)
            }
        }

        val snap = Snap.create(
            productId = command.productId,
            userId = command.userId,
            caption = command.caption,
        )

        command.imageUrls.forEachIndexed { index, url ->
            snap.addImage(url, index)
        }

        val saved = snapRepository.save(snap)

        if (command.idempotencyKey != null) {
            catalogIdempotencyRepository.save(
                CatalogIdempotency.create(command.idempotencyKey, "SNAP", saved.id!!),
            )
        }

        return SnapResult.from(saved)
    }
}
