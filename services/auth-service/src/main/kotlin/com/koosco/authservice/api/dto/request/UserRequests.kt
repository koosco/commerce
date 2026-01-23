package com.koosco.authservice.api.dto.request

import com.koosco.authservice.application.dto.CreateUserCommand
import com.koosco.authservice.domain.vo.AuthProvider

data class CreateUserRequest(
    val userId: Long,
    val email: String,
    val password: String,
    val provider: AuthProvider? = null,
) {
    fun toCommand(): CreateUserCommand = CreateUserCommand(
        userId = this.userId,
        email = this.email,
        password = this.password,
        provider = this.provider,
    )
}

data class DeleteUserRequest(val userId: Long)
