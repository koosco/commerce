package com.koosco.commonobservability

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource(
    properties = [
        "spring.application.name=test-app",
        "observability.logging.enabled=true",
        "observability.metrics.enabled=true",
        "observability.actuator.enabled=true",
    ],
)
class CommonObservabilityApplicationTests {

    @Test
    fun contextLoads() {
        // Test that Spring context loads successfully
    }
}
