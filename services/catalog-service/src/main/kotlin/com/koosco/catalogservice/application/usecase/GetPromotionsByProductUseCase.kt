package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.GetPromotionsByProductCommand
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.application.port.PromotionRepository
import com.koosco.catalogservice.application.result.PromotionInfo
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@UseCase
class GetPromotionsByProductUseCase(
    private val promotionRepository: PromotionRepository,
    private val productRepository: ProductRepository,
) {

    @Transactional(readOnly = true)
    fun execute(command: GetPromotionsByProductCommand): List<PromotionInfo> {
        productRepository.findOrNull(command.productId)
            ?: throw NotFoundException(CatalogErrorCode.PRODUCT_NOT_FOUND)

        val now = LocalDateTime.now()
        val activePromotions = promotionRepository.findActiveByProductId(
            command.productId,
            now,
        )

        return activePromotions.map { PromotionInfo.from(it, now) }
    }
}
