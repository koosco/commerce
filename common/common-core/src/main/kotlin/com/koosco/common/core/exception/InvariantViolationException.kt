package com.koosco.common.core.exception

class InvariantViolationException(
    message: String,
    cause: Throwable? = null,
) : IllegalStateException(message, cause)
