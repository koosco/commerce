package com.koosco.catalogservice.product.application.usecase

import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.catalogservice.product.application.command.UpdateProductCommand
import com.koosco.catalogservice.product.application.port.ProductRepository
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import org.springframework.cache.annotation.CacheEvict
import org.springframework.transaction.annotation.Transactional

@UseCase
class UpdateProductUseCase(private val productRepository: ProductRepository) {

    @CacheEvict(cacheNames = ["productDetail"], key = "#command.productId")
    @Transactional
    fun execute(command: UpdateProductCommand) {
        val product = productRepository.findOrNull(command.productId)
            ?: throw NotFoundException(CatalogErrorCode.PRODUCT_NOT_FOUND)

        product.update(
            name = command.name,
            description = command.description,
            price = command.price,
            status = command.status,
            categoryId = command.categoryId,
            thumbnailImageUrl = command.thumbnailImageUrl,
            brand = command.brand,
        )
    }
}
