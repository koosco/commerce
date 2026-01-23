package com.koosco.userservice.domain.vo

@JvmInline
value class Phone(val value: String?) {
    init {
        value?.let {
            require(it.isNotBlank()) { "Phone cannot be blank" }
            require(isValid(it)) { "Invalid phone format: $it" }
        }
    }

    companion object {

        private val phoneRegex = Regex("^010-\\d{3,4}-?\\d{4}\$")

        fun of(value: String?): Phone = Phone(value)

        fun isValid(phone: String): Boolean = phoneRegex.matches(phone)
    }
}
