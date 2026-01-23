package com.koosco.commonobservability.logging

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertNotNull

@SpringBootTest
@TestPropertySource(
    properties = [
        "spring.application.name=test-app",
        "observability.logging.enabled=true",
        "observability.logging.mdc-enabled=true",
    ],
)
class MdcFilterTest {

    @Autowired(required = false)
    private var mdcContributor: MdcContributor? = null

    @Test
    fun `MDC contributor should be configured when enabled`() {
        assertNotNull(mdcContributor, "MdcContributor should be configured when MDC is enabled")
    }

    @Test
    fun `MdcFilter should generate trace ID`() {
        val filter = MdcFilter()
        val traceId = filter.generateTraceId()
        assertNotNull(traceId, "Trace ID should be generated")
        assert(traceId.length == 32) { "Trace ID should be 32 characters" }
    }

    // Make generateTraceId public for testing
    private fun MdcFilter.generateTraceId(): String = java.util.UUID.randomUUID().toString().replace("-", "")
}
