package com.koosco.catalogservice.product.application.usecase

import com.koosco.catalogservice.common.exception.CatalogErrorCode
import com.koosco.catalogservice.product.application.command.GetProductDetailCommand
import com.koosco.catalogservice.product.application.port.ProductRepository
import com.koosco.catalogservice.product.application.result.ProductInfo
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import org.springframework.transaction.annotation.Transactional

@UseCase
class GetProductDetailUseCase(private val productRepository: ProductRepository) {

    @Transactional(readOnly = true)
    fun execute(command: GetProductDetailCommand): ProductInfo {
        val product = productRepository.findByIdWithOptions(command.productId)
            ?: throw NotFoundException(CatalogErrorCode.PRODUCT_NOT_FOUND)

        return ProductInfo.from(product)
    }
}
