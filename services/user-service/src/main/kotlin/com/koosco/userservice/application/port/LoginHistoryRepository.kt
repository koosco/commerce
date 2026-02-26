package com.koosco.userservice.application.port

import com.koosco.userservice.domain.entity.LoginHistory

interface LoginHistoryRepository {
    fun save(loginHistory: LoginHistory): LoginHistory
}
