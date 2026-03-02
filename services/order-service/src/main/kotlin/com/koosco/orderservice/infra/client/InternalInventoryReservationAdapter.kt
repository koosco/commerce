package com.koosco.orderservice.infra.client

import com.koosco.common.core.exception.ConflictException
import com.koosco.common.core.exception.ServiceUnavailableException
import com.koosco.orderservice.application.port.InventoryReservationPort
import com.koosco.orderservice.common.error.OrderErrorCode
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException

@Profile("!test")
@Component
class InternalInventoryReservationAdapter(
    private val inventoryRestClient: RestClient,
) : InventoryReservationPort {

    override fun reserve(command: InventoryReservationPort.ReserveCommand) {
        val request = ReserveStockRequest(
            orderId = command.orderId,
            items = command.items.map { ReserveStockRequest.ReserveItem(it.skuId.toString(), it.quantity) },
            idempotencyKey = command.idempotencyKey,
            correlationId = command.correlationId,
        )

        try {
            inventoryRestClient.post()
                .uri("/internal/inventories/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity()
        } catch (e: RestClientResponseException) {
            if (e.statusCode.value() == 404 || e.statusCode.value() == 409) {
                throw ConflictException(
                    OrderErrorCode.STOCK_RESERVATION_FAILED,
                    OrderErrorCode.STOCK_RESERVATION_FAILED.message,
                    e,
                )
            }

            throw ServiceUnavailableException(
                OrderErrorCode.INVENTORY_SERVICE_UNAVAILABLE,
                "재고 예약 API 호출 실패(status=${e.statusCode.value()})",
                e,
            )
        } catch (e: ResourceAccessException) {
            throw ServiceUnavailableException(
                OrderErrorCode.INVENTORY_SERVICE_UNAVAILABLE,
                OrderErrorCode.INVENTORY_SERVICE_UNAVAILABLE.message,
                e,
            )
        }
    }

    private data class ReserveStockRequest(
        val orderId: Long,
        val items: List<ReserveItem>,
        val idempotencyKey: String?,
        val correlationId: String,
    ) {
        data class ReserveItem(
            val skuId: String,
            val quantity: Int,
        )
    }
}
