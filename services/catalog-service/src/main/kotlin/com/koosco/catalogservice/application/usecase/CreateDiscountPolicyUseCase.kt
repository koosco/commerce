package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.CreateDiscountPolicyCommand
import com.koosco.catalogservice.application.port.DiscountPolicyRepository
import com.koosco.catalogservice.application.port.IntegrationEventProducer
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.application.result.DiscountPolicyResult
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.catalogservice.contract.outbound.PriceChangedEvent
import com.koosco.catalogservice.domain.entity.DiscountPolicy
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@UseCase
class CreateDiscountPolicyUseCase(
    private val productRepository: ProductRepository,
    private val discountPolicyRepository: DiscountPolicyRepository,
    private val integrationEventProducer: IntegrationEventProducer,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @CacheEvict(cacheNames = ["productDetail"], key = "#command.productId")
    @Transactional
    fun execute(command: CreateDiscountPolicyCommand): DiscountPolicyResult {
        val product = productRepository.findOrNull(command.productId)
            ?: throw NotFoundException(CatalogErrorCode.PRODUCT_NOT_FOUND)

        val now = LocalDateTime.now()
        val previousSellingPrice = product.calculateSellingPrice(now)

        val discountPolicy = DiscountPolicy.create(
            product = product,
            name = command.name,
            discountType = command.discountType,
            discountValue = command.discountValue,
            startAt = command.startAt,
            endAt = command.endAt,
        )

        val saved = discountPolicyRepository.save(discountPolicy)
        product.discountPolicies.add(saved)

        val newSellingPrice = product.calculateSellingPrice(now)

        if (previousSellingPrice != newSellingPrice) {
            publishPriceChangedEvents(product, previousSellingPrice, newSellingPrice, now)
        }

        logger.info(
            "Discount policy created: productId=${product.id}, policyId=${saved.id}, " +
                "type=${command.discountType}, value=${command.discountValue}",
        )

        return DiscountPolicyResult.from(saved, now)
    }

    private fun publishPriceChangedEvents(
        product: com.koosco.catalogservice.domain.entity.Product,
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
                    reason = PriceChangedEvent.REASON_DISCOUNT_APPLIED,
                    changedAt = now,
                ),
            )
        }
    }
}
