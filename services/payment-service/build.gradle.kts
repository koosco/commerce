plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("kapt")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.diffplug.spotless")
}

description = "payment-service"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    // kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // common
    implementation(project(":common:common-core"))
    implementation(project(":common:common-security"))
    implementation(project(":common:common-observability"))
    testImplementation(testFixtures(project(":common:common-core")))

    // spring
    implementation("org.springframework.boot:spring-boot-starter-web")

    // jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

    // kafka
    implementation("org.springframework.kafka:spring-kafka")
    testImplementation("org.springframework.kafka:spring-kafka-test")

    // resilience4j
    implementation("io.github.resilience4j:resilience4j-spring-boot3:2.2.0")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    // swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.1")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // testcontainers
    testImplementation(platform("org.testcontainers:testcontainers-bom:1.19.7"))
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:kafka")
    testImplementation("org.testcontainers:mariadb")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")

    // h2 for unit tests
    testRuntimeOnly("com.h2database:h2")

    // awaitility for async testing
    testImplementation("org.awaitility:awaitility-kotlin:4.2.0")

    // mockito-kotlin for mocking
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
}

spotless {
    kotlin {
        target("src/**/*.kt")
        ktlint("1.5.0")
            .editorConfigOverride(
                mapOf(
                    "max_line_length" to "120",
                    "indent_size" to "4",
                    "insert_final_newline" to "true",
                    "ktlint_standard_no-wildcard-imports" to "disabled",
                ),
            )
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint("1.5.0")
    }
}

tasks.jar {
    enabled = false
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}
