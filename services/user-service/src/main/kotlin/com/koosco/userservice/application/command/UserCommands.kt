package com.koosco.userservice.application.command

import com.koosco.userservice.domain.enums.AuthProvider

/**
 * 사용자 생성 command
 */
data class CreateUserCommand(
    val email: String,
    val password: String,
    val name: String,
    val phone: String?,
    val provider: AuthProvider,
)

/**
 * 사용자 정보 상세 command
 */
data class GetUserDetailCommand(val userId: Long)

/**
 * 사용자 정보 갱신 command
 */
data class UpdateUserCommand(val userId: Long, val name: String?, val phone: String?)

/**
 * 사용자 삭제 command
 */
data class DeleteUserCommand(val userId: Long)
