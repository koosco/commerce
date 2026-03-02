package com.koosco.userservice.application

import com.koosco.common.core.exception.BaseException
import com.koosco.common.core.exception.ConflictException
import com.koosco.common.core.exception.NotFoundException
import com.koosco.userservice.application.command.*
import com.koosco.userservice.application.dto.AuthTokenDto
import com.koosco.userservice.application.port.*
import com.koosco.userservice.application.usecase.*
import com.koosco.userservice.domain.entity.Address
import com.koosco.userservice.domain.entity.LoginHistory
import com.koosco.userservice.domain.entity.Member
import com.koosco.userservice.domain.entity.UserIdempotency
import com.koosco.userservice.domain.enums.MemberRole
import com.koosco.userservice.domain.enums.MemberStatus
import com.koosco.userservice.domain.vo.Email
import com.koosco.userservice.domain.vo.EncryptedPassword
import com.koosco.userservice.domain.vo.Phone
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockitoExtension::class)
@DisplayName("User Use Cases")
class UserUseCaseTest {

    private val userRepository: UserRepository = mock()
    private val passwordEncoder: PasswordEncoder = mock()
    private val tokenGeneratorPort: TokenGeneratorPort = mock()
    private val refreshTokenStorePort: RefreshTokenStorePort = mock()
    private val userIdempotencyRepository: UserIdempotencyRepository = mock()
    private val addressRepository: AddressRepository = mock()
    private val loginHistoryRepository: LoginHistoryRepository = mock()

    private fun createMember(
        id: Long = 1L,
        email: String = "test@example.com",
        name: String = "홍길동",
        phone: String? = "010-1234-5678",
        password: String = "encodedPassword",
        status: MemberStatus = MemberStatus.ACTIVE,
    ): Member {
        val member = Member(
            id = id,
            email = Email.of(email),
            name = name,
            phone = Phone.of(phone),
            passwordHash = EncryptedPassword.of(password),
            role = MemberRole.USER,
            status = status,
        )
        return member
    }

    private fun createAddress(
        id: Long = 1L,
        member: Member,
        label: String = "집",
        isDefault: Boolean = false,
    ): Address {
        val address = Address.create(
            member = member,
            label = label,
            recipient = "홍길동",
            phone = "010-1234-5678",
            zipCode = "12345",
            address = "서울시 강남구",
            addressDetail = "101호",
            isDefault = isDefault,
        )
        address.id = id
        return address
    }

    @Nested
    @DisplayName("RegisterUseCase")
    inner class RegisterUseCaseTest {

        private val useCase = RegisterUseCase(userRepository, passwordEncoder, userIdempotencyRepository)

        @Test
        fun `회원가입에 성공한다`() {
            val command = CreateUserCommand("test@example.com", "password", "홍길동", "010-1234-5678")
            val member = createMember()

            whenever(passwordEncoder.encode("password")).thenReturn("encodedPassword")
            whenever(userRepository.save(any())).thenReturn(member)

            useCase.execute(command)

            verify(userRepository).save(any())
        }

        @Test
        fun `멱등성 키가 있고 이미 존재하면 조기 반환한다`() {
            val command = CreateUserCommand("test@example.com", "password", "홍길동", null, "idem-key")

            whenever(
                userIdempotencyRepository.findByIdempotencyKeyAndResourceType("idem-key", UserIdempotency.USER),
            ).thenReturn(
                UserIdempotency.create("idem-key", UserIdempotency.USER, 1L),
            )

            useCase.execute(command)

            verify(userRepository, never()).save(any())
        }

        @Test
        fun `멱등성 키가 있고 신규이면 멱등성 정보를 저장한다`() {
            val command = CreateUserCommand("test@example.com", "password", "홍길동", null, "idem-key")
            val member = createMember()

            whenever(
                userIdempotencyRepository.findByIdempotencyKeyAndResourceType("idem-key", UserIdempotency.USER),
            ).thenReturn(null)
            whenever(passwordEncoder.encode("password")).thenReturn("encodedPassword")
            whenever(userRepository.save(any())).thenReturn(member)

            useCase.execute(command)

            verify(userIdempotencyRepository).save(any())
        }

        @Test
        fun `이메일 중복 시 ConflictException이 발생한다`() {
            val command = CreateUserCommand("test@example.com", "password", "홍길동", null)

            whenever(passwordEncoder.encode("password")).thenReturn("encodedPassword")
            whenever(userRepository.save(any())).thenThrow(DataIntegrityViolationException("duplicate"))

            assertThatThrownBy { useCase.execute(command) }
                .isInstanceOf(ConflictException::class.java)
        }
    }

