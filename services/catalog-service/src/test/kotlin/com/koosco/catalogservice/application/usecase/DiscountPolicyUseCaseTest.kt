package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.CreateDiscountPolicyCommand
import com.koosco.catalogservice.application.command.DeleteDiscountPolicyCommand
import com.koosco.catalogservice.application.command.GetDiscountPoliciesCommand
import com.koosco.catalogservice.application.command.UpdateDiscountPolicyCommand
import com.koosco.catalogservice.application.port.DiscountPolicyRepository
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.application.usecase.discount.CreateDiscountPolicyUseCase
import com.koosco.catalogservice.application.usecase.discount.DeleteDiscountPolicyUseCase
import com.koosco.catalogservice.application.usecase.discount.GetDiscountPoliciesUseCase
import com.koosco.catalogservice.domain.entity.DiscountPolicy
import com.koosco.catalogservice.domain.entity.Product
import com.koosco.catalogservice.domain.enums.DiscountType
import com.koosco.catalogservice.domain.enums.ProductStatus
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
@DisplayName("DiscountPolicy UseCase 테스트")
class DiscountPolicyUseCaseTest {

    @Mock lateinit var productRepository: ProductRepository

    @Mock lateinit var discountPolicyRepository: DiscountPolicyRepository

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
    @DisplayName("CreateDiscountPolicyUseCase는")
    inner class CreateDiscountPolicyUseCaseTest {

        @Test
        fun `할인 정책을 생성한다`() {
            val useCase =
                CreateDiscountPolicyUseCase(
                    productRepository,
                    discountPolicyRepository,
                    integrationEventProducer,
                )
            val product = createProduct()
            val command = CreateDiscountPolicyCommand(
                productId = 1L,
                name = "10% 할인",
                discountType = DiscountType.RATE,
                discountValue = 10,
                startAt = now.minusDays(1),
                endAt = now.plusDays(1),
            )

            whenever(productRepository.findOrNull(1L)).thenReturn(product)
            whenever(discountPolicyRepository.save(any())).thenAnswer { invocation ->
                val dp = invocation.getArgument<DiscountPolicy>(0)
                DiscountPolicy(
                    id = 1L,
                    product = dp.product,
                    name = dp.name,
                    discountType = dp.discountType,
                    discountValue = dp.discountValue,
                    startAt = dp.startAt,
                    endAt = dp.endAt,
                )
            }

            val result = useCase.execute(command)

            assertThat(result.name).isEqualTo("10% 할인")
            assertThat(result.discountType).isEqualTo(DiscountType.RATE)
        }

        @Test
        fun `상품이 없으면 예외를 던진다`() {
            val useCase =
                CreateDiscountPolicyUseCase(
                    productRepository,
                    discountPolicyRepository,
                    integrationEventProducer,
                )
            val command = CreateDiscountPolicyCommand(1L, "할인", DiscountType.RATE, 10, now, now.plusDays(1))

            whenever(productRepository.findOrNull(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(command) }
                .isInstanceOf(NotFoundException::class.java)
        }

        @Test
        fun `활성 할인이 적용되어 가격이 변경되면 이벤트를 발행한다`() {
            val useCase =
                CreateDiscountPolicyUseCase(
                    productRepository,
                    discountPolicyRepository,
                    integrationEventProducer,
                )
            val product = createProduct()
            // Add an active SKU to trigger price change event
            val sku = com.koosco.catalogservice.domain.entity.ProductSku(
                id = 1L,
                skuId = "SKU-001",
                product = product,
                price = 10000,
                optionValues = """{"색상":"빨강"}""",
                status = com.koosco.catalogservice.domain.enums.SkuStatus.ACTIVE,
            )
            product.skus.add(sku)

            val command = CreateDiscountPolicyCommand(
                productId = 1L,
                name = "50% 할인",
                discountType = DiscountType.RATE,
                discountValue = 50,
                startAt = now.minusDays(1),
                endAt = now.plusDays(1),
            )

            whenever(productRepository.findOrNull(1L)).thenReturn(product)
            whenever(discountPolicyRepository.save(any())).thenAnswer { invocation ->
                val dp = invocation.getArgument<DiscountPolicy>(0)
                DiscountPolicy(
                    id = 1L,
                    product = dp.product,
                    name = dp.name,
                    discountType = dp.discountType,
                    discountValue = dp.discountValue,
                    startAt = dp.startAt,
                    endAt = dp.endAt,
                )
            }
            org.mockito.kotlin.doNothing().whenever(integrationEventProducer).publish(any())

            val result = useCase.execute(command)

            assertThat(result.name).isEqualTo("50% 할인")
            org.mockito.kotlin.verify(integrationEventProducer).publish(any())
        }
    }

    @Nested
    @DisplayName("GetDiscountPoliciesUseCase는")
    inner class GetDiscountPoliciesUseCaseTest {

        @Test
        fun `상품의 할인 정책 목록을 조회한다`() {
            val useCase = GetDiscountPoliciesUseCase(productRepository, discountPolicyRepository)
            val product = createProduct()
            val policy = DiscountPolicy(
                id = 1L,
                product = product,
                name = "할인",
                discountType = DiscountType.RATE,
                discountValue = 10,
                startAt = now.minusDays(1),
                endAt = now.plusDays(1),
            )

            whenever(productRepository.findOrNull(1L)).thenReturn(product)
            whenever(discountPolicyRepository.findByProductId(1L)).thenReturn(listOf(policy))

            val result = useCase.execute(GetDiscountPoliciesCommand(1L))

            assertThat(result).hasSize(1)
        }

        @Test
        fun `상품이 없으면 예외를 던진다`() {
            val useCase = GetDiscountPoliciesUseCase(productRepository, discountPolicyRepository)

            whenever(productRepository.findOrNull(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(GetDiscountPoliciesCommand(1L)) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("DeleteDiscountPolicyUseCase는")
    inner class DeleteDiscountPolicyUseCaseTest {

        @Test
        fun `할인 정책을 삭제한다`() {
            val useCase =
                DeleteDiscountPolicyUseCase(
                    productRepository,
                    discountPolicyRepository,
                    integrationEventProducer,
                )
            val product = createProduct()
            val policy = DiscountPolicy(
                id = 1L,
                product = product,
                name = "할인",
                discountType = DiscountType.RATE,
                discountValue = 10,
                startAt = now.minusDays(1),
                endAt = now.plusDays(1),
            )
            product.discountPolicies.add(policy)

            whenever(productRepository.findOrNull(1L)).thenReturn(product)
            whenever(discountPolicyRepository.findOrNull(1L)).thenReturn(policy)

            useCase.execute(DeleteDiscountPolicyCommand(1L, 1L))

            assertThat(product.discountPolicies).isEmpty()
        }

        @Test
        fun `상품이 없으면 예외를 던진다`() {
            val useCase =
                DeleteDiscountPolicyUseCase(
                    productRepository,
                    discountPolicyRepository,
                    integrationEventProducer,
                )

            whenever(productRepository.findOrNull(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(DeleteDiscountPolicyCommand(1L, 1L)) }
                .isInstanceOf(NotFoundException::class.java)
        }

        @Test
        fun `할인 정책이 없으면 예외를 던진다`() {
            val useCase =
                DeleteDiscountPolicyUseCase(
                    productRepository,
                    discountPolicyRepository,
                    integrationEventProducer,
                )
            val product = createProduct()

            whenever(productRepository.findOrNull(1L)).thenReturn(product)
            whenever(discountPolicyRepository.findOrNull(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(DeleteDiscountPolicyCommand(1L, 1L)) }
                .isInstanceOf(NotFoundException::class.java)
        }

        @Test
        fun `할인 정책이 다른 상품에 속하면 예외를 던진다`() {
            val useCase =
                DeleteDiscountPolicyUseCase(
                    productRepository,
                    discountPolicyRepository,
                    integrationEventProducer,
                )
            val product = createProduct()
            val otherProduct = Product(
                id = 2L,
                productCode = "OTHER-001",
                name = "다른상품",
                price = 20000,
                status = ProductStatus.ACTIVE,
            )
            val policy = DiscountPolicy(
                id = 1L,
                product = otherProduct,
                name = "할인",
                discountType = DiscountType.RATE,
                discountValue = 10,
                startAt = now.minusDays(1),
                endAt = now.plusDays(1),
            )

            whenever(productRepository.findOrNull(1L)).thenReturn(product)
            whenever(discountPolicyRepository.findOrNull(1L)).thenReturn(policy)

            assertThatThrownBy { useCase.execute(DeleteDiscountPolicyCommand(1L, 1L)) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `활성 할인 삭제 시 가격이 변경되면 이벤트를 발행한다`() {
            val useCase =
                DeleteDiscountPolicyUseCase(
                    productRepository,
                    discountPolicyRepository,
                    integrationEventProducer,
                )
            val product = createProduct()
            val sku = com.koosco.catalogservice.domain.entity.ProductSku(
                id = 1L,
                skuId = "SKU-001",
                product = product,
                price = 10000,
                optionValues = """{"색상":"빨강"}""",
                status = com.koosco.catalogservice.domain.enums.SkuStatus.ACTIVE,
            )
            product.skus.add(sku)
            val policy = DiscountPolicy(
                id = 1L,
                product = product,
                name = "50% 할인",
                discountType = DiscountType.RATE,
                discountValue = 50,
                startAt = now.minusDays(1),
                endAt = now.plusDays(1),
            )
            product.discountPolicies.add(policy)

            whenever(productRepository.findOrNull(1L)).thenReturn(product)
            whenever(discountPolicyRepository.findOrNull(1L)).thenReturn(policy)
            org.mockito.kotlin.doNothing().whenever(integrationEventProducer).publish(any())

            useCase.execute(DeleteDiscountPolicyCommand(1L, 1L))

            assertThat(product.discountPolicies).isEmpty()
            org.mockito.kotlin.verify(integrationEventProducer).publish(any())
        }
    }

    @Nested
    @DisplayName("UpdateDiscountPolicyCommand는")
    inner class UpdateDiscountPolicyCommandTest {

        @Test
        fun `데이터를 올바르게 저장한다`() {
            val command = UpdateDiscountPolicyCommand(
                productId = 1L,
                discountPolicyId = 2L,
                name = "새 할인",
            )

            assertThat(command.productId).isEqualTo(1L)
            assertThat(command.discountPolicyId).isEqualTo(2L)
            assertThat(command.name).isEqualTo("새 할인")
        }

        @Test
        fun `name이 null일 수 있다`() {
            val command = UpdateDiscountPolicyCommand(
                productId = 1L,
                discountPolicyId = 2L,
                name = null,
            )

            assertThat(command.name).isNull()
        }
    }
}
