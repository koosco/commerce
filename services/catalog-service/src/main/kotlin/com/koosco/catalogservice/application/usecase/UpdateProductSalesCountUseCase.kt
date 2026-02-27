package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.port.CatalogIdempotencyRepository
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.domain.entity.CatalogIdempotency
import com.koosco.common.core.annotation.UseCase
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

@UseCase
class UpdateProductSalesCountUseCase(
    private val productRepository: ProductRepository,
    private val idempotencyRepository: CatalogIdempotencyRepository,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * 주문 확정 시 해당 SKU의 상품 salesCount를 증가시킨다.
     * 멱등성: 동일 orderId에 대한 중복 처리를 방지한다.
     */
    @Transactional
    fun incrementSalesCount(orderId: Long, skuId: Long, quantity: Int) {
        val idempotencyKey = "order-confirmed:$orderId:$skuId"
        if (isDuplicate(idempotencyKey)) {
            logger.info(
                "Duplicate order confirmed event. orderId={}, skuId={}. Skipping.",
                orderId,
                skuId,
            )
            return
        }

        val product = productRepository.findBySkuId(skuId.toString())
        if (product == null) {
            logger.warn(
                "Product not found for skuId={}. Ignoring order confirmed event. orderId={}",
                skuId,
                orderId,
            )
            return
        }

        product.incrementSalesCount(quantity)

        idempotencyRepository.save(
            CatalogIdempotency.create(
                idempotencyKey = idempotencyKey,
                resourceType = RESOURCE_TYPE_SALES_COUNT,
                resourceId = product.id!!,
            ),
        )

        logger.info(
            "Incremented salesCount. productId={}, skuId={}, orderId={}, quantity={}, newSalesCount={}",
            product.id,
            skuId,
            orderId,
            quantity,
            product.salesCount,
        )
    }

    /**
     * 주문 취소 시 해당 SKU의 상품 salesCount를 감소시킨다.
     * 멱등성: 동일 orderId에 대한 중복 처리를 방지한다.
     */
    @Transactional
    fun decrementSalesCount(orderId: Long, skuId: Long, quantity: Int) {
        val idempotencyKey = "order-cancelled:$orderId:$skuId"
        if (isDuplicate(idempotencyKey)) {
            logger.info(
                "Duplicate order cancelled event. orderId={}, skuId={}. Skipping.",
                orderId,
                skuId,
            )
            return
        }

        val product = productRepository.findBySkuId(skuId.toString())
        if (product == null) {
            logger.warn(
                "Product not found for skuId={}. Ignoring order cancelled event. orderId={}",
                skuId,
                orderId,
            )
            return
        }

        product.decrementSalesCount(quantity)

        idempotencyRepository.save(
            CatalogIdempotency.create(
                idempotencyKey = idempotencyKey,
                resourceType = RESOURCE_TYPE_SALES_COUNT,
                resourceId = product.id!!,
            ),
        )

        logger.info(
            "Decremented salesCount. productId={}, skuId={}, orderId={}, quantity={}, newSalesCount={}",
            product.id,
            skuId,
            orderId,
            quantity,
            product.salesCount,
        )
    }

    private fun isDuplicate(idempotencyKey: String): Boolean =
        idempotencyRepository.findByIdempotencyKeyAndResourceType(
            idempotencyKey,
            RESOURCE_TYPE_SALES_COUNT,
        ) != null

    companion object {
        private const val RESOURCE_TYPE_SALES_COUNT = "SALES_COUNT"
    }
}
