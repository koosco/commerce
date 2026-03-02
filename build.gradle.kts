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

    val jacocoCommonExcludes = listOf(
        "*.config.*",
        "*Application*",
        "*.contract.*",
        "*.common.*",
        "*.Q*",
    )

    // PreCommit용: domain, application, api 계층 80% (infra 제외)
    tasks.withType<JacocoCoverageVerification> {
        dependsOn(tasks.withType<JacocoReport>())

        violationRules {
            rule {
                element = "CLASS"
                includes = listOf("*.domain.*", "*.application.*", "*.api.*")
                excludes = jacocoCommonExcludes + listOf("*.infra.*")
                limit {
                    counter = "LINE"
                    minimum = "0.80".toBigDecimal()
                }
            }
        }
    }

    // CI용: 전체 계층 80% (infra 포함)
    tasks.register<JacocoCoverageVerification>("jacocoCiCoverageVerification") {
        dependsOn(tasks.withType<JacocoReport>())

        violationRules {
            rule {
                element = "CLASS"
                includes = listOf("*.domain.*", "*.application.*", "*.api.*", "*.infra.*")
                excludes = jacocoCommonExcludes
                limit {
                    counter = "LINE"
                    minimum = "0.80".toBigDecimal()
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
