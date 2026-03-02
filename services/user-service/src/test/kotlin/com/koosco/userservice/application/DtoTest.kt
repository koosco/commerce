package com.koosco.userservice.application

import com.koosco.userservice.application.command.DeleteUserCommand
import com.koosco.userservice.application.command.ForceUpdateCommand
import com.koosco.userservice.application.dto.AddressDto
import com.koosco.userservice.application.dto.AuthTokenDto
import com.koosco.userservice.application.dto.UserDto
import com.koosco.userservice.domain.entity.Address
import com.koosco.userservice.domain.entity.Member
import com.koosco.userservice.domain.vo.Email
import com.koosco.userservice.domain.vo.EncryptedPassword
import com.koosco.userservice.domain.vo.Phone
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Application DTOs")
class DtoTest {

    @Nested
    @DisplayName("AddressDto")
    inner class AddressDtoTest {

        @Test
        fun `Address 엔티티에서 AddressDto를 생성한다`() {
            val member = Member.create(
                email = Email.of("test@example.com"),
                name = "홍길동",
                phone = Phone.of("010-1234-5678"),
                passwordHash = EncryptedPassword.of("encoded"),
            )
            val address = Address.create(
                member = member,
                label = "집",
                recipient = "홍길동",
                phone = "010-1234-5678",
                zipCode = "12345",
                address = "서울시",
                addressDetail = "101호",
                isDefault = true,
            )
            address.id = 1L

            val dto = AddressDto.from(address)

            assertThat(dto.id).isEqualTo(1L)
            assertThat(dto.label).isEqualTo("집")
            assertThat(dto.recipient).isEqualTo("홍길동")
            assertThat(dto.phone).isEqualTo("010-1234-5678")
            assertThat(dto.zipCode).isEqualTo("12345")
            assertThat(dto.address).isEqualTo("서울시")
            assertThat(dto.addressDetail).isEqualTo("101호")
            assertThat(dto.isDefault).isTrue()
        }
    }

    @Nested
    @DisplayName("AuthTokenDto")
    inner class AuthTokenDtoTest {

        @Test
        fun `AuthTokenDto를 생성한다`() {
            val dto = AuthTokenDto("access", "refresh", 604800L)

            assertThat(dto.accessToken).isEqualTo("access")
            assertThat(dto.refreshToken).isEqualTo("refresh")
            assertThat(dto.refreshTokenExpiresIn).isEqualTo(604800L)
        }
    }

    @Nested
    @DisplayName("UserDto")
    inner class UserDtoTest {

        @Test
        fun `UserDto를 생성한다`() {
            val dto = UserDto(1L, "test@example.com", "홍길동", "010-1234-5678")

            assertThat(dto.id).isEqualTo(1L)
            assertThat(dto.email).isEqualTo("test@example.com")
            assertThat(dto.name).isEqualTo("홍길동")
            assertThat(dto.phone).isEqualTo("010-1234-5678")
        }
    }

    @Nested
    @DisplayName("ForceUpdateCommand")
    inner class ForceUpdateCommandTest {

        @Test
        fun `of 팩토리 메서드로 생성한다`() {
            val command = ForceUpdateCommand.of(1L, "이름", "010-1234-5678")

            assertThat(command.userId).isEqualTo(1L)
            assertThat(command.name).isEqualTo("이름")
            assertThat(command.phone).isEqualTo("010-1234-5678")
        }
    }

    @Nested
    @DisplayName("DeleteUserCommand")
    inner class DeleteUserCommandTest {

        @Test
        fun `DeleteUserCommand를 생성한다`() {
            val command = DeleteUserCommand(1L)

            assertThat(command.userId).isEqualTo(1L)
        }
    }
}
