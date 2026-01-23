package com.koosco.authservice.domain.vo

@JvmInline
value class Password(val value: String) {

    init {
        require(value.isNotBlank()) {
            "Password cannot be blank"
        }
        require(value.length >= 8) {
            "Password must be at least 8 characters long"
        }
    }

    companion object {
        fun of(raw: String?): Password {
            require(!raw.isNullOrBlank()) { "Password cannot be null or blank" }
            return Password(raw)
        }
    }
}
