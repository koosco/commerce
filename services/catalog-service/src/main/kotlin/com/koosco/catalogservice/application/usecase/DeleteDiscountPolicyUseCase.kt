package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.DeleteDiscountPolicyCommand
import com.koosco.catalogservice.application.port.DiscountPolicyRepository
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.catalogservice.contract.outbound.PriceChangedEvent
import com.koosco.catalogservice.domain.entity.Product
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.event.IntegrationEventProducer
import com.koosco.common.core.exception.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@UseCase
class DeleteDiscountPolicyUseCase(
    private val productRepository: ProductRepository,
    private val discountPolicyRepository: DiscountPolicyRepository,
    private val integrationEventProducer: IntegrationEventProducer,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @CacheEvict(cacheNames = ["productDetail"], key = "#command.productId")
    @Transactional
    fun execute(command: DeleteDiscountPolicyCommand) {
        val product = productRepository.findOrNull(command.productId)
            ?: throw NotFoundException(CatalogErrorCode.PRODUCT_NOT_FOUND)

        val policy = discountPolicyRepository.findOrNull(command.discountPolicyId)
            ?: throw NotFoundException(CatalogErrorCode.DISCOUNT_POLICY_NOT_FOUND)

        require(policy.product.id == product.id) {
            "할인 정책이 해당 상품에 속하지 않습니다."
        }

        val now = LocalDateTime.now()
        val previousSellingPrice = product.calculateSellingPrice(now)

        product.discountPolicies.remove(policy)
        discountPolicyRepository.delete(policy)

        val newSellingPrice = product.calculateSellingPrice(now)

        if (previousSellingPrice != newSellingPrice) {
            publishPriceChangedEvents(product, previousSellingPrice, newSellingPrice, now)
        }

        logger.info(
            "Discount policy deleted: productId=${product.id}, policyId=${command.discountPolicyId}",
        )
    }

    private fun publishPriceChangedEvents(
        product: Product,
        previousSellingPrice: Long,
        newSellingPrice: Long,
        now: LocalDateTime,
    ) {
        product.skus.filter { it.isActive() }.forEach { sku ->
            integrationEventProducer.publish(
                PriceChangedEvent(
                    skuId = sku.skuId,
                    productId = product.id!!,
                    previousPrice = previousSellingPrice,
                    newPrice = newSellingPrice,
                    reason = PriceChangedEvent.REASON_DISCOUNT_EXPIRED,
                    changedAt = now,
                ),
            )
        }
    }
}
