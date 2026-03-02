package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.CreatePromotionCommand
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.application.port.PromotionRepository
import com.koosco.catalogservice.application.result.PromotionInfo
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.catalogservice.contract.outbound.PromotionActivatedEvent
import com.koosco.catalogservice.domain.entity.Promotion
import com.koosco.catalogservice.domain.service.PromotionPriceResolver
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.event.IntegrationEventProducer
import com.koosco.common.core.exception.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@UseCase
class CreatePromotionUseCase(
    private val promotionRepository: PromotionRepository,
    private val productRepository: ProductRepository,
    private val integrationEventProducer: IntegrationEventProducer,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun execute(command: CreatePromotionCommand): PromotionInfo {
        val product = productRepository.findOrNull(command.productId)
            ?: throw NotFoundException(CatalogErrorCode.PRODUCT_NOT_FOUND)

        val promotion = Promotion.create(
            productId = command.productId,
            discountPrice = command.discountPrice,
            startAt = command.startAt,
            endAt = command.endAt,
            type = command.type,
            priority = command.priority,
            description = command.description,
        )

        val savedPromotion = promotionRepository.save(promotion)

        logger.info(
            "Promotion created: promotionId=${savedPromotion.id}, " +
                "productId=${command.productId}, type=${command.type}",
        )

        // 프로모션이 현재 활성 상태라면 이벤트 발행
        val now = LocalDateTime.now()
        if (savedPromotion.isActiveAt(now)) {
            val activePromotions = promotionRepository.findActiveByProductId(
                command.productId,
                now,
            )
            val resolvedPrice = PromotionPriceResolver.resolve(activePromotions)

            if (resolvedPrice != null) {
                integrationEventProducer.publish(
                    PromotionActivatedEvent(
                        promotionId = savedPromotion.id!!,
                        productId = command.productId,
                        productCode = product.productCode,
                        originalPrice = product.price,
                        discountPrice = resolvedPrice,
                        type = command.type,
                        startAt = command.startAt,
                        endAt = command.endAt,
                        activatedAt = now,
                    ),
                )
            }
        }

        return PromotionInfo.from(savedPromotion)
    }
}
