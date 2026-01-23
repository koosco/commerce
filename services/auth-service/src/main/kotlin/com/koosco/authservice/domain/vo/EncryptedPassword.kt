package com.koosco.authservice.domain.vo

@JvmInline
value class EncryptedPassword(val value: String) {

    init {
        require(value.isNotBlank()) {
            "Encrypted password cannot be blank"
        }
    }

    companion object {
        fun of(raw: String?): EncryptedPassword {
            require(!raw.isNullOrBlank()) { "Encrypted password cannot be null or blank" }
            return EncryptedPassword(raw)
        }
    }
}
