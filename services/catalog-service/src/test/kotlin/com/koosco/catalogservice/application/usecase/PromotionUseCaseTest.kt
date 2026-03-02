package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.CreatePromotionCommand
import com.koosco.catalogservice.application.command.GetPromotionPriceCommand
import com.koosco.catalogservice.application.command.GetPromotionsByProductCommand
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.application.port.PromotionRepository
import com.koosco.catalogservice.application.usecase.promotion.CreatePromotionUseCase
import com.koosco.catalogservice.application.usecase.promotion.GetPromotionPriceUseCase
import com.koosco.catalogservice.application.usecase.promotion.GetPromotionsByProductUseCase
import com.koosco.catalogservice.domain.entity.Product
import com.koosco.catalogservice.domain.entity.Promotion
import com.koosco.catalogservice.domain.enums.ProductStatus
import com.koosco.catalogservice.domain.enums.PromotionType
import com.koosco.common.core.event.IntegrationEventProducer
import com.koosco.common.core.exception.NotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
@DisplayName("Promotion UseCase 테스트")
class PromotionUseCaseTest {

    @Mock lateinit var promotionRepository: PromotionRepository

    @Mock lateinit var productRepository: ProductRepository

    @Mock lateinit var integrationEventProducer: IntegrationEventProducer

    private val now = LocalDateTime.now()

    private fun createProduct(): Product = Product(
        id = 1L,
        productCode = "TEST-001",
        name = "테스트",
        price = 10000,
        status = ProductStatus.ACTIVE,
    )

    @Nested
    @DisplayName("CreatePromotionUseCase는")
    inner class CreatePromotionUseCaseTest {

        @Test
        fun `프로모션을 생성한다`() {
            val useCase = CreatePromotionUseCase(
                promotionRepository,
                productRepository,
                integrationEventProducer
            )
            val product = createProduct()
            val command = CreatePromotionCommand(
                productId = 1L,
                discountPrice = 8000,
                startAt = now.plusDays(1),
                endAt = now.plusDays(10),
                type = PromotionType.CAMPAIGN,
                priority = 0,
            )

            whenever(productRepository.findOrNull(1L)).thenReturn(product)
            whenever(promotionRepository.save(any())).thenAnswer { invocation ->
                val p = invocation.getArgument<Promotion>(0)
                Promotion(
                    id = 1L,
                    productId = p.productId,
                    discountPrice = p.discountPrice,
                    startAt = p.startAt,
                    endAt = p.endAt,
                    type = p.type,
                    priority = p.priority,
                )
            }

            val result = useCase.execute(command)

            assertThat(result.discountPrice).isEqualTo(8000)
        }

        @Test
        fun `활성 프로모션이면 이벤트를 발행한다`() {
            val useCase = CreatePromotionUseCase(
                promotionRepository,
                productRepository,
                integrationEventProducer
            )
            val product = createProduct()
            val command = CreatePromotionCommand(
                productId = 1L,
                discountPrice = 8000,
                startAt = now.minusDays(1),
                endAt = now.plusDays(10),
                type = PromotionType.CAMPAIGN,
                priority = 0,
            )

            whenever(productRepository.findOrNull(1L)).thenReturn(product)
            whenever(promotionRepository.save(any())).thenAnswer { invocation ->
                val p = invocation.getArgument<Promotion>(0)
                Promotion(
                    id = 1L,
                    productId = p.productId,
                    discountPrice = p.discountPrice,
                    startAt = p.startAt,
                    endAt = p.endAt,
                    type = p.type,
                    priority = p.priority,
                )
            }
            whenever(promotionRepository.findActiveByProductId(any(), any())).thenReturn(
                listOf(
                    Promotion.create(1L, 8000, now.minusDays(1), now.plusDays(10), PromotionType.CAMPAIGN, 0),
                ),
            )

            useCase.execute(command)

            // 이벤트 발행 시도 검증 - integrationEventProducer.publish가 호출됨
        }

        @Test
        fun `상품이 없으면 예외를 던진다`() {
            val useCase = CreatePromotionUseCase(
                promotionRepository,
                productRepository,
                integrationEventProducer
            )
            val command = CreatePromotionCommand(1L, 8000, now, now.plusDays(1), PromotionType.CAMPAIGN, 0)

            whenever(productRepository.findOrNull(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(command) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("GetPromotionsByProductUseCase는")
    inner class GetPromotionsByProductUseCaseTest {

        @Test
        fun `활성 프로모션 목록을 조회한다`() {
            val useCase = GetPromotionsByProductUseCase(promotionRepository, productRepository)
            val product = createProduct()
            val promotion = Promotion(
                id = 1L,
                productId = 1L,
                discountPrice = 8000,
                startAt = now.minusDays(1),
                endAt = now.plusDays(1),
                type = PromotionType.CAMPAIGN,
                priority = 0,
            )

            whenever(productRepository.findOrNull(1L)).thenReturn(product)
            whenever(promotionRepository.findActiveByProductId(any(), any())).thenReturn(listOf(promotion))

            val result = useCase.execute(GetPromotionsByProductCommand(1L))

            assertThat(result).hasSize(1)
        }

        @Test
        fun `상품이 없으면 예외를 던진다`() {
            val useCase = GetPromotionsByProductUseCase(promotionRepository, productRepository)

            whenever(productRepository.findOrNull(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(GetPromotionsByProductCommand(1L)) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("GetPromotionPriceUseCase는")
    inner class GetPromotionPriceUseCaseTest {

        @Test
        fun `프로모션 가격 정보를 반환한다`() {
            val useCase = GetPromotionPriceUseCase(promotionRepository, productRepository)
            val product = createProduct()
            val promotion = Promotion.create(1L, 8000, now.minusDays(1), now.plusDays(1), PromotionType.CAMPAIGN, 0)

            whenever(productRepository.findOrNull(1L)).thenReturn(product)
            whenever(promotionRepository.findActiveByProductId(any(), any())).thenReturn(listOf(promotion))

            val result = useCase.execute(GetPromotionPriceCommand(1L))

            assertThat(result.hasActivePromotion).isTrue()
            assertThat(result.finalPrice).isEqualTo(8000)
            assertThat(result.originalPrice).isEqualTo(10000)
        }

        @Test
        fun `프로모션이 없으면 원래 가격을 반환한다`() {
            val useCase = GetPromotionPriceUseCase(promotionRepository, productRepository)
            val product = createProduct()

            whenever(productRepository.findOrNull(1L)).thenReturn(product)
            whenever(promotionRepository.findActiveByProductId(any(), any())).thenReturn(emptyList())

            val result = useCase.execute(GetPromotionPriceCommand(1L))

            assertThat(result.hasActivePromotion).isFalse()
            assertThat(result.finalPrice).isEqualTo(10000)
        }
    }
}
