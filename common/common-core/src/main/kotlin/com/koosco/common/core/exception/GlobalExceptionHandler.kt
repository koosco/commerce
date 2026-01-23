package com.koosco.common.core.exception

import com.koosco.common.core.error.ApiError
import com.koosco.common.core.error.CommonErrorCode
import com.koosco.common.core.response.ApiResponse
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * =========================
     * 1. Application-level Exception
     * =========================
     *
     * BaseException 계열은 비즈니스/도메인 레벨에서
     * "의미 있는 실패"를 표현하기 위한 예외이다.
     *
     * - ErrorCode를 반드시 포함한다.
     * - HTTP 상태 코드와 1:1로 매핑된다.
     * - 클라이언트에게 그대로 응답으로 내려간다.
     */
    @ExceptionHandler(BaseException::class)
    fun handleBaseException(e: BaseException): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("BaseException occurred: [{}] {}", e.errorCode.code, e.message)
        return ResponseEntity
            .status(e.errorCode.status)
            .body(ApiResponse.error(e.toApiError()))
    }

    /**
     * =========================
     * 2. Validation Exceptions
     * =========================
     *
     * 요청 파라미터/바디 검증 실패.
     * - 컨트롤러 진입 시점에서 발생
     * - 트랜잭션이 시작되기 전에 실패
     * - 모든 검증 오류는 VALIDATION_ERROR로 통합 응답
     */

    /**
     * @Valid 사용 시 RequestBody 검증 실패
     * (POST, PUT, PATCH Body)
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        e: MethodArgumentNotValidException,
    ): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("Validation error: {}", e.message)
        val fieldErrors = e.bindingResult.fieldErrors.map { error ->
            ApiError.FieldError(
                field = error.field,
                value = error.rejectedValue,
                reason = error.defaultMessage ?: "Invalid value",
            )
        }
        return ResponseEntity
            .status(CommonErrorCode.VALIDATION_ERROR.status)
            .body(ApiResponse.error(CommonErrorCode.VALIDATION_ERROR, fieldErrors = fieldErrors))
    }

    /**
     * @ModelAttribute / Query Parameter 바인딩 실패
     * (주로 GET 요청)
     */
    @ExceptionHandler(BindException::class)
    fun handleBindException(e: BindException): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("Bind error: {}", e.message)
        val fieldErrors = e.bindingResult.fieldErrors.map { error ->
            ApiError.FieldError(
                field = error.field,
                value = error.rejectedValue,
                reason = error.defaultMessage ?: "Invalid value",
            )
        }
        return ResponseEntity
            .status(CommonErrorCode.VALIDATION_ERROR.status)
            .body(ApiResponse.error(CommonErrorCode.VALIDATION_ERROR, fieldErrors = fieldErrors))
    }

    /**
     * @Validated 사용 시 PathVariable / RequestParam 검증 실패
     */
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(
        e: ConstraintViolationException,
    ): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("Constraint violation: {}", e.message)
        val fieldErrors = e.constraintViolations.map { violation ->
            val propertyPath = violation.propertyPath.toString()
            val field = propertyPath.substringAfterLast('.')
            ApiError.FieldError(
                field = field,
                value = violation.invalidValue,
                reason = violation.message,
            )
        }
        return ResponseEntity
            .status(CommonErrorCode.VALIDATION_ERROR.status)
            .body(ApiResponse.error(CommonErrorCode.VALIDATION_ERROR, fieldErrors = fieldErrors))
    }

    /**
     * =========================
     * 3. Request Mapping Errors
     * =========================
     *
     * 요청 자체가 잘못된 경우
     * (형식, 타입, 메서드, 미디어 타입 등)
     */

    /**
     * 필수 RequestParam 누락
     */
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameterException(
        e: MissingServletRequestParameterException,
    ): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("Missing parameter: {}", e.parameterName)
        return ResponseEntity
            .status(CommonErrorCode.MISSING_PARAMETER.status)
            .body(
                ApiResponse.error(
                    CommonErrorCode.MISSING_PARAMETER,
                    "Missing parameter: ${e.parameterName}",
                ),
            )
    }

    /**
     * PathVariable / RequestParam 타입 불일치
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatchException(
        e: MethodArgumentTypeMismatchException,
    ): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("Type mismatch: {} = {}", e.name, e.value)
        return ResponseEntity
            .status(CommonErrorCode.INVALID_TYPE.status)
            .body(
                ApiResponse.error(
                    CommonErrorCode.INVALID_TYPE,
                    "Invalid type for parameter: ${e.name}",
                ),
            )
    }

    /**
     * 잘못된 JSON 형식
     * (파싱 불가)
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        e: HttpMessageNotReadableException,
    ): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("Message not readable: {}", e.message)
        return ResponseEntity
            .status(CommonErrorCode.INVALID_INPUT.status)
            .body(ApiResponse.error(CommonErrorCode.INVALID_INPUT, "Invalid request body"))
    }

    /**
     * 지원하지 않는 HTTP Method
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupportedException(
        e: HttpRequestMethodNotSupportedException,
    ): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("Method not supported: {}", e.method)
        return ResponseEntity
            .status(CommonErrorCode.METHOD_NOT_ALLOWED.status)
            .body(
                ApiResponse.error(
                    CommonErrorCode.METHOD_NOT_ALLOWED,
                    "Method ${e.method} not allowed",
                ),
            )
    }

    /**
     * 지원하지 않는 Content-Type
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun handleHttpMediaTypeNotSupportedException(
        e: HttpMediaTypeNotSupportedException,
    ): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("Media type not supported: {}", e.contentType)
        return ResponseEntity
            .status(CommonErrorCode.BAD_REQUEST.status)
            .body(ApiResponse.error(CommonErrorCode.BAD_REQUEST, "Media type not supported"))
    }

    /**
     * =========================
     * 4. System-level Exceptions
     * =========================
     *
     * 시스템 불변식(invariant) 위반 또는
     * 예측 불가능한 오류.
     *
     * - 클라이언트 책임 ❌
     * - 비즈니스 오류 ❌
     * - 반드시 로깅 및 알림 대상
     */

    /**
     * 절대 발생하면 안 되는 상태
     * (버그 또는 트랜잭션/동시성 문제)
     */
    @ExceptionHandler(InvariantViolationException::class)
    fun handleInvariant(ex: InvariantViolationException): ResponseEntity<ApiResponse<Any>> {
        log.error("Invariant violation detected", ex)

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(CommonErrorCode.INTERNAL_SERVER_ERROR))
    }

    /**
     * 최종 fallback.
     * 위에서 처리되지 않은 모든 예외.
     */
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        log.error("Unexpected error occurred", e)
        return ResponseEntity
            .status(CommonErrorCode.INTERNAL_SERVER_ERROR.status)
            .body(ApiResponse.error(CommonErrorCode.INTERNAL_SERVER_ERROR))
    }
}
