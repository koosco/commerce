package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.CreateBrandCommand
import com.koosco.catalogservice.application.port.BrandRepository
import com.koosco.catalogservice.application.port.CatalogIdempotencyRepository
import com.koosco.catalogservice.application.result.BrandResult
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.catalogservice.domain.entity.Brand
import com.koosco.catalogservice.domain.entity.CatalogIdempotency
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import org.springframework.transaction.annotation.Transactional

@UseCase
class CreateBrandUseCase(
    private val brandRepository: BrandRepository,
    private val catalogIdempotencyRepository: CatalogIdempotencyRepository,
) {

    @Transactional
    fun execute(command: CreateBrandCommand, idempotencyKey: String? = null): BrandResult {
        if (idempotencyKey != null) {
            val existing = catalogIdempotencyRepository.findByIdempotencyKeyAndResourceType(
                idempotencyKey,
                "BRAND",
            )
            if (existing != null) {
                val brand = brandRepository.findOrNull(existing.resourceId)
                    ?: throw NotFoundException(CatalogErrorCode.BRAND_NOT_FOUND)
                return BrandResult.from(brand)
            }
        }

        val brand = Brand(
            name = command.name,
            logoImageUrl = command.logoImageUrl,
        )
        val saved = brandRepository.save(brand)

        if (idempotencyKey != null) {
            catalogIdempotencyRepository.save(
                CatalogIdempotency.create(idempotencyKey, "BRAND", saved.id!!),
            )
        }

        return BrandResult.from(saved)
    }
}
