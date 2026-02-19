package com.koosco.userservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.userservice.application.port.RefreshTokenStorePort

@UseCase
class LogoutUseCase(private val refreshTokenStorePort: RefreshTokenStorePort) {
    fun execute(userId: Long) {
        refreshTokenStorePort.delete(userId)
    }
}
