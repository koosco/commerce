package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.CreateCategoryCommand
import com.koosco.catalogservice.application.command.CreateCategoryTreeCommand
import com.koosco.catalogservice.application.command.GetCategoryListCommand
import com.koosco.catalogservice.application.port.CatalogIdempotencyRepository
import com.koosco.catalogservice.application.port.CategoryRepository
import com.koosco.catalogservice.application.usecase.category.CreateCategoryTreeUseCase
import com.koosco.catalogservice.application.usecase.category.CreateCategoryUseCase
import com.koosco.catalogservice.application.usecase.category.GetCategoryByIdUseCase
import com.koosco.catalogservice.application.usecase.category.GetCategoryListUseCase
import com.koosco.catalogservice.application.usecase.category.GetCategoryTreeUseCase
import com.koosco.catalogservice.domain.entity.Category
import com.koosco.common.core.exception.ConflictException
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

@ExtendWith(MockitoExtension::class)
@DisplayName("Category UseCase 테스트")
class CategoryUseCaseTest {

    @Mock lateinit var categoryRepository: CategoryRepository

    @Mock lateinit var catalogIdempotencyRepository: CatalogIdempotencyRepository

    private fun createCategory(id: Long = 1L, name: String = "전자제품", depth: Int = 0): Category = Category(
        id = id,
        name = name,
        code = "ELEC_ABCD",
        depth = depth,
    )

