package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.GetPromotionPriceCommand
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.application.port.PromotionRepository
import com.koosco.catalogservice.application.result.PromotionPriceInfo
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.catalogservice.domain.service.PromotionPriceResolver
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import org.springframework.cache.annotation.Cacheable
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@UseCase
class GetPromotionPriceUseCase(
    private val promotionRepository: PromotionRepository,
    private val productRepository: ProductRepository,
) {

    @Cacheable(cacheNames = ["promotionPrice"], key = "#command.productId")
    @Transactional(readOnly = true)
    fun execute(command: GetPromotionPriceCommand): PromotionPriceInfo {
        val product = productRepository.findOrNull(command.productId)
            ?: throw NotFoundException(CatalogErrorCode.PRODUCT_NOT_FOUND)

        val now = LocalDateTime.now()
        val activePromotions = promotionRepository.findActiveByProductId(
            command.productId,
            now,
        )
        val discountPrice = PromotionPriceResolver.resolve(activePromotions)

        return PromotionPriceInfo(
            productId = product.id!!,
            originalPrice = product.price,
            discountPrice = discountPrice,
            finalPrice = discountPrice ?: product.price,
            hasActivePromotion = discountPrice != null,
        )
    }
}
