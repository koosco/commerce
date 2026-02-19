package com.koosco.userservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.ConflictException
import com.koosco.userservice.application.command.CreateUserCommand
import com.koosco.userservice.application.port.UserRepository
import com.koosco.userservice.common.MemberErrorCode
import com.koosco.userservice.domain.entity.Member
import com.koosco.userservice.domain.vo.Email
import com.koosco.userservice.domain.vo.EncryptedPassword
import com.koosco.userservice.domain.vo.Phone
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional

@UseCase
class RegisterUseCase(private val userRepository: UserRepository, private val passwordEncoder: PasswordEncoder) {

    @Transactional
    fun execute(command: CreateUserCommand) {
        val encodedPassword = EncryptedPassword.of(passwordEncoder.encode(command.password))

        try {
            val member = Member.create(
                email = Email.of(command.email),
                name = command.name,
                phone = Phone.of(command.phone),
                passwordHash = encodedPassword,
            )

            userRepository.save(member)
        } catch (ex: DataIntegrityViolationException) {
            throw ConflictException(
                MemberErrorCode.EMAIL_ALREADY_EXISTS,
                "이미 존재하는 이메일입니다.",
                ex,
            )
        }
    }
}
