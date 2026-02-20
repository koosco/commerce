package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.CreateSnapCommand
import com.koosco.catalogservice.application.port.SnapRepository
import com.koosco.catalogservice.application.result.SnapResult
import com.koosco.catalogservice.domain.entity.Snap
import com.koosco.common.core.annotation.UseCase
import org.springframework.transaction.annotation.Transactional

@UseCase
class CreateSnapUseCase(private val snapRepository: SnapRepository) {

    @Transactional
    fun execute(command: CreateSnapCommand): SnapResult {
        val snap = Snap.create(
            productId = command.productId,
            userId = command.userId,
            caption = command.caption,
        )

        command.imageUrls.forEachIndexed { index, url ->
            snap.addImage(url, index)
        }

        val saved = snapRepository.save(snap)
        return SnapResult.from(saved)
    }
}
