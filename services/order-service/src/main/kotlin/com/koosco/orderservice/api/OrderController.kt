package com.koosco.orderservice.api

import com.koosco.common.core.messaging.MessageContext
import com.koosco.common.core.response.ApiResponse
import com.koosco.commonsecurity.resolver.AuthId
import com.koosco.orderservice.application.command.CancelOrderCommand
import com.koosco.orderservice.application.usecase.CancelOrderByUserUseCase
import com.koosco.orderservice.application.usecase.CreateOrderUseCase
import com.koosco.orderservice.application.usecase.GetOrderDetailUseCase
import com.koosco.orderservice.application.usecase.GetOrdersUseCase
import com.koosco.orderservice.domain.enums.OrderCancelReason
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse

@Tag(name = "Orders", description = "Order management API - Create, view, and manage orders")
@RestController
@RequestMapping("/api/orders")
class OrderController(
    private val createOrderUseCase: CreateOrderUseCase,
    private val getOrdersUseCase: GetOrdersUseCase,
    private val getOrderDetailUseCase: GetOrderDetailUseCase,
    private val cancelOrderByUserUseCase: CancelOrderByUserUseCase,
) {

    @Operation(
        summary = "Create a new order",
        description = "Creates a new order with multiple items. Order starts in CREATED status.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(responseCode = "200", description = "Order created successfully"),
            SwaggerApiResponse(responseCode = "400", description = "Invalid request data"),
            SwaggerApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        ],
    )
    @PostMapping
    fun createOrder(
        @Parameter(hidden = true) @AuthId userId: Long,
        @Valid @RequestBody request: CreateOrderRequest,
    ): ApiResponse<CreateOrderResponse> {
        val result = createOrderUseCase.execute(request.toCommand(userId))

        return ApiResponse.Companion.success(CreateOrderResponse.Companion.from(result))
    }

    @Operation(
        summary = "주문 목록 조회",
        description = "특정 사용자의 주문 목록을 페이징 처리하여 조회합니다. 기본 페이지 크기는 20입니다.",
    )
    @GetMapping
    fun getOrders(
        @AuthId userId: Long,
        @PageableDefault(size = 20, sort = ["createdAt"]) pageable: Pageable,
    ): ApiResponse<Page<OrderResponse>> {
        val result = getOrdersUseCase.execute(userId, pageable)
        return ApiResponse.Companion.success(result.map { OrderResponse.Companion.from(it) })
    }

    @Operation(
        summary = "주문 상세 조회",
        description = "주문 상세 정보를 조회합니다. 주문 아이템 정보를 포함합니다.",
    )
    @GetMapping("/{orderId}")
    fun getOrderDetail(@PathVariable orderId: Long): ApiResponse<OrderDetailResponse> {
        val result = getOrderDetailUseCase.execute(orderId)
        return ApiResponse.Companion.success(OrderDetailResponse.Companion.from(result))
    }

    @Operation(
        summary = "주문 취소",
        description = "사용자 요청으로 주문을 취소합니다.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @PostMapping("/{orderId}/cancel")
    fun cancelOrder(@Parameter(hidden = true) @AuthId userId: Long, @PathVariable orderId: Long): ApiResponse<Any> {
        val command = CancelOrderCommand(
            orderId = orderId,
            reason = OrderCancelReason.USER_REQUEST,
        )
        val context = MessageContext(
            correlationId = UUID.randomUUID().toString(),
            causationId = "rest-cancel-by-user",
        )
        cancelOrderByUserUseCase.execute(command, context)
        return ApiResponse.success()
    }
}
