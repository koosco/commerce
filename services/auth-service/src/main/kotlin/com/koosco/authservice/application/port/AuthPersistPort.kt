package com.koosco.authservice.application.port

import com.koosco.authservice.domain.entity.UserAuth

interface AuthPersistPort {

    fun save(userAuth: UserAuth): UserAuth

    fun findByEmail(email: String): UserAuth?
}
