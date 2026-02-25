package com.koosco.paymentservice.api

import com.koosco.common.core.exception.NotFoundException
import com.koosco.paymentservice.application.command.CancelPaymentCommand
import com.koosco.paymentservice.application.command.PaymentApproveCommand
import com.koosco.paymentservice.application.port.PaymentRepository
import com.koosco.paymentservice.application.usecase.ApprovePaymentUseCase
import com.koosco.paymentservice.application.usecase.CancelPaymentUseCase
import com.koosco.paymentservice.common.PaymentErrorCode
import com.koosco.paymentservice.domain.vo.Money
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@Tag(name = "Payment Widget", description = "Toss Payments widget integration APIs")
@RestController
@RequestMapping("/api/payments")
class WidgetController(
    private val approvePaymentUseCase: ApprovePaymentUseCase,
    private val cancelPaymentUseCase: CancelPaymentUseCase,
    private val paymentRepository: PaymentRepository,
) {

    @Operation(
        summary = "결제 승인",
        description = """
            Toss Payments 위젯에서 결제를 승인합니다.
            클라이언트에서 받은 paymentKey, orderId, amount를 통해 결제를 최종 승인합니다.
        """,
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "결제 승인 성공",
                content = [Content(mediaType = "application/json")],
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (금액 불일치, 유효하지 않은 paymentKey 등)",
                content = [Content(mediaType = "application/json")],
            ),
            ApiResponse(
                responseCode = "500",
                description = "서버 오류",
                content = [Content(mediaType = "application/json")],
            ),
        ],
    )
    @PostMapping("/confirm")
    fun confirmPayment(
        @RequestBody
        @Schema(description = "결제 승인 요청 정보")
        body: PaymentConfirmRequest,
    ): Map<String, Any> {
        val payment = paymentRepository.findByOrderId(body.orderId)
            ?: throw NotFoundException(PaymentErrorCode.PAYMENT_NOT_FOUND)

        approvePaymentUseCase.execute(
            paymentId = payment.paymentId,
            command = PaymentApproveCommand(
                paymentId = payment.paymentId,
                orderId = body.orderId,
                amount = Money(body.amount.toLong()),
            ),
            idempotencyKey = body.paymentKey,
        )

        return mapOf("success" to true)
    }

    @Operation(
        summary = "결제 취소 (부분/전액)",
        description = "결제를 부분 또는 전액 취소합니다. 취소 금액이 원 결제 금액 미만이면 부분 취소로 처리됩니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "결제 취소 성공",
                content = [Content(mediaType = "application/json")],
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (취소 불가능 상태, 취소 금액 초과 등)",
                content = [Content(mediaType = "application/json")],
            ),
            ApiResponse(
                responseCode = "404",
                description = "결제 정보 없음",
                content = [Content(mediaType = "application/json")],
            ),
        ],
    )
    @PostMapping("/{paymentId}/cancel")
    fun cancelPayment(@PathVariable paymentId: UUID, @RequestBody body: PaymentCancelRequest): Map<String, Any> {
        cancelPaymentUseCase.execute(
            CancelPaymentCommand(
                paymentId = paymentId,
                cancelAmount = body.cancelAmount,
            ),
        )

        return mapOf("success" to true)
    }
}