    @Nested
    @DisplayName("LoginUseCase")
    inner class LoginUseCaseTest {

        private val loginUserRepository: UserRepository = mock()
        private val loginPasswordEncoder: PasswordEncoder = mock()
        private val loginTokenGeneratorPort: TokenGeneratorPort = mock()
        private val loginRefreshTokenStorePort: RefreshTokenStorePort = mock()
        private val loginHistoryRepo: LoginHistoryRepository = mock()

        private val useCase = LoginUseCase(
            loginUserRepository,
            loginPasswordEncoder,
            loginTokenGeneratorPort,
            loginRefreshTokenStorePort,
            loginHistoryRepo,
        )

        private val loginCommand = LoginCommand("test@example.com", "password", "127.0.0.1", "Mozilla/5.0")

        @Test
        fun `로그인에 성공하면 토큰을 반환한다`() {
            val member = createMember()
            val tokens = AuthTokenDto("accessToken", "refreshToken", 604800L)

            whenever(loginUserRepository.findByEmail(Email.of("test@example.com"))).thenReturn(member)
            whenever(loginPasswordEncoder.matches("password", "encodedPassword")).thenReturn(true)
            whenever(loginTokenGeneratorPort.generateTokens(eq(1L), eq("test@example.com"), any())).thenReturn(tokens)
            whenever(loginHistoryRepo.save(any())).thenReturn(
                LoginHistory.success(1L, "127.0.0.1", "Mozilla/5.0"),
            )

            val result = useCase.execute(loginCommand)

            assertThat(result.accessToken).isEqualTo("accessToken")
            assertThat(result.refreshToken).isEqualTo("refreshToken")
            verify(loginRefreshTokenStorePort).save(eq(1L), eq("refreshToken"), eq(604800L))
        }

        @Test
        fun `사용자가 없으면 NotFoundException이 발생한다`() {
            whenever(loginUserRepository.findByEmail(Email.of("test@example.com"))).thenReturn(null)
            whenever(loginHistoryRepo.save(any())).thenReturn(
                LoginHistory.failure(0L, "127.0.0.1", "Mozilla/5.0", "USER_NOT_FOUND"),
            )

            assertThatThrownBy { useCase.execute(loginCommand) }
                .isInstanceOf(NotFoundException::class.java)
        }

        @Test
        fun `비밀번호가 null이면 NotFoundException이 발생한다`() {
            val member = Member(
                id = 1L,
                email = Email.of("test@example.com"),
                name = "홍길동",
                phone = null,
                passwordHash = null,
                role = MemberRole.USER,
                status = MemberStatus.ACTIVE,
            )

            whenever(loginUserRepository.findByEmail(Email.of("test@example.com"))).thenReturn(member)
            whenever(loginHistoryRepo.save(any())).thenReturn(
                LoginHistory.failure(1L, "127.0.0.1", "Mozilla/5.0", "INVALID_PASSWORD"),
            )

            assertThatThrownBy { useCase.execute(loginCommand) }
                .isInstanceOf(NotFoundException::class.java)
        }

        @Test
        fun `비밀번호가 틀리면 NotFoundException이 발생한다`() {
            val member = createMember()

            whenever(loginUserRepository.findByEmail(Email.of("test@example.com"))).thenReturn(member)
            whenever(loginPasswordEncoder.matches("password", "encodedPassword")).thenReturn(false)
            whenever(loginHistoryRepo.save(any())).thenReturn(
                LoginHistory.failure(1L, "127.0.0.1", "Mozilla/5.0", "INVALID_PASSWORD"),
            )

            assertThatThrownBy { useCase.execute(loginCommand) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("RefreshTokenUseCase")
    inner class RefreshTokenUseCaseTest {

        private val useCase = RefreshTokenUseCase(tokenGeneratorPort, refreshTokenStorePort, userRepository)

        @Test
        fun `유효한 리프레시 토큰으로 새 토큰을 발급한다`() {
            val member = createMember()
            val tokens = AuthTokenDto("newAccess", "newRefresh", 604800L)

            whenever(tokenGeneratorPort.validateRefreshToken("refreshToken")).thenReturn(true)
            whenever(tokenGeneratorPort.extractUserId("refreshToken")).thenReturn(1L)
            whenever(refreshTokenStorePort.findByUserId(1L)).thenReturn("refreshToken")
            whenever(userRepository.findActiveUserById(1L)).thenReturn(member)
            whenever(tokenGeneratorPort.generateTokens(eq(1L), eq("test@example.com"), any())).thenReturn(tokens)

            val result = useCase.execute("refreshToken")

            assertThat(result.accessToken).isEqualTo("newAccess")
            verify(refreshTokenStorePort).save(1L, "newRefresh", 604800L)
        }

        @Test
        fun `유효하지 않은 리프레시 토큰이면 BaseException이 발생한다`() {
            whenever(tokenGeneratorPort.validateRefreshToken("invalid")).thenReturn(false)

            assertThatThrownBy { useCase.execute("invalid") }
                .isInstanceOf(BaseException::class.java)
        }

        @Test
        fun `저장된 토큰이 없으면 BaseException이 발생한다`() {
            whenever(tokenGeneratorPort.validateRefreshToken("refreshToken")).thenReturn(true)
            whenever(tokenGeneratorPort.extractUserId("refreshToken")).thenReturn(1L)
            whenever(refreshTokenStorePort.findByUserId(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute("refreshToken") }
                .isInstanceOf(BaseException::class.java)
        }

        @Test
        fun `저장된 토큰과 불일치하면 BaseException이 발생한다`() {
            whenever(tokenGeneratorPort.validateRefreshToken("refreshToken")).thenReturn(true)
            whenever(tokenGeneratorPort.extractUserId("refreshToken")).thenReturn(1L)
            whenever(refreshTokenStorePort.findByUserId(1L)).thenReturn("differentToken")

            assertThatThrownBy { useCase.execute("refreshToken") }
                .isInstanceOf(BaseException::class.java)
        }

        @Test
        fun `사용자를 찾을 수 없으면 NotFoundException이 발생한다`() {
            whenever(tokenGeneratorPort.validateRefreshToken("refreshToken")).thenReturn(true)
            whenever(tokenGeneratorPort.extractUserId("refreshToken")).thenReturn(1L)
            whenever(refreshTokenStorePort.findByUserId(1L)).thenReturn("refreshToken")
            whenever(userRepository.findActiveUserById(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute("refreshToken") }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("LogoutUseCase")
    inner class LogoutUseCaseTest {

        private val useCase = LogoutUseCase(refreshTokenStorePort)

        @Test
        fun `로그아웃 시 리프레시 토큰을 삭제한다`() {
            useCase.execute(1L)

            verify(refreshTokenStorePort).delete(1L)
        }
    }

    @Nested
    @DisplayName("GetUserDetailUseCase")
    inner class GetUserDetailUseCaseTest {

        private val useCase = GetUserDetailUseCase(userRepository)

        @Test
        fun `사용자 정보를 조회한다`() {
            val member = createMember()
            whenever(userRepository.findActiveUserById(1L)).thenReturn(member)

            val result = useCase.execute(GetUserDetailCommand(1L))

            assertThat(result.id).isEqualTo(1L)
            assertThat(result.email).isEqualTo("test@example.com")
            assertThat(result.name).isEqualTo("홍길동")
            assertThat(result.phone).isEqualTo("010-1234-5678")
        }

        @Test
        fun `사용자를 찾을 수 없으면 NotFoundException이 발생한다`() {
            whenever(userRepository.findActiveUserById(999L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(GetUserDetailCommand(999L)) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("UpdateMeUseCase")
    inner class UpdateMeUseCaseTest {

        private val useCase = UpdateMeUseCase(userRepository)

        @Test
        fun `사용자 정보를 수정한다`() {
            val member = createMember()
            whenever(userRepository.findActiveUserById(1L)).thenReturn(member)

            useCase.execute(UpdateUserCommand(1L, "김철수", "010-9999-8888"))

            assertThat(member.name).isEqualTo("김철수")
        }

        @Test
        fun `사용자를 찾을 수 없으면 NotFoundException이 발생한다`() {
            whenever(userRepository.findActiveUserById(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(UpdateUserCommand(1L, "김철수", null)) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("DeleteMeUseCase")
    inner class DeleteMeUseCaseTest {

        private val useCase = DeleteMeUseCase(userRepository, refreshTokenStorePort)

        @Test
        fun `회원 탈퇴하면 WITHDRAWN 상태가 되고 리프레시 토큰이 삭제된다`() {
            val member = createMember()
            whenever(userRepository.findActiveUserById(1L)).thenReturn(member)

            useCase.execute(1L)

            assertThat(member.status).isEqualTo(MemberStatus.WITHDRAWN)
            verify(refreshTokenStorePort).delete(1L)
        }

        @Test
        fun `사용자를 찾을 수 없으면 NotFoundException이 발생한다`() {
            whenever(userRepository.findActiveUserById(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(1L) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("ForceUpdateUseCase")
    inner class ForceUpdateUseCaseTest {

        private val useCase = ForceUpdateUseCase(userRepository)

        @Test
        fun `관리자가 사용자 정보를 수정한다`() {
            val member = createMember()
            whenever(userRepository.findActiveUserById(1L)).thenReturn(member)

            useCase.execute(ForceUpdateCommand.of(1L, "관리자변경", "010-9999-8888"))

            assertThat(member.name).isEqualTo("관리자변경")
        }

        @Test
        fun `사용자를 찾을 수 없으면 NotFoundException이 발생한다`() {
            whenever(userRepository.findActiveUserById(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(ForceUpdateCommand.of(1L, "변경", null)) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("ForceDeleteUseCase")
    inner class ForceDeleteUseCaseTest {

        private val useCase = ForceDeleteUseCase(userRepository, refreshTokenStorePort)

        @Test
        fun `관리자가 사용자를 잠금 처리한다`() {
            val member = createMember()
            whenever(userRepository.findActiveUserById(1L)).thenReturn(member)

            useCase.execute(ForceDeleteCommand(1L))

            assertThat(member.status).isEqualTo(MemberStatus.LOCKED)
            verify(refreshTokenStorePort).delete(1L)
        }

        @Test
        fun `사용자를 찾을 수 없으면 NotFoundException이 발생한다`() {
            whenever(userRepository.findActiveUserById(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(ForceDeleteCommand(1L)) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("CreateAddressUseCase")
    inner class CreateAddressUseCaseTest {

        private val useCase = CreateAddressUseCase(userRepository, addressRepository, userIdempotencyRepository)

        private fun addressCommand(userId: Long = 1L, isDefault: Boolean = false, idempotencyKey: String? = null) =
            CreateAddressCommand(
                userId = userId,
                label = "집",
                recipient = "홍길동",
                phone = "010-1234-5678",
                zipCode = "12345",
                address = "서울시 강남구",
                addressDetail = "101호",
                isDefault = isDefault,
                idempotencyKey = idempotencyKey,
            )

        @Test
        fun `주소를 생성한다`() {
            val member = createMember()
            val address = createAddress(member = member)

            whenever(userRepository.findActiveUserById(1L)).thenReturn(member)
            whenever(addressRepository.countByMemberId(1L)).thenReturn(0)
            whenever(addressRepository.save(any())).thenReturn(address)

            val result = useCase.execute(addressCommand())

            assertThat(result.label).isEqualTo("집")
            verify(addressRepository).save(any())
        }

        @Test
        fun `기본 배송지로 등록 시 기존 기본 배송지를 해제한다`() {
            val member = createMember()
            val existingDefault = createAddress(id = 2L, member = member, isDefault = true)
            val newAddress = createAddress(id = 3L, member = member, isDefault = true)

            whenever(userRepository.findActiveUserById(1L)).thenReturn(member)
            whenever(addressRepository.countByMemberId(1L)).thenReturn(1)
            whenever(addressRepository.findByMemberId(1L)).thenReturn(listOf(existingDefault))
            whenever(addressRepository.save(any())).thenReturn(newAddress)

            useCase.execute(addressCommand(isDefault = true))

            assertThat(existingDefault.isDefault).isFalse()
        }

        @Test
        fun `최대 주소 수를 초과하면 BaseException이 발생한다`() {
            val member = createMember()
            whenever(userRepository.findActiveUserById(1L)).thenReturn(member)
            whenever(addressRepository.countByMemberId(1L)).thenReturn(10)

            assertThatThrownBy { useCase.execute(addressCommand()) }
                .isInstanceOf(BaseException::class.java)
        }

        @Test
        fun `사용자를 찾을 수 없으면 NotFoundException이 발생한다`() {
            whenever(userRepository.findActiveUserById(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(addressCommand()) }
                .isInstanceOf(NotFoundException::class.java)
        }

        @Test
        fun `멱등성 키가 있고 기존 주소가 있으면 기존 주소를 반환한다`() {
            val member = createMember()
            val address = createAddress(member = member)
            val idempotency = UserIdempotency.create("idem-key", UserIdempotency.ADDRESS, 1L)

            whenever(
                userIdempotencyRepository.findByIdempotencyKeyAndResourceType("idem-key", UserIdempotency.ADDRESS),
            ).thenReturn(idempotency)
            whenever(addressRepository.findByIdAndMemberId(1L, 1L)).thenReturn(address)

            val result = useCase.execute(addressCommand(idempotencyKey = "idem-key"))

            assertThat(result.label).isEqualTo("집")
            verify(userRepository, never()).findActiveUserById(any())
        }

        @Test
        fun `멱등성 키가 있지만 기존 주소가 없으면 새로 생성한다`() {
            val member = createMember()
            val address = createAddress(member = member)
            val idempotency = UserIdempotency.create("idem-key", UserIdempotency.ADDRESS, 99L)

            whenever(
                userIdempotencyRepository.findByIdempotencyKeyAndResourceType("idem-key", UserIdempotency.ADDRESS),
            ).thenReturn(idempotency)
            whenever(addressRepository.findByIdAndMemberId(99L, 1L)).thenReturn(null)
            whenever(userRepository.findActiveUserById(1L)).thenReturn(member)
            whenever(addressRepository.countByMemberId(1L)).thenReturn(0)
            whenever(addressRepository.save(any())).thenReturn(address)

            val result = useCase.execute(addressCommand(idempotencyKey = "idem-key"))

            assertThat(result.label).isEqualTo("집")
            verify(userIdempotencyRepository).save(any())
        }

        @Test
        fun `멱등성 키가 있고 기존 멱등성 레코드가 없으면 새로 생성하고 멱등성을 저장한다`() {
            val member = createMember()
            val address = createAddress(member = member)

            whenever(
                userIdempotencyRepository.findByIdempotencyKeyAndResourceType("idem-key", UserIdempotency.ADDRESS),
            ).thenReturn(null)
            whenever(userRepository.findActiveUserById(1L)).thenReturn(member)
            whenever(addressRepository.countByMemberId(1L)).thenReturn(0)
            whenever(addressRepository.save(any())).thenReturn(address)

            useCase.execute(addressCommand(idempotencyKey = "idem-key"))

            verify(userIdempotencyRepository).save(any())
        }
    }

    @Nested
    @DisplayName("GetAddressesUseCase")
    inner class GetAddressesUseCaseTest {

        private val useCase = GetAddressesUseCase(userRepository, addressRepository)

        @Test
        fun `주소 목록을 조회한다`() {
            val member = createMember()
            val addresses = listOf(
                createAddress(id = 1L, member = member, label = "집"),
                createAddress(id = 2L, member = member, label = "회사"),
            )

            whenever(userRepository.findActiveUserById(1L)).thenReturn(member)
            whenever(addressRepository.findByMemberId(1L)).thenReturn(addresses)

            val result = useCase.execute(GetAddressesCommand(1L))

            assertThat(result).hasSize(2)
        }

        @Test
        fun `사용자를 찾을 수 없으면 NotFoundException이 발생한다`() {
            whenever(userRepository.findActiveUserById(1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(GetAddressesCommand(1L)) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("DeleteAddressUseCase")
    inner class DeleteAddressUseCaseTest {

        private val useCase = DeleteAddressUseCase(addressRepository)

        @Test
        fun `주소를 삭제한다`() {
            val member = createMember()
            val address = createAddress(member = member)

            whenever(addressRepository.findByIdAndMemberId(1L, 1L)).thenReturn(address)
            whenever(addressRepository.save(any())).thenReturn(address)

            useCase.execute(DeleteAddressCommand(1L, 1L))

            assertThat(address.deletedAt).isNotNull()
            verify(addressRepository).save(address)
        }

        @Test
        fun `주소를 찾을 수 없으면 NotFoundException이 발생한다`() {
            whenever(addressRepository.findByIdAndMemberId(1L, 1L)).thenReturn(null)

            assertThatThrownBy { useCase.execute(DeleteAddressCommand(1L, 1L)) }
                .isInstanceOf(NotFoundException::class.java)
        }
    }
}
