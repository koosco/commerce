package com.koosco.commonobservability.metrics

import io.micrometer.core.instrument.MeterRegistry
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest
@TestPropertySource(
    properties = [
        "spring.application.name=test-service",
        "observability.metrics.enabled=true",
        "observability.metrics.environment=test",
        "observability.logging.enabled=false",
    ],
)
class MetricsAutoConfigurationTest {

    @Autowired
    private lateinit var meterRegistry: MeterRegistry

    @Autowired
    private lateinit var commonMetricTags: CommonMetricTags

    @Test
    fun `metrics should be auto-configured`() {
        assertNotNull(meterRegistry, "MeterRegistry should be configured")
    }

    @Test
    fun `common metric tags should be configured`() {
        assertNotNull(commonMetricTags, "CommonMetricTags should be configured")

        val tags = commonMetricTags.getCommonTags()
        assertTrue(tags.stream().anyMatch { it.key == "application" }, "Should have application tag")
        assertTrue(tags.stream().anyMatch { it.key == "environment" }, "Should have environment tag")
    }
}
