package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.port.CategoryAttributeRepository
import com.koosco.catalogservice.application.port.ProductAttributeValueRepository
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.application.result.ProductAttributeValueInfo
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import org.springframework.transaction.annotation.Transactional

@UseCase
class GetProductAttributeValuesUseCase(
    private val productRepository: ProductRepository,
    private val categoryAttributeRepository: CategoryAttributeRepository,
    private val productAttributeValueRepository: ProductAttributeValueRepository,
) {

    @Transactional(readOnly = true)
    fun execute(productId: Long): List<ProductAttributeValueInfo> {
        productRepository.findOrNull(productId)
            ?: throw NotFoundException(CatalogErrorCode.PRODUCT_NOT_FOUND)

        val attributeValues = productAttributeValueRepository.findByProductId(productId)
        if (attributeValues.isEmpty()) return emptyList()

        val attributeIds = attributeValues.map { it.attributeId }.distinct()
        val attributeMap = attributeIds.mapNotNull { id ->
            categoryAttributeRepository.findOrNull(id)?.let { id to it }
        }.toMap()

        return attributeValues.mapNotNull { av ->
            attributeMap[av.attributeId]?.let { attr ->
                ProductAttributeValueInfo.from(av, attr)
            }
        }
    }
}