    @Nested
    @DisplayName("CreateCategoryUseCase는")
    inner class CreateCategoryUseCaseTest {

        @Test
        fun `루트 카테고리를 생성한다`() {
            val useCase = CreateCategoryUseCase(categoryRepository, catalogIdempotencyRepository)
            val command = CreateCategoryCommand("전자제품", null, 0)

            whenever(categoryRepository.existsByNameAndParent("전자제품", null)).thenReturn(false)
            whenever(categoryRepository.save(any())).thenAnswer { invocation ->
                val c = invocation.getArgument<Category>(0)
                Category(id = 1L, name = c.name, code = c.code, depth = c.depth)
            }

            val result = useCase.execute(command)

            assertThat(result.name).isEqualTo("전자제품")
            assertThat(result.depth).isEqualTo(0)
        }

        @Test
        fun `부모 카테고리 하위에 자식을 생성한다`() {
            val useCase = CreateCategoryUseCase(categoryRepository, catalogIdempotencyRepository)
            val parent = createCategory()
            val command = CreateCategoryCommand("스마트폰", 1L, 0)

            whenever(categoryRepository.findByIdOrNull(1L)).thenReturn(parent)
            whenever(categoryRepository.existsByNameAndParent("스마트폰", parent)).thenReturn(false)
            whenever(categoryRepository.save(any())).thenAnswer { invocation ->
                val c = invocation.getArgument<Category>(0)
                Category(id = 2L, name = c.name, code = c.code, depth = c.depth, parent = parent)
            }

            val result = useCase.execute(command)

            assertThat(result.name).isEqualTo("스마트폰")
            assertThat(result.depth).isEqualTo(1)
        }

        @Test
        fun `부모가 없으면 NotFoundException을 던진다`() {
            val useCase = CreateCategoryUseCase(categoryRepository, catalogIdempotencyRepository)
            val command = CreateCategoryCommand("스마트폰", 999L, 0)

            whenever(categoryRepository.findByIdOrNull(999L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(command) }
                .isInstanceOf(NotFoundException::class.java)
        }

        @Test
        fun `중복 이름이면 ConflictException을 던진다`() {
            val useCase = CreateCategoryUseCase(categoryRepository, catalogIdempotencyRepository)
            val command = CreateCategoryCommand("전자제품", null, 0)

            whenever(categoryRepository.existsByNameAndParent("전자제품", null)).thenReturn(true)

            assertThatThrownBy { useCase.execute(command) }
                .isInstanceOf(ConflictException::class.java)
        }

        @Test
        fun `멱등성 키가 있으면 기존 카테고리를 반환한다`() {
            val useCase = CreateCategoryUseCase(categoryRepository, catalogIdempotencyRepository)
            val category = createCategory()
            val existing = com.koosco.catalogservice.domain.entity.CatalogIdempotency.create("key-1", "CATEGORY", 1L)

            whenever(catalogIdempotencyRepository.findByIdempotencyKeyAndResourceType("key-1", "CATEGORY"))
                .thenReturn(existing)
            whenever(categoryRepository.findByIdOrNull(1L)).thenReturn(category)

            val result = useCase.execute(CreateCategoryCommand("전자제품", null, 0), idempotencyKey = "key-1")

            assertThat(result.id).isEqualTo(1L)
        }

        @Test
        fun `멱등성 키가 있지만 카테고리가 없으면 예외를 던진다`() {
            val useCase = CreateCategoryUseCase(categoryRepository, catalogIdempotencyRepository)
            val existing = com.koosco.catalogservice.domain.entity.CatalogIdempotency.create("key-1", "CATEGORY", 1L)

            whenever(catalogIdempotencyRepository.findByIdempotencyKeyAndResourceType("key-1", "CATEGORY"))
                .thenReturn(existing)
            whenever(categoryRepository.findByIdOrNull(1L)).thenReturn(null)

            assertThatThrownBy {
                useCase.execute(CreateCategoryCommand("전자제품", null, 0), idempotencyKey = "key-1")
            }.isInstanceOf(NotFoundException::class.java)
        }

        @Test
        fun `멱등성 키와 함께 카테고리를 생성하면 멱등성 레코드를 저장한다`() {
            val useCase = CreateCategoryUseCase(categoryRepository, catalogIdempotencyRepository)
            val command = CreateCategoryCommand("전자제품", null, 0)

            whenever(catalogIdempotencyRepository.findByIdempotencyKeyAndResourceType("key-1", "CATEGORY"))
                .thenReturn(null)
            whenever(categoryRepository.existsByNameAndParent("전자제품", null)).thenReturn(false)
            whenever(categoryRepository.save(any())).thenAnswer { invocation ->
                val c = invocation.getArgument<Category>(0)
                Category(id = 1L, name = c.name, code = c.code, depth = c.depth)
            }
            whenever(catalogIdempotencyRepository.save(any())).thenAnswer { it.getArgument(0) }

            val result = useCase.execute(command, idempotencyKey = "key-1")

            assertThat(result.name).isEqualTo("전자제품")
        }
    }

    @Nested
    @DisplayName("CreateCategoryTreeUseCase는")
    inner class CreateCategoryTreeUseCaseTest {

        @Test
        fun `카테고리 트리를 생성한다`() {
            val useCase =
                CreateCategoryTreeUseCase(categoryRepository, catalogIdempotencyRepository)
            val command = CreateCategoryTreeCommand(
                name = "패션",
                children = listOf(
                    CreateCategoryTreeCommand("남성"),
                    CreateCategoryTreeCommand("여성"),
                ),
            )

            whenever(categoryRepository.save(any())).thenAnswer { invocation ->
                val c = invocation.getArgument<Category>(0)
                // id 설정을 위한 리플렉션
                val field = Category::class.java.getDeclaredField("id")
                field.isAccessible = true
                field.set(c, 1L)
                c.children.forEachIndexed { index, child ->
                    val childField = Category::class.java.getDeclaredField("id")
                    childField.isAccessible = true
                    childField.set(child, (index + 2).toLong())
                }
                c
            }

            val result = useCase.execute(command)

            assertThat(result.name).isEqualTo("패션")
            assertThat(result.children).hasSize(2)
        }

        @Test
        fun `멱등성 키가 있으면 기존 트리를 반환한다`() {
            val useCase =
                CreateCategoryTreeUseCase(categoryRepository, catalogIdempotencyRepository)
            val category = createCategory()
            val existing =
                com.koosco.catalogservice.domain.entity.CatalogIdempotency.create("key-1", "CATEGORY_TREE", 1L)

            whenever(
                catalogIdempotencyRepository.findByIdempotencyKeyAndResourceType(
                    "key-1",
                    "CATEGORY_TREE",
                ),
            ).thenReturn(existing)
            whenever(categoryRepository.findByIdOrNull(1L)).thenReturn(category)

            val result = useCase.execute(CreateCategoryTreeCommand("패션"), idempotencyKey = "key-1")

            assertThat(result.id).isEqualTo(1L)
        }

        @Test
        fun `멱등성 키가 있지만 카테고리가 없으면 예외를 던진다`() {
            val useCase =
                CreateCategoryTreeUseCase(categoryRepository, catalogIdempotencyRepository)
            val existing =
                com.koosco.catalogservice.domain.entity.CatalogIdempotency.create("key-1", "CATEGORY_TREE", 1L)

            whenever(
                catalogIdempotencyRepository.findByIdempotencyKeyAndResourceType(
                    "key-1",
                    "CATEGORY_TREE",
                ),
            ).thenReturn(existing)
            whenever(categoryRepository.findByIdOrNull(1L)).thenReturn(null)

            assertThatThrownBy {
                useCase.execute(CreateCategoryTreeCommand("패션"), idempotencyKey = "key-1")
            }.isInstanceOf(NotFoundException::class.java)
        }

        @Test
        fun `멱등성 키와 함께 트리를 생성하면 멱등성 레코드를 저장한다`() {
            val useCase =
                CreateCategoryTreeUseCase(categoryRepository, catalogIdempotencyRepository)
            val command = CreateCategoryTreeCommand(name = "패션")

            whenever(
                catalogIdempotencyRepository.findByIdempotencyKeyAndResourceType(
                    "key-1",
                    "CATEGORY_TREE",
                ),
            ).thenReturn(null)
            whenever(categoryRepository.save(any())).thenAnswer { invocation ->
                val c = invocation.getArgument<Category>(0)
                val field = Category::class.java.getDeclaredField("id")
                field.isAccessible = true
                field.set(c, 1L)
                c
            }
            whenever(catalogIdempotencyRepository.save(any())).thenAnswer { it.getArgument(0) }

            val result = useCase.execute(command, idempotencyKey = "key-1")

            assertThat(result.name).isEqualTo("패션")
        }
    }

    @Nested
    @DisplayName("GetCategoryByIdUseCase는")
    inner class GetCategoryByIdUseCaseTest {

        @Test
        fun `카테고리를 조회한다`() {
            val useCase = GetCategoryByIdUseCase(categoryRepository)
            val category = createCategory()

            whenever(categoryRepository.findByIdOrNull(1L)).thenReturn(category)

            val result = useCase.execute(1L)

            assertThat(result.name).isEqualTo("전자제품")
        }

        @Test
        fun `카테고리가 없으면 예외를 던진다`() {
            val useCase = GetCategoryByIdUseCase(categoryRepository)

            whenever(categoryRepository.findByIdOrNull(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(1L) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("GetCategoryListUseCase는")
    inner class GetCategoryListUseCaseTest {

        @Test
        fun `부모 ID가 없으면 루트 카테고리를 조회한다`() {
            val useCase = GetCategoryListUseCase(categoryRepository)
            val categories = listOf(createCategory())

            whenever(categoryRepository.findByParentIsNull()).thenReturn(categories)

            val result = useCase.execute(GetCategoryListCommand(null))

            assertThat(result).hasSize(1)
        }

        @Test
        fun `부모 ID가 있으면 자식 카테고리를 조회한다`() {
            val useCase = GetCategoryListUseCase(categoryRepository)
            val categories = listOf(createCategory(id = 2L, name = "스마트폰", depth = 1))

            whenever(categoryRepository.findByParentIdOrderByOrderingAsc(1L)).thenReturn(categories)

            val result = useCase.execute(GetCategoryListCommand(1L))

            assertThat(result).hasSize(1)
            assertThat(result.first().name).isEqualTo("스마트폰")
        }
    }

    @Nested
    @DisplayName("GetCategoryTreeUseCase는")
    inner class GetCategoryTreeUseCaseTest {

        @Test
        fun `카테고리 트리를 구성한다`() {
            val useCase = GetCategoryTreeUseCase(categoryRepository)
            val root = createCategory(id = 1L, name = "패션")
            val child = Category(id = 2L, name = "남성", code = "MEN_ABCD", depth = 1, parent = root)
            root.children.add(child)

            whenever(categoryRepository.findAllByOrderByDepthAscOrderingAsc()).thenReturn(listOf(root, child))

            val result = useCase.execute()

            assertThat(result).hasSize(1)
            assertThat(result.first().name).isEqualTo("패션")
            assertThat(result.first().children).hasSize(1)
        }
    }
}
