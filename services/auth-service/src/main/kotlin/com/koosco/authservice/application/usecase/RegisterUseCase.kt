package com.koosco.authservice.application.usecase

import com.koosco.authservice.application.dto.CreateUserCommand
import com.koosco.authservice.application.port.AuthPersistPort
import com.koosco.authservice.domain.entity.UserAuth
import com.koosco.authservice.domain.vo.Email
import com.koosco.authservice.domain.vo.EncryptedPassword
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegisterUseCase(private val authPersistPort: AuthPersistPort, private val passwordEncoder: PasswordEncoder) {

    private val log = LoggerFactory.getLogger(RegisterUseCase::class.java)

    @Transactional
    fun execute(command: CreateUserCommand) {
        val email = Email.of(command.email)
        val encryptedPassword = EncryptedPassword.of(passwordEncoder.encode(command.password))

        val userAuth = UserAuth.createUser(
            userId = command.userId,
            email = email,
            password = encryptedPassword,
            provider = command.provider,
        )

        authPersistPort.save(userAuth)
        log.info("Registered user auth for userId={}", command.userId)
    }
}
