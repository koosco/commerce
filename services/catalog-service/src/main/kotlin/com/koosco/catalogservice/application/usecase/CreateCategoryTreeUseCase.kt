package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.CreateCategoryTreeCommand
import com.koosco.catalogservice.application.dto.CategoryTreeInfo
import com.koosco.catalogservice.application.port.CatalogIdempotencyRepository
import com.koosco.catalogservice.application.port.CategoryRepository
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.catalogservice.domain.entity.CatalogIdempotency
import com.koosco.catalogservice.domain.entity.Category
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import org.springframework.cache.annotation.CacheEvict
import org.springframework.transaction.annotation.Transactional

@UseCase
class CreateCategoryTreeUseCase(
    private val categoryRepository: CategoryRepository,
    private val catalogIdempotencyRepository: CatalogIdempotencyRepository,
) {

    @CacheEvict(cacheNames = ["categoryTree"], allEntries = true)
    @Transactional
    fun execute(command: CreateCategoryTreeCommand): CategoryTreeInfo {
        if (command.idempotencyKey != null) {
            val existing = catalogIdempotencyRepository.findByIdempotencyKeyAndResourceType(
                command.idempotencyKey,
                "CATEGORY_TREE",
            )
            if (existing != null) {
                val category = categoryRepository.findByIdOrNull(existing.resourceId)
                    ?: throw NotFoundException(CatalogErrorCode.CATEGORY_NOT_FOUND)
                return CategoryTreeInfo.from(category)
            }
        }

        val rootCategory = Category.createTree(command)

        categoryRepository.save(rootCategory)

        if (command.idempotencyKey != null) {
            catalogIdempotencyRepository.save(
                CatalogIdempotency.create(command.idempotencyKey, "CATEGORY_TREE", rootCategory.id!!),
            )
        }

        return CategoryTreeInfo.from(rootCategory)
    }
}
