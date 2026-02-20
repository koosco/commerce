package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.GetProductDetailCommand
import com.koosco.catalogservice.application.port.BrandRepository
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.application.result.ProductInfo
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import org.springframework.cache.annotation.Cacheable
import org.springframework.transaction.annotation.Transactional

@UseCase
class GetProductDetailUseCase(
    private val productRepository: ProductRepository,
    private val brandRepository: BrandRepository,
) {

    @Cacheable(cacheNames = ["productDetail"], key = "#command.productId")
    @Transactional(readOnly = true)
    fun execute(command: GetProductDetailCommand): ProductInfo {
        val product = productRepository.findByIdWithOptions(command.productId)
            ?: throw NotFoundException(CatalogErrorCode.PRODUCT_NOT_FOUND)

        val brandName = product.brandId?.let { brandRepository.findOrNull(it)?.name }

        return ProductInfo.from(product, brandName)
    }
}
