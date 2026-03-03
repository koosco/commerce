package com.koosco.catalogservice.application.usecase.product

import com.koosco.catalogservice.application.command.DeleteProductCommand
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.catalogservice.contract.outbound.ProductDeletedEvent
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.event.IntegrationEventProducer
import com.koosco.common.core.exception.NotFoundException
import org.springframework.cache.annotation.CacheEvict
import org.springframework.transaction.annotation.Transactional

@UseCase
class DeleteProductUseCase(
    private val productRepository: ProductRepository,
    private val integrationEventProducer: IntegrationEventProducer,
) {

    @CacheEvict(cacheNames = ["productDetail"], key = "#command.productId")
    @Transactional
    fun execute(command: DeleteProductCommand) {
        val product = productRepository.findOrNull(command.productId)
            ?: throw NotFoundException(CatalogErrorCode.PRODUCT_NOT_FOUND)

        product.delete()

        // 상품 삭제 이벤트 발행 (search-service 동기화용)
        integrationEventProducer.publish(
            ProductDeletedEvent(productId = product.id!!),
        )
    }
}
