package com.koosco.searchservice.application.usecase.sync

import com.koosco.common.core.annotation.UseCase
import com.koosco.searchservice.contract.inbound.catalog.ProductChangedEvent
import com.koosco.searchservice.contract.inbound.catalog.ProductDeletedEvent
import com.koosco.searchservice.domain.entity.SearchProduct
import com.koosco.searchservice.domain.repository.SearchProductRepository
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

@UseCase
class SearchProductSyncUseCase(private val searchProductRepository: SearchProductRepository) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun upsert(event: ProductChangedEvent) {
        val existing = searchProductRepository.findByProductId(event.productId)

        if (existing != null) {
            existing.name = event.name
            existing.description = event.description
            existing.price = event.price
            existing.sellingPrice = event.sellingPrice
            existing.categoryId = event.categoryId
            existing.categoryName = event.categoryName
            existing.brandId = event.brandId
            existing.brandName = event.brandName
            existing.thumbnailImageUrl = event.thumbnailImageUrl
            existing.status = event.status

            logger.info("Updated search product: productId={}", event.productId)
        } else {
            searchProductRepository.save(
                SearchProduct(
                    productId = event.productId,
                    name = event.name,
                    description = event.description,
                    price = event.price,
                    sellingPrice = event.sellingPrice,
                    categoryId = event.categoryId,
                    categoryName = event.categoryName,
                    brandId = event.brandId,
                    brandName = event.brandName,
                    thumbnailImageUrl = event.thumbnailImageUrl,
                    status = event.status,
                ),
            )

            logger.info("Created search product: productId={}", event.productId)
        }
    }

    @Transactional
    fun delete(event: ProductDeletedEvent) {
        val existing = searchProductRepository.findByProductId(event.productId)

        if (existing != null) {
            searchProductRepository.deleteByProductId(event.productId)
            logger.info("Deleted search product: productId={}", event.productId)
        } else {
            logger.info("Search product not found for deletion (idempotent skip): productId={}", event.productId)
        }
    }
}
