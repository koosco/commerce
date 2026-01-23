package com.koosco.userservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.error.CommonErrorCode
import com.koosco.common.core.exception.ConflictException
import com.koosco.common.core.exception.ExternalServiceException
import com.koosco.common.core.transaction.TransactionRunner
import com.koosco.userservice.application.command.CreateUserCommand
import com.koosco.userservice.application.port.AuthServiceClient
import com.koosco.userservice.application.repository.UserRepository
import com.koosco.userservice.common.UserErrorCode
import com.koosco.userservice.domain.entity.User
import com.koosco.userservice.domain.vo.Email
import com.koosco.userservice.domain.vo.Phone
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException

@UseCase
class RegisterUseCase(
    private val userRepository: UserRepository,
    private val authServiceClient: AuthServiceClient,
    private val transactionRunner: TransactionRunner,
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun execute(command: CreateUserCommand) {
        val user = transactionRunner.run { registerUser(command) }

        try {
            authServiceClient.notifyUserCreated(
                userId = user.id!!,
                password = command.password,
                email = command.email,
                provider = command.provider,
                role = user.role,
            )
        } catch (ex: Exception) {
            runCatching { transactionRunner.runNew { deleteById(user.id!!) } }
                .onFailure {
                    // TODO : 실패 처리
                    logger.error("rollback error", it)
                }
            throw ExternalServiceException(
                CommonErrorCode.EXTERNAL_SERVICE_ERROR,
                "Auth service 호출 실패로 회원가입 취소",
                ex,
            )
        }
    }

    private fun registerUser(command: CreateUserCommand): User {
        try {
            val user = User.createUser(
                email = Email.Companion.of(command.email),
                name = command.name,
                phone = Phone.Companion.of(command.phone),
                provider = command.provider,
            )

            return userRepository.save(user)
        } catch (ex: DataIntegrityViolationException) {
            throw ConflictException(
                UserErrorCode.EMAIL_ALREADY_EXISTS,
                "이미 존재하는 이메일입니다.",
                ex,
            )
        }
    }

    private fun deleteById(userId: Long) {
        userRepository.deleteById(userId)
    }
}
