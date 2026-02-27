package com.koosco.catalogservice.common.config

import com.koosco.catalogservice.application.usecase.CalculateSearchQualityUseCase
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration

/**
 * 검색 품질 지표(mAP@k)를 Prometheus 메트릭으로 노출하는 설정.
 * k=10, 30, 60에 대해 각각 Gauge를 등록한다.
 */
@Configuration
class SearchQualityMetricsConfig(
    private val meterRegistry: MeterRegistry,
    private val calculateSearchQualityUseCase: CalculateSearchQualityUseCase,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @PostConstruct
    fun registerMetrics() {
        K_VALUES.forEach { k ->
            Gauge.builder("search.quality.map_at_k") {
                try {
                    calculateSearchQualityUseCase.calculateMapAtK(k)
                } catch (e: Exception) {
                    logger.warn("Failed to calculate mAP@{}: {}", k, e.message)
                    0.0
                }
            }
                .tag("k", k.toString())
                .description("Mean Average Precision at k for search quality")
                .register(meterRegistry)
        }

        logger.info("Registered search quality mAP@k metrics for k={}", K_VALUES)
    }

    companion object {
        private val K_VALUES = listOf(10, 30, 60)
    }
}
