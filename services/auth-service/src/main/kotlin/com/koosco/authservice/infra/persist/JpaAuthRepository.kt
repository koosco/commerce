package com.koosco.authservice.infra.persist

import com.koosco.authservice.domain.entity.UserAuth
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

interface JpaAuthRepository : JpaRepository<UserAuth, Long> {
    fun findByEmail(email: String): UserAuth?

    @Transactional
    @Modifying
    @Query("UPDATE UserAuth u SET u.refreshToken = :refreshToken WHERE u.id = :id")
    fun updateRefreshToken(id: Long, refreshToken: String)
}
