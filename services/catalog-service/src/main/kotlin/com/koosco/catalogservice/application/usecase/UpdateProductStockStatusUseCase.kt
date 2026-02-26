package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.domain.enums.ProductStatus
import com.koosco.common.core.annotation.UseCase
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.transaction.annotation.Transactional

@UseCase
class UpdateProductStockStatusUseCase(private val productRepository: ProductRepository) {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * 재고 소진 시 상품 상태를 OUT_OF_STOCK으로 변경
     * 이미 OUT_OF_STOCK이면 무시 (멱등성 보장)
     */
    @CacheEvict(cacheNames = ["productDetail"], allEntries = true)
    @Transactional
    fun markOutOfStock(skuId: String) {
        val product = productRepository.findBySkuId(skuId)
        if (product == null) {
            logger.warn("Product not found for skuId={}. Ignoring stock depleted event.", skuId)
            return
        }

        if (product.status == ProductStatus.OUT_OF_STOCK) {
            logger.info(
                "Product already OUT_OF_STOCK. productId={}, skuId={}",
                product.id,
                skuId,
            )
            return
        }

        if (!product.status.canTransitionTo(ProductStatus.OUT_OF_STOCK)) {
            logger.warn(
                "Cannot transition to OUT_OF_STOCK from {}. productId={}, skuId={}",
                product.status,
                product.id,
                skuId,
            )
            return
        }

        product.changeStatus(ProductStatus.OUT_OF_STOCK)

        logger.info(
            "Product marked as OUT_OF_STOCK. productId={}, skuId={}",
            product.id,
            skuId,
        )
    }

    /**
     * 재고 복구 시 상품 상태를 ACTIVE로 변경
     * 이미 ACTIVE이면 무시 (멱등성 보장)
     */
    @CacheEvict(cacheNames = ["productDetail"], allEntries = true)
    @Transactional
    fun markActive(skuId: String) {
        val product = productRepository.findBySkuId(skuId)
        if (product == null) {
            logger.warn("Product not found for skuId={}. Ignoring stock restored event.", skuId)
            return
        }

        if (product.status == ProductStatus.ACTIVE) {
            logger.info(
                "Product already ACTIVE. productId={}, skuId={}",
                product.id,
                skuId,
            )
            return
        }

        if (!product.status.canTransitionTo(ProductStatus.ACTIVE)) {
            logger.warn(
                "Cannot transition to ACTIVE from {}. productId={}, skuId={}",
                product.status,
                product.id,
                skuId,
            )
            return
        }

        product.changeStatus(ProductStatus.ACTIVE)

        logger.info(
            "Product marked as ACTIVE. productId={}, skuId={}",
            product.id,
            skuId,
        )
    }
}
