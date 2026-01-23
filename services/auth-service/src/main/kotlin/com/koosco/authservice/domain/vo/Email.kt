package com.koosco.authservice.domain.vo

import com.koosco.common.core.error.CommonErrorCode
import com.koosco.common.core.exception.BaseException

@JvmInline
value class Email private constructor(val value: String) {

    init {
        require(isValid(value)) {
            "Invalid email format: $value"
        }
    }

    companion object {

        private val emailRegex =
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()

        fun of(rawEmail: String?): Email {
            if (rawEmail.isNullOrBlank()) {
                throw BaseException(CommonErrorCode.INVALID_INPUT, "Email cannot be empty")
            }

            return try {
                Email(rawEmail)
            } catch (e: IllegalArgumentException) {
                throw BaseException(
                    CommonErrorCode.INVALID_INPUT,
                    e.message ?: "Invalid email format",
                )
            }
        }

        fun isValid(email: String): Boolean = emailRegex.matches(email)
    }

    override fun toString(): String = value
}
