package com.koosco.catalogservice.application.usecase.product

import com.koosco.catalogservice.application.command.GetProductDetailCommand
import com.koosco.catalogservice.application.port.BrandRepository
import com.koosco.catalogservice.application.port.InventoryQueryPort
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.application.port.PromotionRepository
import com.koosco.catalogservice.application.port.UserBehaviorEventProducer
import com.koosco.catalogservice.application.result.ProductInfo
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.catalogservice.domain.service.PromotionPriceResolver
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.event.BehaviorType
import com.koosco.common.core.event.UserBehaviorEvent
import com.koosco.common.core.exception.NotFoundException
import org.springframework.cache.annotation.Cacheable
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@UseCase
class GetProductDetailUseCase(
    private val productRepository: ProductRepository,
    private val brandRepository: BrandRepository,
    private val promotionRepository: PromotionRepository,
    private val userBehaviorEventProducer: UserBehaviorEventProducer,
    private val inventoryQueryPort: InventoryQueryPort,
) {

    @Cacheable(cacheNames = ["productDetail"], key = "#command.productId")
    @Transactional(readOnly = true)
    fun execute(command: GetProductDetailCommand): ProductInfo {
        val product = productRepository.findByIdWithOptions(command.productId)
            ?: throw NotFoundException(CatalogErrorCode.PRODUCT_NOT_FOUND)

        val brandName = product.brandId?.let { brandRepository.findOrNull(it)?.name }

        val now = LocalDateTime.now()
        val activePromotions = promotionRepository.findActiveByProductId(product.id!!, now)
        val discountPrice = PromotionPriceResolver.resolve(activePromotions)

        // 실시간 재고 조회
        val skuIds = product.skus.map { it.skuId }
        val availability = if (skuIds.isNotEmpty()) {
            inventoryQueryPort.getAvailability(skuIds)
        } else {
            emptyMap()
        }
        val hasAvailableStock = skuIds.isEmpty() || availability.values.any { it }

        publishViewEvent(command)

        return ProductInfo.from(product, brandName, discountPrice, hasAvailableStock)
    }

    private fun publishViewEvent(command: GetProductDetailCommand) {
        val userId = command.userId ?: return

        userBehaviorEventProducer.publish(
            UserBehaviorEvent(
                userId = userId,
                behaviorType = BehaviorType.VIEW,
                productId = command.productId,
                searchQuery = null,
            ),
        )
    }
}
