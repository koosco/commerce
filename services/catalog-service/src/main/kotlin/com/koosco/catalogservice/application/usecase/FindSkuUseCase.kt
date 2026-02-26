package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.FindSkuCommand
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.domain.entity.ProductSku
import com.koosco.catalogservice.domain.vo.ProductOptions
import com.koosco.common.core.annotation.UseCase
import org.springframework.transaction.annotation.Transactional

@UseCase
class FindSkuUseCase(private val productRepository: ProductRepository) {

    @Transactional(readOnly = true)
    fun execute(command: FindSkuCommand): ProductSku {
        // Product 조회 (SKU도 함께)
        val product = productRepository.findOrNull(command.productId)
            ?: throw IllegalArgumentException("Product not found: ${command.productId}")

        // 입력받은 옵션을 VO로 변환
        val requestedOptions = ProductOptions.from(command.options)

        // 일치하는 활성 SKU 찾기 - 객체 비교
        val sku = product.skus.find { sku ->
            sku.isActive() &&
                ProductOptions.fromJson(sku.optionValues) == requestedOptions
        } ?: throw IllegalArgumentException(
            "No SKU found for options: ${command.options}. " +
                "Available SKUs: ${product.skus.map { productSku ->
                    ProductOptions.fromJson(productSku.optionValues).asMap()
                }}",
        )

        return sku
    }
}
