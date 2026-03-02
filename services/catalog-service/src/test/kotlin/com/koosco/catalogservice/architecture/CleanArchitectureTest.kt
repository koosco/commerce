package com.koosco.catalogservice.architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Clean Architecture 의존성 방향 검증")
class CleanArchitectureTest {

    companion object {
        private const val BASE_PACKAGE = "com.koosco.catalogservice"
        private lateinit var classes: JavaClasses

        @JvmStatic
        @BeforeAll
        fun setup() {
            classes =
                ClassFileImporter()
                    .withImportOption(ImportOption.DoNotIncludeTests())
                    .importPackages(BASE_PACKAGE)
        }
    }

    @Test
    @DisplayName("domain 계층은 api 계층에 의존하지 않는다")
    fun domainShouldNotDependOnApi() {
        noClasses()
            .that()
            .resideInAPackage("..domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..api..")
            .check(classes)
    }

    @Test
    @DisplayName("domain 계층은 application 계층에 의존하지 않는다")
    fun domainShouldNotDependOnApplication() {
        noClasses()
            .that()
            .resideInAPackage("..domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..application..")
            .check(classes)
    }

    @Test
    @DisplayName("domain 계층은 infra 계층에 의존하지 않는다")
    fun domainShouldNotDependOnInfra() {
        noClasses()
            .that()
            .resideInAPackage("..domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..infra..")
            .check(classes)
    }

    @Test
    @DisplayName("application 계층은 api 계층에 의존하지 않는다")
    fun applicationShouldNotDependOnApi() {
        noClasses()
            .that()
            .resideInAPackage("..application..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..api..")
            .check(classes)
    }

    @Test
    @DisplayName("application 계층은 infra 계층에 의존하지 않는다")
    fun applicationShouldNotDependOnInfra() {
        noClasses()
            .that()
            .resideInAPackage("..application..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..infra..")
            .check(classes)
    }
}
