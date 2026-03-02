package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.AddProductOptionCommand
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.application.result.ProductInfo
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.catalogservice.contract.outbound.ProductSkuCreatedEvent
import com.koosco.catalogservice.domain.entity.ProductOption
import com.koosco.catalogservice.domain.entity.ProductSku
import com.koosco.catalogservice.domain.vo.ProductOptions
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.event.IntegrationEventProducer
import com.koosco.common.core.exception.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@UseCase
class AddProductOptionUseCase(
    private val productRepository: ProductRepository,
    private val integrationEventProducer: IntegrationEventProducer,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun execute(command: AddProductOptionCommand): ProductInfo {
        val product = productRepository.findByIdWithOptions(command.productId)
            ?: throw NotFoundException(CatalogErrorCode.PRODUCT_NOT_FOUND)

        val targetGroup = product.optionGroups.find { it.id == command.optionGroupId }
            ?: throw NotFoundException(CatalogErrorCode.OPTION_NOT_FOUND)

        // 기존 활성 SKU의 옵션 조합 수집
        val existingCombinations = product.skus
            .filter { it.isActive() }
            .map { ProductOptions.fromJson(it.optionValues) }
            .toSet()

        // 새 옵션 값을 옵션 그룹에 추가
        command.options.forEach { spec ->
            val option = ProductOption(
                name = spec.name,
                additionalPrice = spec.additionalPrice,
                ordering = spec.ordering,
            )
            targetGroup.addOption(option)
        }

        // 새로운 전체 조합 생성
        val allCombinations = generateAllCombinations(product)

        // diff: 새로 생성해야 할 조합
        val newCombinations = allCombinations.filter { (options, _) ->
            options !in existingCombinations
        }

        // 새 SKU 생성
        val newSkus = newCombinations.map { (options, additionalPrice) ->
            ProductSku.create(
                product = product,
                options = options.asMap(),
                price = product.price + additionalPrice,
            )
        }

        product.addSkus(newSkus)

        val savedProduct = productRepository.save(product)

        // 새 SKU에 대한 이벤트 발행
        newSkus.forEach { sku ->
            integrationEventProducer.publish(
                ProductSkuCreatedEvent(
                    skuId = sku.skuId,
                    productId = savedProduct.id!!,
                    productCode = product.productCode,
                    price = sku.price,
                    optionValues = sku.optionValues,
                    initialQuantity = 0,
                    createdAt = LocalDateTime.now(),
                ),
            )
        }

        logger.info(
            "Product option added: productId=${savedProduct.id}, " +
                "optionGroupId=${command.optionGroupId}, " +
                "newSkuCount=${newSkus.size}",
        )

        return ProductInfo.from(savedProduct)
    }

    /**
     * 현재 옵션 그룹 기준으로 모든 조합을 생성합니다.
     * @return Pair<ProductOptions, additionalPrice>
     */
    private fun generateAllCombinations(
        product: com.koosco.catalogservice.domain.entity.Product,
    ): List<Pair<ProductOptions, Long>> {
        if (product.optionGroups.isEmpty()) return emptyList()

        val groups = product.optionGroups
            .sortedBy { it.ordering }
            .map { group ->
                group.options
                    .sortedBy { it.ordering }
                    .map { option -> Triple(group.name, option.name, option.additionalPrice) }
            }

        return cartesianProduct(groups).map { combination ->
            val optionsMap = combination.associate { (groupName, optionName, _) ->
                groupName to optionName
            }
            val additionalPrice = combination.sumOf { (_, _, price) -> price }
            ProductOptions.from(optionsMap) to additionalPrice
        }
    }

    private fun <T> cartesianProduct(lists: List<List<T>>): List<List<T>> {
        if (lists.isEmpty()) return listOf(emptyList())
        if (lists.size == 1) return lists[0].map { listOf(it) }

        val result = mutableListOf<List<T>>()
        val rest = cartesianProduct(lists.drop(1))

        for (item in lists[0]) {
            for (r in rest) {
                result.add(listOf(item) + r)
            }
        }

        return result
    }
}
