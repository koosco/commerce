package com.koosco.inventoryservice.domain.exception

import com.koosco.common.core.error.ApiError
import com.koosco.common.core.error.ErrorCode
import com.koosco.common.core.exception.BaseException

/**
 * fileName       : BusinessException
 * author         : koo
 * date           : 2025. 12. 19. 오후 3:37
 * description    : 비즈니스 규칙 위반을 나타내는 예외
 */
abstract class BusinessException(
    errorCode: ErrorCode,
    message: String = errorCode.message,
    cause: Throwable? = null,
    fieldErrors: List<ApiError.FieldError>? = null,
) : BaseException(errorCode, message, cause, fieldErrors)
