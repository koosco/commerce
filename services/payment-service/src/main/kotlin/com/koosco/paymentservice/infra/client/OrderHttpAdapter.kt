package com.koosco.paymentservice.infra.client

import com.koosco.common.core.exception.ServiceUnavailableException
import com.koosco.paymentservice.application.port.OrderQueryPort
import com.koosco.paymentservice.common.PaymentErrorCode
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException

@Component
class OrderHttpAdapter(private val orderRestClient: RestClient) : OrderQueryPort {
    private val logger = LoggerFactory.getLogger(javaClass)

    @CircuitBreaker(name = "order-api")
    override fun getOrder(orderId: Long): OrderQueryPort.OrderInfo? {
        try {
            return orderRestClient.get()
                .uri("/internal/orders/{orderId}", orderId)
                .retrieve()
                .body(OrderQueryPort.OrderInfo::class.java)
        } catch (e: RestClientResponseException) {
            if (e.statusCode.value() == 404) {
                return null
            }
            throw ServiceUnavailableException(
                PaymentErrorCode.ORDER_SERVICE_UNAVAILABLE,
                "주문 조회 API 호출 실패(status=${e.statusCode.value()})",
                e,
            )
        } catch (e: ResourceAccessException) {
            throw ServiceUnavailableException(
                PaymentErrorCode.ORDER_SERVICE_UNAVAILABLE,
                PaymentErrorCode.ORDER_SERVICE_UNAVAILABLE.message,
                e,
            )
        }
    }
}
