package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.port.CatalogIdempotencyRepository
import com.koosco.catalogservice.application.port.ProductLikeRepository
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.catalogservice.domain.entity.CatalogIdempotency
import com.koosco.catalogservice.domain.entity.ProductLike
import com.koosco.catalogservice.domain.entity.ProductLikeId
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import org.springframework.transaction.annotation.Transactional

@UseCase
class ToggleProductLikeUseCase(
    private val productRepository: ProductRepository,
    private val productLikeRepository: ProductLikeRepository,
    private val idempotencyRepository: CatalogIdempotencyRepository,
) {

    @Transactional
    fun execute(productId: Long, userId: Long, idempotencyKey: String? = null): Boolean {
        if (idempotencyKey != null) {
            val existing = idempotencyRepository.findByIdempotencyKeyAndResourceType(
                idempotencyKey,
                RESOURCE_TYPE,
            )
            if (existing != null) {
                return existing.resourceId == 1L
            }
        }

        val product = productRepository.findOrNull(productId)
            ?: throw NotFoundException(CatalogErrorCode.PRODUCT_NOT_FOUND)

        val existing = productLikeRepository.findById(ProductLikeId(productId, userId))

        val liked = if (existing != null) {
            productLikeRepository.delete(existing)
            product.likeCount = maxOf(0, product.likeCount - 1)
            false
        } else {
            productLikeRepository.save(ProductLike(productId = productId, userId = userId))
            product.likeCount += 1
            true
        }

        if (idempotencyKey != null) {
            idempotencyRepository.save(
                CatalogIdempotency.create(
                    idempotencyKey = idempotencyKey,
                    resourceType = RESOURCE_TYPE,
                    resourceId = if (liked) 1L else 0L,
                ),
            )
        }

        return liked
    }

    companion object {
        private const val RESOURCE_TYPE = "PRODUCT_LIKE"
    }
}
