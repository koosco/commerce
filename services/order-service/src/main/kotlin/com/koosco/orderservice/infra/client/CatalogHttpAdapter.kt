package com.koosco.orderservice.infra.client

import com.koosco.common.core.exception.ServiceUnavailableException
import com.koosco.orderservice.application.port.CatalogQueryPort
import com.koosco.orderservice.common.error.OrderErrorCode
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException

@Profile("!test")
@Component
class CatalogHttpAdapter(private val catalogRestClient: RestClient) : CatalogQueryPort {

    private val log = LoggerFactory.getLogger(javaClass)

    @CircuitBreaker(name = "catalogService", fallbackMethod = "getSkuInfosFallback")
    override fun getSkuInfos(skuIds: List<Long>): Map<Long, CatalogQueryPort.SkuInfo> {
        if (skuIds.isEmpty()) return emptyMap()

        try {
            val skuIdsParam = skuIds.joinToString(",")

            val response = catalogRestClient.get()
                .uri("/internal/products/skus?skuIds={skuIds}", skuIdsParam)
                .retrieve()
                .body(object : ParameterizedTypeReference<CatalogApiResponse<List<SkuInfoDto>>>() {})

            return response?.data
                ?.associate { it.skuPkId to it.toDomain() }
                ?: emptyMap()
        } catch (e: RestClientResponseException) {
            log.error("catalog-service SKU 조회 실패 (status={})", e.statusCode.value(), e)
            throw ServiceUnavailableException(
                OrderErrorCode.CATALOG_SERVICE_UNAVAILABLE,
                "상품 정보 조회 API 호출 실패(status=${e.statusCode.value()})",
                e,
            )
        } catch (e: ResourceAccessException) {
            log.error("catalog-service 연결 실패", e)
            throw ServiceUnavailableException(
                OrderErrorCode.CATALOG_SERVICE_UNAVAILABLE,
                OrderErrorCode.CATALOG_SERVICE_UNAVAILABLE.message,
                e,
            )
        }
    }

    @Suppress("unused")
    private fun getSkuInfosFallback(skuIds: List<Long>, ex: Throwable): Map<Long, CatalogQueryPort.SkuInfo> {
        log.error("catalog-service CircuitBreaker open — 주문 생성 불가", ex)
        throw ServiceUnavailableException(
            OrderErrorCode.CATALOG_SERVICE_UNAVAILABLE,
            OrderErrorCode.CATALOG_SERVICE_UNAVAILABLE.message,
            ex,
        )
    }

    /**
     * catalog-service ApiResponse wrapper
     */
    private data class CatalogApiResponse<T>(val status: String?, val data: T?, val error: Any?)

    private data class SkuInfoDto(
        val skuPkId: Long,
        val skuId: String,
        val productId: Long,
        val productName: String,
        val price: Long,
        val status: String,
    ) {
        fun toDomain(): CatalogQueryPort.SkuInfo = CatalogQueryPort.SkuInfo(
            skuPkId = skuPkId,
            skuId = skuId,
            productId = productId,
            productName = productName,
            price = price,
            status = status,
        )
    }
}
