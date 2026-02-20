package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.UpdateSnapCommand
import com.koosco.catalogservice.application.port.SnapRepository
import com.koosco.catalogservice.application.result.SnapResult
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.ForbiddenException
import com.koosco.common.core.exception.NotFoundException
import org.springframework.transaction.annotation.Transactional

@UseCase
class UpdateSnapUseCase(private val snapRepository: SnapRepository) {

    @Transactional
    fun execute(command: UpdateSnapCommand): SnapResult {
        val snap = snapRepository.findByIdOrNull(command.snapId)
            ?: throw NotFoundException(CatalogErrorCode.SNAP_NOT_FOUND)

        if (snap.userId != command.userId) {
            throw ForbiddenException(CatalogErrorCode.FORBIDDEN)
        }

        snap.update(caption = command.caption)

        return SnapResult.from(snap)
    }
}
