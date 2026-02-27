package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.GetProductListCommand
import com.koosco.catalogservice.application.port.BrandRepository
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.application.port.PromotionRepository
import com.koosco.catalogservice.application.result.ProductInfo
import com.koosco.catalogservice.domain.service.PromotionPriceResolver
import com.koosco.common.core.annotation.UseCase
import org.springframework.data.domain.Page
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@UseCase
class GetProductListUseCase(
    private val productRepository: ProductRepository,
    private val brandRepository: BrandRepository,
    private val promotionRepository: PromotionRepository,
) {

    @Transactional(readOnly = true)
    fun execute(command: GetProductListCommand): Page<ProductInfo> {
        val page = productRepository.search(command)

        val brandIds = page.content.mapNotNull { it.brandId }.distinct()
        val brandMap = if (brandIds.isNotEmpty()) {
            brandRepository.findAllByIdIn(brandIds).associateBy { it.id }
        } else {
            emptyMap()
        }

        val productIds = page.content.mapNotNull { it.id }
        val now = LocalDateTime.now()
        val promotionMap = if (productIds.isNotEmpty()) {
            promotionRepository.findActiveByProductIds(productIds, now)
                .groupBy { it.productId }
        } else {
            emptyMap()
        }

        return page.map { product ->
            val activePromotions = promotionMap[product.id] ?: emptyList()
            val discountPrice = PromotionPriceResolver.resolve(activePromotions)
            ProductInfo.from(product, brandMap[product.brandId]?.name, discountPrice)
        }
    }
}
