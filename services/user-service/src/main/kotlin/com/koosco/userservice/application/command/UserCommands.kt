package com.koosco.userservice.application.command

data class CreateUserCommand(val email: String, val password: String, val name: String, val phone: String?)

data class GetUserDetailCommand(val userId: Long)

data class UpdateUserCommand(val userId: Long, val name: String?, val phone: String?)

data class DeleteUserCommand(val userId: Long)
