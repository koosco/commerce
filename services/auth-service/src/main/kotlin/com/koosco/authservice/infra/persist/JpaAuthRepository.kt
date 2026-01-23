package com.koosco.authservice.infra.persist

import com.koosco.authservice.domain.entity.UserAuth
import org.springframework.data.jpa.repository.JpaRepository

interface JpaAuthRepository : JpaRepository<UserAuth, Long> {
    fun findByEmail(email: String): UserAuth?
}
