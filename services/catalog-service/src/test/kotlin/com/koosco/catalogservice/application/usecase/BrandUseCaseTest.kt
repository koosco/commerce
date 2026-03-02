package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.CreateBrandCommand
import com.koosco.catalogservice.application.command.DeleteBrandCommand
import com.koosco.catalogservice.application.command.UpdateBrandCommand
import com.koosco.catalogservice.application.port.BrandRepository
import com.koosco.catalogservice.application.port.CatalogIdempotencyRepository
import com.koosco.catalogservice.domain.entity.Brand
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
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
@DisplayName("Brand UseCase 테스트")
class BrandUseCaseTest {

    @Mock lateinit var brandRepository: BrandRepository

    @Mock lateinit var catalogIdempotencyRepository: CatalogIdempotencyRepository

    private fun createBrand(id: Long = 1L): Brand = Brand(id = id, name = "Nike", logoImageUrl = "http://logo.jpg")

    @Nested
    @DisplayName("CreateBrandUseCase는")
    inner class CreateBrandUseCaseTest {

        @Test
        fun `브랜드를 생성한다`() {
            val useCase = CreateBrandUseCase(brandRepository, catalogIdempotencyRepository)
            val command = CreateBrandCommand("Adidas", null)

            whenever(brandRepository.save(any())).thenAnswer { invocation ->
                val b = invocation.getArgument<Brand>(0)
                Brand(id = 1L, name = b.name, logoImageUrl = b.logoImageUrl)
            }

            val result = useCase.execute(command)

            assertThat(result.name).isEqualTo("Adidas")
        }

        @Test
        fun `멱등성 키가 있으면 기존 브랜드를 반환한다`() {
            val useCase = CreateBrandUseCase(brandRepository, catalogIdempotencyRepository)
            val brand = createBrand()
            val existing = com.koosco.catalogservice.domain.entity.CatalogIdempotency.create("key-1", "BRAND", 1L)

            whenever(catalogIdempotencyRepository.findByIdempotencyKeyAndResourceType("key-1", "BRAND"))
                .thenReturn(existing)
            whenever(brandRepository.findOrNull(1L)).thenReturn(brand)

            val result = useCase.execute(CreateBrandCommand("Adidas", null), idempotencyKey = "key-1")

            assertThat(result.id).isEqualTo(1L)
        }

        @Test
        fun `멱등성 키가 있지만 브랜드가 없으면 예외를 던진다`() {
            val useCase = CreateBrandUseCase(brandRepository, catalogIdempotencyRepository)
            val existing = com.koosco.catalogservice.domain.entity.CatalogIdempotency.create("key-1", "BRAND", 1L)

            whenever(catalogIdempotencyRepository.findByIdempotencyKeyAndResourceType("key-1", "BRAND"))
                .thenReturn(existing)
            whenever(brandRepository.findOrNull(1L)).thenReturn(null)

            assertThatThrownBy {
                useCase.execute(CreateBrandCommand("Adidas", null), idempotencyKey = "key-1")
            }.isInstanceOf(NotFoundException::class.java)
        }

        @Test
        fun `멱등성 키와 함께 브랜드를 생성하면 멱등성 레코드를 저장한다`() {
            val useCase = CreateBrandUseCase(brandRepository, catalogIdempotencyRepository)
            val command = CreateBrandCommand("Adidas", null)

            whenever(catalogIdempotencyRepository.findByIdempotencyKeyAndResourceType("key-1", "BRAND"))
                .thenReturn(null)
            whenever(brandRepository.save(any())).thenAnswer { invocation ->
                val b = invocation.getArgument<Brand>(0)
                Brand(id = 1L, name = b.name, logoImageUrl = b.logoImageUrl)
            }
            whenever(catalogIdempotencyRepository.save(any())).thenAnswer { it.getArgument(0) }

            val result = useCase.execute(command, idempotencyKey = "key-1")

            assertThat(result.name).isEqualTo("Adidas")
        }
    }

    @Nested
    @DisplayName("GetBrandsUseCase는")
    inner class GetBrandsUseCaseTest {

        @Test
        fun `모든 브랜드를 조회한다`() {
            val useCase = GetBrandsUseCase(brandRepository)
            whenever(brandRepository.findAll()).thenReturn(listOf(createBrand()))

            val result = useCase.getAll()

            assertThat(result).hasSize(1)
        }

        @Test
        fun `ID로 브랜드를 조회한다`() {
            val useCase = GetBrandsUseCase(brandRepository)
            whenever(brandRepository.findOrNull(1L)).thenReturn(createBrand())

            val result = useCase.getById(1L)

            assertThat(result.name).isEqualTo("Nike")
        }

        @Test
        fun `없는 브랜드를 조회하면 예외를 던진다`() {
            val useCase = GetBrandsUseCase(brandRepository)
            whenever(brandRepository.findOrNull(1L)).thenReturn(null)

            assertThatThrownBy { useCase.getById(1L) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("UpdateBrandUseCase는")
    inner class UpdateBrandUseCaseTest {

        @Test
        fun `브랜드를 수정한다`() {
            val useCase = UpdateBrandUseCase(brandRepository)
            val brand = createBrand()
            whenever(brandRepository.findOrNull(1L)).thenReturn(brand)

            useCase.execute(UpdateBrandCommand(1L, "Puma", null))

            assertThat(brand.name).isEqualTo("Puma")
        }

        @Test
        fun `없는 브랜드이면 예외를 던진다`() {
            val useCase = UpdateBrandUseCase(brandRepository)
            whenever(brandRepository.findOrNull(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(UpdateBrandCommand(1L, "Puma", null)) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("DeleteBrandUseCase는")
    inner class DeleteBrandUseCaseTest {

        @Test
        fun `브랜드를 소프트 삭제한다`() {
            val useCase = DeleteBrandUseCase(brandRepository)
            val brand = createBrand()
            whenever(brandRepository.findOrNull(1L)).thenReturn(brand)
            whenever(brandRepository.save(any())).thenReturn(brand)

            useCase.execute(DeleteBrandCommand(1L))

            assertThat(brand.deletedAt).isNotNull()
            verify(brandRepository).save(brand)
        }

        @Test
        fun `없는 브랜드이면 예외를 던진다`() {
            val useCase = DeleteBrandUseCase(brandRepository)
            whenever(brandRepository.findOrNull(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(DeleteBrandCommand(1L)) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }
}
