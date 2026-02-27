rootProject.name = "commerce"

buildCache {
    local {
        directory = File(rootDir, ".gradle/build-cache")
        removeUnusedEntriesAfterDays = 7
    }
}

// Common modules
include(":common:common-core")
include(":common:common-security")
include(":common:common-observability")

// Service modules
include(":services:user-service")
include(":services:catalog-service")
include(":services:inventory-service")
include(":services:order-service")
include(":services:payment-service")
