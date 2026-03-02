package com.koosco.userservice.api

import jakarta.servlet.http.HttpServletRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@DisplayName("Request DTOs")
class RequestsTest {

    @Nested
    @DisplayName("RegisterRequest")
    inner class RegisterRequestTest {

        @Test
        fun `toCommand로 CreateUserCommand를 생성한다`() {
            val request = RegisterRequest("test@example.com", "password", "홍길동", "010-1234-5678", "idem")

            val command = request.toCommand()

            assertThat(command.email).isEqualTo("test@example.com")
            assertThat(command.password).isEqualTo("password")
            assertThat(command.name).isEqualTo("홍길동")
            assertThat(command.phone).isEqualTo("010-1234-5678")
            assertThat(command.idempotencyKey).isEqualTo("idem")
        }

        @Test
        fun `phone이 null이면 command에도 null이다`() {
            val request = RegisterRequest("test@example.com", "password", "홍길동")

            val command = request.toCommand()

            assertThat(command.phone).isNull()
        }
    }

    @Nested
    @DisplayName("UpdateRequest")
    inner class UpdateRequestTest {

        @Test
        fun `toCommand로 UpdateUserCommand를 생성한다`() {
            val request = UpdateRequest("김철수", "010-9999-8888")

            val command = request.toCommand(1L)

            assertThat(command.userId).isEqualTo(1L)
            assertThat(command.name).isEqualTo("김철수")
            assertThat(command.phone).isEqualTo("010-9999-8888")
        }
    }

    @Nested
    @DisplayName("LoginRequest")
    inner class LoginRequestTest {

        @Test
        fun `toCommand로 LoginCommand를 생성한다 - remoteAddr 사용`() {
            val request = LoginRequest("test@example.com", "password")
            val httpRequest: HttpServletRequest = mock()

            whenever(httpRequest.getHeader("X-Forwarded-For")).thenReturn(null)
            whenever(httpRequest.getHeader("X-Real-IP")).thenReturn(null)
            whenever(httpRequest.remoteAddr).thenReturn("127.0.0.1")
            whenever(httpRequest.getHeader("User-Agent")).thenReturn("TestAgent")

            val command = request.toCommand(httpRequest)

            assertThat(command.email).isEqualTo("test@example.com")
            assertThat(command.password).isEqualTo("password")
            assertThat(command.ip).isEqualTo("127.0.0.1")
            assertThat(command.userAgent).isEqualTo("TestAgent")
        }

        @Test
        fun `X-Forwarded-For 헤더에서 첫 번째 IP를 추출한다`() {
            val request = LoginRequest("test@example.com", "password")
            val httpRequest: HttpServletRequest = mock()

            whenever(httpRequest.getHeader("X-Forwarded-For")).thenReturn("10.0.0.1, 10.0.0.2")
            whenever(httpRequest.getHeader("User-Agent")).thenReturn(null)

            val command = request.toCommand(httpRequest)

            assertThat(command.ip).isEqualTo("10.0.0.1")
        }

        @Test
        fun `X-Real-IP 헤더에서 IP를 추출한다`() {
            val request = LoginRequest("test@example.com", "password")
            val httpRequest: HttpServletRequest = mock()

            whenever(httpRequest.getHeader("X-Forwarded-For")).thenReturn(null)
            whenever(httpRequest.getHeader("X-Real-IP")).thenReturn("192.168.1.1")
            whenever(httpRequest.getHeader("User-Agent")).thenReturn(null)

            val command = request.toCommand(httpRequest)

            assertThat(command.ip).isEqualTo("192.168.1.1")
        }
    }

    @Nested
    @DisplayName("CreateAddressRequest")
    inner class CreateAddressRequestTest {

        @Test
        fun `toCommand로 CreateAddressCommand를 생성한다`() {
            val request = CreateAddressRequest(
                label = "집",
                recipient = "홍길동",
                phone = "010-1234-5678",
                zipCode = "12345",
                address = "서울시",
                addressDetail = "101호",
                isDefault = true,
                idempotencyKey = "idem",
            )

            val command = request.toCommand(1L)

            assertThat(command.userId).isEqualTo(1L)
            assertThat(command.label).isEqualTo("집")
            assertThat(command.recipient).isEqualTo("홍길동")
            assertThat(command.phone).isEqualTo("010-1234-5678")
            assertThat(command.zipCode).isEqualTo("12345")
            assertThat(command.address).isEqualTo("서울시")
            assertThat(command.addressDetail).isEqualTo("101호")
            assertThat(command.isDefault).isTrue()
            assertThat(command.idempotencyKey).isEqualTo("idem")
        }

        @Test
        fun `기본값으로 CreateAddressRequest를 생성한다`() {
            val request = CreateAddressRequest(
                label = "회사",
                recipient = "김철수",
                phone = "010-9999-8888",
                zipCode = "54321",
                address = "부산시",
                addressDetail = "202호",
            )

            assertThat(request.label).isEqualTo("회사")
            assertThat(request.recipient).isEqualTo("김철수")
            assertThat(request.phone).isEqualTo("010-9999-8888")
            assertThat(request.zipCode).isEqualTo("54321")
            assertThat(request.address).isEqualTo("부산시")
            assertThat(request.addressDetail).isEqualTo("202호")
            assertThat(request.isDefault).isFalse()
            assertThat(request.idempotencyKey).isNull()
        }
    }

    @Nested
    @DisplayName("LoginResponse")
    inner class LoginResponseTest {

        @Test
        fun `LoginResponse를 생성한다`() {
            val response = LoginResponse("accessToken")

            assertThat(response.accessToken).isEqualTo("accessToken")
        }
    }
}
