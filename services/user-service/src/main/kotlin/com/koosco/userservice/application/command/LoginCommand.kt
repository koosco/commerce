package com.koosco.userservice.application.command

data class LoginCommand(val email: String, val password: String, val ip: String, val userAgent: String?)
