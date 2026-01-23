package com.koosco.paymentservice.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * fileName       : WidgetController
 * author         : koo
 * date           : 2025. 12. 24. 오후 5:27
 * description    : Toss Payments Widget Integration Controller
 */
@Tag(name = "Payment Widget", description = "Toss Payments widget integration APIs")
@RestController
@RequestMapping("/api/payments")
class WidgetController {

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
    ) {
    }
}
//
// @Controller
// public class WidgetController {
//
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    @RequestMapping(value = "/confirm")
//    public ResponseEntity<JSONObject> confirmPayment(@RequestBody String jsonBody) throws Exception {
//
//        // 토스페이먼츠 API는 시크릿 키를 사용자 ID로 사용하고, 비밀번호는 사용하지 않습니다.
//        // 비밀번호가 없다는 것을 알리기 위해 시크릿 키 뒤에 콜론을 추가합니다.
//        String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
//        Base64.Encoder encoder = Base64.getEncoder();
//        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
//        String authorizations = "Basic " + new String(encodedBytes);
//
//        // 결제를 승인하면 결제수단에서 금액이 차감돼요.
//        URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setRequestProperty("Authorization", authorizations);
//        connection.setRequestProperty("Content-Type", "application/json");
//        connection.setRequestMethod("POST");
//        connection.setDoOutput(true);
//
//        OutputStream outputStream = connection.getOutputStream();
//        outputStream.write(obj.toString().getBytes("UTF-8"));
//
//        int code = connection.getResponseCode();
//        boolean isSuccess = code == 200;
//
//        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();
//
//        // 결제 성공 및 실패 비즈니스 로직을 구현하세요.
//        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
//        JSONObject jsonObject = (JSONObject) parser.parse(reader);
//        responseStream.close();
//
//        return ResponseEntity.status(code).body(jsonObject);
//    }
