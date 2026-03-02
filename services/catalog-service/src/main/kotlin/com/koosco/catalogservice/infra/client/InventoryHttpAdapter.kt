package com.koosco.catalogservice.infra.client

import com.koosco.catalogservice.application.port.InventoryQueryPort
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.slf4j.LoggerFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class InventoryHttpAdapter(private val inventoryRestClient: RestClient) : InventoryQueryPort {

    private val logger = LoggerFactory.getLogger(javaClass)

    @CircuitBreaker(name = "inventory-availability", fallbackMethod = "getAvailabilityFallback")
    override fun getAvailability(skuIds: List<String>): Map<String, Boolean> {
        if (skuIds.isEmpty()) return emptyMap()

        return inventoryRestClient.get()
            .uri { builder ->
                builder.path("/internal/inventories/availability")
                    .queryParam("skuIds", skuIds)
                    .build()
            }
            .retrieve()
            .body(object : ParameterizedTypeReference<Map<String, Boolean>>() {})
            ?: skuIds.associateWith { true }
    }

    @Suppress("unused")
    private fun getAvailabilityFallback(skuIds: List<String>, ex: Throwable): Map<String, Boolean> {
        logger.warn(
            "Inventory availability fallback triggered for {} SKUs: {}",
            skuIds.size,
            ex.message,
        )
        // 장애 시 재고 있다고 가정 -> 주문 시 재검증
        return skuIds.associateWith { true }
    }
}
