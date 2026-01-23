package com.koosco.orderservice.order.domain.exception

import com.koosco.common.core.error.ApiError
import com.koosco.common.core.error.ErrorCode
import com.koosco.common.core.exception.BaseException

/**
 * fileName       : BusinessException
 * author         : koo
 * date           : 2025. 12. 22. 오전 5:17
 * description    :
 */
abstract class BusinessException(
    errorCode: ErrorCode,
    message: String = errorCode.message,
    cause: Throwable? = null,
    fieldErrors: List<ApiError.FieldError>? = null,
) : BaseException(errorCode, message, cause, fieldErrors)
