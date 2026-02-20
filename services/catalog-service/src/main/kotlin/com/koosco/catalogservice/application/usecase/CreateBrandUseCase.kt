package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.CreateBrandCommand
import com.koosco.catalogservice.application.port.BrandRepository
import com.koosco.catalogservice.application.result.BrandResult
import com.koosco.catalogservice.domain.entity.Brand
import com.koosco.common.core.annotation.UseCase
import org.springframework.transaction.annotation.Transactional

@UseCase
class CreateBrandUseCase(private val brandRepository: BrandRepository) {

    @Transactional
    fun execute(command: CreateBrandCommand): BrandResult {
        val brand = Brand(
            name = command.name,
            logoImageUrl = command.logoImageUrl,
        )
        val saved = brandRepository.save(brand)
        return BrandResult.from(saved)
    }
}
