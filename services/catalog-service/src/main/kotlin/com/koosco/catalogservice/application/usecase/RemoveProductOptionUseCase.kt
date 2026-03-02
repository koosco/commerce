package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.RemoveProductOptionCommand
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.application.result.ProductInfo
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.catalogservice.contract.outbound.ProductSkuDeactivatedEvent
import com.koosco.catalogservice.domain.vo.ProductOptions
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.event.IntegrationEventProducer
import com.koosco.common.core.exception.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@UseCase
class RemoveProductOptionUseCase(
    private val productRepository: ProductRepository,
    private val integrationEventProducer: IntegrationEventProducer,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun execute(command: RemoveProductOptionCommand): ProductInfo {
        val product = productRepository.findByIdWithOptions(command.productId)
            ?: throw NotFoundException(CatalogErrorCode.PRODUCT_NOT_FOUND)

        // 삭제 대상 옵션 찾기
        val targetOption = product.optionGroups
            .flatMap { group -> group.options.map { option -> group to option } }
            .find { (_, option) -> option.id == command.optionId }
            ?: throw NotFoundException(CatalogErrorCode.OPTION_NOT_FOUND)

        val (targetGroup, removedOption) = targetOption
        val groupName = targetGroup.name
        val optionName = removedOption.name

        // 해당 옵션을 포함하는 활성 SKU를 비활성화
        val deactivatedSkus = product.skus.filter { sku ->
            sku.isActive() && skuContainsOption(sku.optionValues, groupName, optionName)
        }

        deactivatedSkus.forEach { sku ->
            sku.deactivate()
        }

        // 옵션 그룹에서 옵션 제거
        targetGroup.options.remove(removedOption)

        val savedProduct = productRepository.save(product)

        // 비활성화된 SKU에 대한 이벤트 발행
        deactivatedSkus.forEach { sku ->
            integrationEventProducer.publish(
                ProductSkuDeactivatedEvent(
                    skuId = sku.skuId,
                    productId = savedProduct.id!!,
                    productCode = product.productCode,
                    optionValues = sku.optionValues,
                    deactivatedAt = LocalDateTime.now(),
                ),
            )
        }

        logger.info(
            "Product option removed: productId=${savedProduct.id}, " +
                "optionId=${command.optionId}, " +
                "deactivatedSkuCount=${deactivatedSkus.size}",
        )

        return ProductInfo.from(savedProduct)
    }

    private fun skuContainsOption(optionValuesJson: String, groupName: String, optionName: String): Boolean {
        val options = ProductOptions.fromJson(optionValuesJson).asMap()
        return options[groupName] == optionName
    }
}
