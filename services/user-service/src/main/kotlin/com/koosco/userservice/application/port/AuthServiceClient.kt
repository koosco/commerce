package com.koosco.userservice.application.port

import com.koosco.userservice.domain.enums.AuthProvider
import com.koosco.userservice.domain.enums.UserRole

interface AuthServiceClient {

    fun notifyUserCreated(userId: Long, password: String, email: String, provider: AuthProvider?, role: UserRole)
}
