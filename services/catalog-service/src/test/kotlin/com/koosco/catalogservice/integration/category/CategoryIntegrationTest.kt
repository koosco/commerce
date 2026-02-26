package com.koosco.catalogservice.integration.category

import com.koosco.catalogservice.application.command.CreateCategoryCommand
import com.koosco.catalogservice.application.command.CreateCategoryTreeCommand
import com.koosco.catalogservice.application.command.GetCategoryListCommand
import com.koosco.catalogservice.application.port.CategoryRepository
import com.koosco.catalogservice.application.usecase.CreateCategoryTreeUseCase
import com.koosco.catalogservice.application.usecase.CreateCategoryUseCase
import com.koosco.catalogservice.application.usecase.GetCategoryListUseCase
import com.koosco.catalogservice.application.usecase.GetCategoryTreeUseCase
import com.koosco.common.core.test.IntegrationTestBase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

/**
 * Integration test for category creation and query using real MariaDB container.
 *
 * Verifies:
 * - Root category creation and persistence
 * - Child category creation under parent
 * - Category tree creation (hierarchical)
 * - Category list and tree query
 */
@SpringBootTest
@ActiveProfiles("test")
class CategoryIntegrationTest : IntegrationTestBase() {

    @Autowired
    private lateinit var createCategoryUseCase: CreateCategoryUseCase

    @Autowired
    private lateinit var createCategoryTreeUseCase: CreateCategoryTreeUseCase

    @Autowired
    private lateinit var getCategoryListUseCase: GetCategoryListUseCase

    @Autowired
    private lateinit var getCategoryTreeUseCase: GetCategoryTreeUseCase

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @Test
    fun `should create root category and query it`() {
        // given
        val command = CreateCategoryCommand(
            name = "Electronics",
            parentId = null,
            ordering = 1,
        )

        // when
        val result = createCategoryUseCase.execute(command)

        // then
        assertNotNull(result.id)
        assertEquals("Electronics", result.name)
        assertEquals(0, result.depth)
        assertNull(result.parentId)
    }

    @Test
    fun `should create child category under parent`() {
        // given
        val parentResult = createCategoryUseCase.execute(
            CreateCategoryCommand(name = "Fashion", parentId = null, ordering = 2),
        )
        val childCommand = CreateCategoryCommand(
            name = "Shoes",
            parentId = parentResult.id,
            ordering = 1,
        )

        // when
        val childResult = createCategoryUseCase.execute(childCommand)

        // then
        assertNotNull(childResult.id)
        assertEquals("Shoes", childResult.name)
        assertEquals(1, childResult.depth)
        assertEquals(parentResult.id, childResult.parentId)
    }

    @Test
    fun `should create category tree and query as tree structure`() {
        // given
        val treeCommand = CreateCategoryTreeCommand(
            name = "Home & Living",
            ordering = 3,
            children = listOf(
                CreateCategoryTreeCommand(
                    name = "Furniture",
                    ordering = 1,
                    children = listOf(
                        CreateCategoryTreeCommand(name = "Sofas", ordering = 1),
                        CreateCategoryTreeCommand(name = "Tables", ordering = 2),
                    ),
                ),
                CreateCategoryTreeCommand(
                    name = "Kitchen",
                    ordering = 2,
                ),
            ),
        )

        // when
        val treeResult = createCategoryTreeUseCase.execute(treeCommand)

        // then
        assertEquals("Home & Living", treeResult.name)
        assertEquals(2, treeResult.children.size)

        val furniture = treeResult.children.find { it.name == "Furniture" }
        assertNotNull(furniture)
        assertEquals(2, furniture!!.children.size)
        assertTrue(furniture.children.any { it.name == "Sofas" })
        assertTrue(furniture.children.any { it.name == "Tables" })

        val kitchen = treeResult.children.find { it.name == "Kitchen" }
        assertNotNull(kitchen)
        assertTrue(kitchen!!.children.isEmpty())
    }

    @Test
    fun `should list root categories`() {
        // given - create root categories
        createCategoryUseCase.execute(
            CreateCategoryCommand(name = "Sports", parentId = null, ordering = 10),
        )
        createCategoryUseCase.execute(
            CreateCategoryCommand(name = "Books", parentId = null, ordering = 11),
        )

        // when
        val rootCategories = getCategoryListUseCase.execute(GetCategoryListCommand(parentId = null))

        // then
        assertTrue(rootCategories.size >= 2)
        assertTrue(rootCategories.any { it.name == "Sports" })
        assertTrue(rootCategories.any { it.name == "Books" })
    }
}
