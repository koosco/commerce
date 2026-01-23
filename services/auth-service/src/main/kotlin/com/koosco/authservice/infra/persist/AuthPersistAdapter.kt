package com.koosco.authservice.infra.persist

import com.koosco.authservice.application.port.AuthPersistPort
import com.koosco.authservice.domain.entity.UserAuth
import org.springframework.stereotype.Repository

@Repository
class AuthPersistAdapter(private val jpaAuthRepository: JpaAuthRepository) : AuthPersistPort {

    override fun save(userAuth: UserAuth): UserAuth = jpaAuthRepository.save(userAuth)

    override fun findByEmail(email: String): UserAuth? = jpaAuthRepository.findByEmail(email)
}
