package com.koosco.userservice.api

import com.koosco.common.core.annotation.NotBlankIfPresent
import com.koosco.userservice.application.command.CreateUserCommand
import com.koosco.userservice.application.command.UpdateUserCommand
import com.koosco.userservice.domain.enums.AuthProvider
import jakarta.validation.constraints.NotBlank

data class RegisterRequest(
    @field:NotBlank(message = "이메일은 공백일 수 없습니다.")
    val email: String,

    @field:NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    val password: String,

    @field:NotBlank(message = "이름은 공백일 수 없습니다.")
    val name: String,

    @field:NotBlankIfPresent(message = "전화번호는 공백일 수 없습니다.")
    val phone: String? = null,

    val provider: AuthProvider = AuthProvider.LOCAL,
) {
    fun toCommand(): CreateUserCommand = CreateUserCommand(
        email = this.email,
        password = this.password,
        name = this.name,
        phone = this.phone,
        provider = this.provider,
    )
}

data class UpdateRequest(
    @field:NotBlankIfPresent(message = "이름은 공백일 수 없습니다.")
    val name: String?,

    @field:NotBlankIfPresent(message = "전화번호는 공백일 수 없습니다.")
    val phone: String?,
) {
    fun toCommand(userId: Long): UpdateUserCommand = UpdateUserCommand(userId, name, phone)
}
