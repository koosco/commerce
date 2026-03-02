package com.koosco.catalogservice.application.usecase.product

import com.koosco.catalogservice.application.command.UpdateProductCommand
import com.koosco.catalogservice.application.port.BrandRepository
import com.koosco.catalogservice.application.port.CategoryRepository
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.catalogservice.contract.outbound.ProductChangedEvent
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.event.IntegrationEventProducer
import com.koosco.common.core.exception.NotFoundException
import org.springframework.cache.annotation.CacheEvict
import org.springframework.transaction.annotation.Transactional

@UseCase
class UpdateProductUseCase(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val brandRepository: BrandRepository,
    private val integrationEventProducer: IntegrationEventProducer,
) {

    @CacheEvict(cacheNames = ["productDetail"], key = "#command.productId")
    @Transactional
    fun execute(command: UpdateProductCommand) {
        val product = productRepository.findOrNull(command.productId)
            ?: throw NotFoundException(CatalogErrorCode.PRODUCT_NOT_FOUND)

        product.update(
            name = command.name,
            description = command.description,
            price = command.price,
            categoryId = command.categoryId,
            thumbnailImageUrl = command.thumbnailImageUrl,
            brandId = command.brandId,
        )

        // 상품 변경 이벤트 발행 (search-service 동기화용)
        val category = product.categoryId?.let { categoryRepository.findByIdOrNull(it) }
        val brand = product.brandId?.let { brandRepository.findOrNull(it) }
        integrationEventProducer.publish(
            ProductChangedEvent(
                productId = product.id!!,
                name = product.name,
                description = product.description,
                price = product.price,
                sellingPrice = product.calculateSellingPrice(),
                categoryId = product.categoryId,
                categoryName = category?.name,
                brandId = product.brandId,
                brandName = brand?.name,
                thumbnailImageUrl = product.thumbnailImageUrl,
                status = product.status.name,
                averageRating = product.averageRating,
                reviewCount = product.reviewCount,
                salesCount = product.salesCount,
                viewCount = product.viewCount,
                likeCount = product.likeCount,
            ),
        )
    }
}
