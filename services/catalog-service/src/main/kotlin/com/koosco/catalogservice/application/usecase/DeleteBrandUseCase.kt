package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.DeleteBrandCommand
import com.koosco.catalogservice.application.port.BrandRepository
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import org.springframework.transaction.annotation.Transactional

@UseCase
class DeleteBrandUseCase(private val brandRepository: BrandRepository) {

    @Transactional
    fun execute(command: DeleteBrandCommand) {
        val brand = brandRepository.findOrNull(command.brandId)
            ?: throw NotFoundException(CatalogErrorCode.BRAND_NOT_FOUND)

        brandRepository.delete(brand)
    }
}
