package com.koosco.authservice.api.dto.request

import com.koosco.authservice.application.dto.LoginCommand

data class LoginRequest(val email: String, val password: String) {
    fun toCommand(): LoginCommand = LoginCommand(
        email = this.email,
        password = this.password,
    )
}
