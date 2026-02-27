package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.SetProductAttributeValuesCommand
import com.koosco.catalogservice.application.port.CategoryAttributeRepository
import com.koosco.catalogservice.application.port.ProductAttributeValueRepository
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.application.result.ProductAttributeValueInfo
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.catalogservice.domain.entity.ProductAttributeValue
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import org.springframework.transaction.annotation.Transactional

@UseCase
class SetProductAttributeValuesUseCase(
    private val productRepository: ProductRepository,
    private val categoryAttributeRepository: CategoryAttributeRepository,
    private val productAttributeValueRepository: ProductAttributeValueRepository,
) {

    @Transactional
    fun execute(command: SetProductAttributeValuesCommand): List<ProductAttributeValueInfo> {
        val product = productRepository.findOrNull(command.productId)
            ?: throw NotFoundException(CatalogErrorCode.PRODUCT_NOT_FOUND)

        // Load attribute definitions for validation
        val attributeIds = command.attributes.map { it.attributeId }
        val attributeMap = attributeIds.mapNotNull { id ->
            categoryAttributeRepository.findOrNull(id)?.let { id to it }
        }.toMap()

        // Validate all attribute IDs exist
        command.attributes.forEach { spec ->
            val attribute = attributeMap[spec.attributeId]
                ?: throw NotFoundException(CatalogErrorCode.ATTRIBUTE_NOT_FOUND)
            attribute.validateValue(spec.value)
        }

        // Delete existing attribute values and replace with new ones
        productAttributeValueRepository.deleteByProductId(product.id!!)

        val attributeValues = command.attributes.map { spec ->
            ProductAttributeValue.create(
                productId = product.id!!,
                attributeId = spec.attributeId,
                value = spec.value,
            )
        }

        val saved = productAttributeValueRepository.saveAll(attributeValues)

        return saved.map { av ->
            ProductAttributeValueInfo.from(av, attributeMap[av.attributeId]!!)
        }
    }
}
