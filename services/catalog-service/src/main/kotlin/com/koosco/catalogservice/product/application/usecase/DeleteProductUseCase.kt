package com.koosco.catalogservice.product.application.usecase

import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.catalogservice.product.application.command.DeleteProductCommand
import com.koosco.catalogservice.product.application.port.ProductRepository
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import org.springframework.transaction.annotation.Transactional

@UseCase
class DeleteProductUseCase(private val productRepository: ProductRepository) {

    @Transactional
    fun execute(command: DeleteProductCommand) {
        val product = productRepository.findOrNull(command.productId)
            ?: throw NotFoundException(CatalogErrorCode.PRODUCT_NOT_FOUND)

        product.delete()
    }
}
