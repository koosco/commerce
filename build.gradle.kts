plugins {
    kotlin("jvm") version "1.9.25" apply false
    kotlin("plugin.spring") version "1.9.25" apply false
    kotlin("plugin.jpa") version "1.9.25" apply false
    kotlin("kapt") version "1.9.25" apply false
    id("org.springframework.boot") version "3.5.8" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("com.diffplug.spotless") version "7.0.4" apply false
}

allprojects {
    group = "com.koosco"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "jacoco")

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        finalizedBy(tasks.withType<JacocoReport>())
        finalizedBy(tasks.withType<JacocoCoverageVerification>())
    }

    tasks.withType<JacocoReport> {
        dependsOn(tasks.withType<Test>())
        reports {
            xml.required.set(true)
            html.required.set(true)
            csv.required.set(false)
        }
    }

    tasks.withType<JacocoCoverageVerification> {
        dependsOn(tasks.withType<JacocoReport>())

        violationRules {
            rule {
                element = "CLASS"
                includes = listOf("*.domain.*")
                excludes = listOf(
                    "*.api.*",
                    "*.infra.*",
                    "*.config.*",
                    "*Application*",
                    "*.contract.*",
                    "*.common.*",
                )
                limit {
                    counter = "LINE"
                    minimum = "0.80".toBigDecimal()
                }
            }
            rule {
                element = "CLASS"
                includes = listOf("*.application.*")
                excludes = listOf(
                    "*.api.*",
                    "*.infra.*",
                    "*.config.*",
                    "*Application*",
                    "*.contract.*",
                    "*.common.*",
                )
                limit {
                    counter = "LINE"
                    minimum = "0.70".toBigDecimal()
                }
            }
        }
    }

    plugins.withType<JavaPlugin> {
        dependencies {
            "testImplementation"("com.tngtech.archunit:archunit-junit5:1.3.0")
        }
    }
}
