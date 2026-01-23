package com.koosco.userservice.infra.client.dto

import com.koosco.userservice.domain.enums.AuthProvider
import com.koosco.userservice.domain.enums.UserRole

data class CreateUserRequest(
    val userId: Long,
    val email: String,
    val password: String,
    val provider: AuthProvider?,
    val role: UserRole,
)

data class DeleteUserRequest(val userId: Long)
