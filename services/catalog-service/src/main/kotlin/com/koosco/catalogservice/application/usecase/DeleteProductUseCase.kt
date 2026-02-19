package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.DeleteProductCommand
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import org.springframework.cache.annotation.CacheEvict
import org.springframework.transaction.annotation.Transactional

@UseCase
class DeleteProductUseCase(private val productRepository: ProductRepository) {

    @CacheEvict(cacheNames = ["productDetail"], key = "#command.productId")
    @Transactional
    fun execute(command: DeleteProductCommand) {
        val product = productRepository.findOrNull(command.productId)
            ?: throw NotFoundException(CatalogErrorCode.PRODUCT_NOT_FOUND)

        product.delete()
    }
}
