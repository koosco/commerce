package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.CreateProductCommand
import com.koosco.catalogservice.application.contract.outbound.ProductSkuCreatedEvent
import com.koosco.catalogservice.application.port.CategoryRepository
import com.koosco.catalogservice.application.port.IntegrationEventProducer
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.application.result.ProductInfo
import com.koosco.catalogservice.domain.entity.Product
import com.koosco.catalogservice.domain.service.ProductValidator
import com.koosco.catalogservice.domain.service.SkuGenerator
import com.koosco.catalogservice.domain.vo.CreateOptionSpec
import com.koosco.catalogservice.domain.vo.OptionGroupCreateSpec
import com.koosco.common.core.annotation.UseCase
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@UseCase
class CreateProductUseCase(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val skuGenerator: SkuGenerator,
    private val productValidator: ProductValidator,
    private val integrationEventProducer: IntegrationEventProducer,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun execute(command: CreateProductCommand): ProductInfo {
        // Category 조회 및 code 추출
        val categoryCode = command.categoryId?.let { categoryId ->
            categoryRepository.findByIdOrNull(categoryId)?.code
        }

        val optionSpec = command.optionGroups.map { group ->
            OptionGroupCreateSpec(
                name = group.name,
                ordering = group.ordering,
                options = group.options.map { option ->
                    CreateOptionSpec(
                        name = option.name,
                        additionalPrice = option.additionalPrice,
                        ordering = option.ordering,
                    )
                },
            )
        }

        // SKU 개수 검증
        productValidator.validateSkuCount(optionSpec)
        productValidator.validateOptionGroupStructure(optionSpec)

        val product = Product.create(
            name = command.name,
            description = command.description,
            price = command.price,
            status = command.status,
            categoryId = command.categoryId,
            categoryCode = categoryCode,
            thumbnailImageUrl = command.thumbnailImageUrl,
            brand = command.brand,
            optionGroupSpecs = optionSpec,
        )

        // SKU 생성 및 추가
        skuGenerator.generateSkus(product)

        // 상품 정보 저장 (SKU도 함께 저장)
        val savedProduct = productRepository.save(product)

        // Product 생성 도메인 이벤트 발행
        logger.info(
            "Product created: productId=${savedProduct.id}, " +
                "skuCount=${savedProduct.skus.size}, ",
        )

        product.skus.forEach {
            integrationEventProducer.publish(
                ProductSkuCreatedEvent(
                    skuId = it.skuId,
                    productId = savedProduct.id!!,
                    productCode = product.productCode,
                    price = it.price,
                    optionValues = it.optionValues,
                    initialQuantity = 0,
                    createdAt = LocalDateTime.now(),
                ),
            )
        }

        return ProductInfo.from(savedProduct)
    }
}
