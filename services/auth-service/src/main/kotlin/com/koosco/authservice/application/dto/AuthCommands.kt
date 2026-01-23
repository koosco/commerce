package com.koosco.authservice.application.dto

import com.koosco.authservice.domain.vo.AuthProvider

data class CreateUserCommand(
    val userId: Long,
    val email: String,
    val password: String,
    val provider: AuthProvider? = null,
)

data class LoginCommand(val email: String, val password: String)
