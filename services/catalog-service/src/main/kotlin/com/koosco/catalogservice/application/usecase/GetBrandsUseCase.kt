package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.port.BrandRepository
import com.koosco.catalogservice.application.result.BrandResult
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import org.springframework.transaction.annotation.Transactional

@UseCase
class GetBrandsUseCase(private val brandRepository: BrandRepository) {

    @Transactional(readOnly = true)
    fun getAll(): List<BrandResult> = brandRepository.findAll().map { BrandResult.from(it) }

    @Transactional(readOnly = true)
    fun getById(brandId: Long): BrandResult {
        val brand = brandRepository.findOrNull(brandId)
            ?: throw NotFoundException(CatalogErrorCode.BRAND_NOT_FOUND)
        return BrandResult.from(brand)
    }
}
