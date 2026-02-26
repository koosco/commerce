package com.koosco.common.core.test

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MariaDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

/**
 * Base class for MariaDB integration tests using Testcontainers.
 *
 * Provides:
 * - Shared MariaDB container (started once per test class)
 * - Dynamic property configuration for Spring DataSource
 * - JPA ddl-auto set to create-drop for clean test schema
 *
 * Usage:
 * ```kotlin
 * @SpringBootTest
 * @ActiveProfiles("test")
 * class MyDatabaseTest : MariaDBContainerTestBase() {
 *     @Test
 *     fun `should save entity`() {
 *         // test with real MariaDB
 *     }
 * }
 * ```
 */
@Testcontainers
abstract class MariaDBContainerTestBase {

    companion object {
        private const val MARIADB_IMAGE = "mariadb:10.11"

        @Container
        @JvmStatic
        val mariaDBContainer: MariaDBContainer<*> = MariaDBContainer(MARIADB_IMAGE)
            .withDatabaseName("commerce-test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true)

        @JvmStatic
        @DynamicPropertySource
        fun mariaDBProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { mariaDBContainer.jdbcUrl }
            registry.add("spring.datasource.username") { mariaDBContainer.username }
            registry.add("spring.datasource.password") { mariaDBContainer.password }
            registry.add("spring.datasource.driver-class-name") { "org.mariadb.jdbc.Driver" }
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
            registry.add("spring.jpa.properties.hibernate.dialect") {
                "org.hibernate.dialect.MariaDBDialect"
            }
        }
    }
}
