package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.port.SnapRepository
import com.koosco.catalogservice.application.result.SnapResult
import com.koosco.common.core.annotation.UseCase
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.transaction.annotation.Transactional

@UseCase
class GetSnapFeedUseCase(private val snapRepository: SnapRepository) {

    @Transactional(readOnly = true)
    fun execute(pageable: Pageable): Page<SnapResult> = snapRepository.findAll(pageable)
        .map { SnapResult.from(it) }
}
