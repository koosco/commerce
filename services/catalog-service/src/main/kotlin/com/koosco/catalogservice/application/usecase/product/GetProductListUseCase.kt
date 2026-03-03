package com.koosco.catalogservice.application.usecase.product

import com.koosco.catalogservice.application.command.GetProductListCommand
import com.koosco.catalogservice.application.port.BrandRepository
import com.koosco.catalogservice.application.port.CategoryRepository
import com.koosco.catalogservice.application.port.InventoryQueryPort
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.application.port.PromotionRepository
import com.koosco.catalogservice.application.port.UserBehaviorEventProducer
import com.koosco.catalogservice.application.result.ProductInfo
import com.koosco.catalogservice.domain.service.PromotionPriceResolver
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.event.BehaviorType
import com.koosco.common.core.event.UserBehaviorEvent
import org.springframework.data.domain.Page
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@UseCase
class GetProductListUseCase(
    private val productRepository: ProductRepository,
    private val brandRepository: BrandRepository,
    private val categoryRepository: CategoryRepository,
    private val promotionRepository: PromotionRepository,
    private val userBehaviorEventProducer: UserBehaviorEventProducer,
    private val inventoryQueryPort: InventoryQueryPort,
) {

    @Transactional(readOnly = true)
    fun execute(command: GetProductListCommand): Page<ProductInfo> {
        val resolvedCommand = command.categoryId?.let {
            val descendantIds = categoryRepository.findDescendantIds(it)
            command.copy(categoryIds = descendantIds)
        } ?: command

        val page = productRepository.search(resolvedCommand)

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

        // 실시간 재고 조회 (모든 상품의 SKU를 한 번에 조회)
        val allSkuIds = page.content.flatMap { product -> product.skus.map { it.skuId } }
        val availability = if (allSkuIds.isNotEmpty()) {
            inventoryQueryPort.getAvailability(allSkuIds)
        } else {
            emptyMap()
        }

        publishSearchEvent(command)

        return page.map { product ->
            val activePromotions = promotionMap[product.id] ?: emptyList()
            val discountPrice = PromotionPriceResolver.resolve(activePromotions)
            val productSkuIds = product.skus.map { it.skuId }
            val hasAvailableStock = productSkuIds.isEmpty() ||
                productSkuIds.any { skuId -> availability[skuId] ?: true }
            ProductInfo.from(product, brandMap[product.brandId]?.name, discountPrice, hasAvailableStock)
        }
    }

    private fun publishSearchEvent(command: GetProductListCommand) {
        val userId = command.userId ?: return
        val keyword = command.keyword ?: return

        userBehaviorEventProducer.publish(
            UserBehaviorEvent(
                userId = userId,
                behaviorType = BehaviorType.SEARCH,
                productId = null,
                searchQuery = keyword,
                metadata = buildMap {
                    command.categoryId?.let { put("categoryId", it.toString()) }
                    command.brandId?.let { put("brandId", it.toString()) }
                },
            ),
        )
    }
}
