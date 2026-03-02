package com.koosco.userservice.api

import com.koosco.common.core.exception.BaseException
import com.koosco.userservice.api.controller.AuthController
import com.koosco.userservice.application.dto.AuthTokenDto
import com.koosco.userservice.application.usecase.LoginUseCase
import com.koosco.userservice.application.usecase.LogoutUseCase
import com.koosco.userservice.application.usecase.RefreshTokenUseCase
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*

@ExtendWith(MockitoExtension::class)
@DisplayName("AuthController")
class AuthControllerTest {

    private val loginUseCase: LoginUseCase = mock()
    private val refreshTokenUseCase: RefreshTokenUseCase = mock()
    private val logoutUseCase: LogoutUseCase = mock()

    private val controller = AuthController(loginUseCase, refreshTokenUseCase, logoutUseCase)

    @Nested
    @DisplayName("POST /api/auth/login")
    inner class Login {

        @Test
        fun `로그인 성공 시 토큰을 반환한다`() {
            val request = LoginRequest("test@example.com", "password")
            val httpRequest: HttpServletRequest = mock()
            val httpResponse: HttpServletResponse = mock()
            val tokens = AuthTokenDto("accessToken", "refreshToken", 604800L)

            whenever(httpRequest.getHeader("X-Forwarded-For")).thenReturn(null)
            whenever(httpRequest.getHeader("X-Real-IP")).thenReturn(null)
            whenever(httpRequest.remoteAddr).thenReturn("127.0.0.1")
            whenever(httpRequest.getHeader("User-Agent")).thenReturn("TestAgent")
            whenever(loginUseCase.execute(any())).thenReturn(tokens)

            val result = controller.login(request, httpRequest, httpResponse)

            assertThat(result.success).isTrue()
            assertThat(result.data?.accessToken).isEqualTo("accessToken")
            verify(httpResponse).addHeader(eq("Authorization"), eq("accessToken"))
            verify(httpResponse).addHeader(eq("Set-Cookie"), any())
        }

        @Test
        fun `X-Forwarded-For 헤더가 있으면 첫번째 IP를 사용한다`() {
            val request = LoginRequest("test@example.com", "password")
            val httpRequest: HttpServletRequest = mock()
            val httpResponse: HttpServletResponse = mock()
            val tokens = AuthTokenDto("accessToken", "refreshToken", 604800L)

            whenever(httpRequest.getHeader("X-Forwarded-For")).thenReturn("10.0.0.1, 10.0.0.2")
            whenever(httpRequest.getHeader("User-Agent")).thenReturn(null)
            whenever(loginUseCase.execute(any())).thenReturn(tokens)

            controller.login(request, httpRequest, httpResponse)

            verify(loginUseCase).execute(argThat { ip == "10.0.0.1" })
        }

        @Test
        fun `X-Real-IP 헤더가 있으면 해당 IP를 사용한다`() {
            val request = LoginRequest("test@example.com", "password")
            val httpRequest: HttpServletRequest = mock()
            val httpResponse: HttpServletResponse = mock()
            val tokens = AuthTokenDto("accessToken", "refreshToken", 604800L)

            whenever(httpRequest.getHeader("X-Forwarded-For")).thenReturn(null)
            whenever(httpRequest.getHeader("X-Real-IP")).thenReturn("192.168.1.1")
            whenever(httpRequest.getHeader("User-Agent")).thenReturn(null)
            whenever(loginUseCase.execute(any())).thenReturn(tokens)

            controller.login(request, httpRequest, httpResponse)

            verify(loginUseCase).execute(argThat { ip == "192.168.1.1" })
        }
    }

    @Nested
    @DisplayName("POST /api/auth/refresh")
    inner class Refresh {

        @Test
        fun `리프레시 토큰으로 새 토큰을 발급한다`() {
            val httpRequest: HttpServletRequest = mock()
            val httpResponse: HttpServletResponse = mock()
            val cookies = arrayOf(Cookie("refreshToken", "validRefreshToken"))
            val tokens = AuthTokenDto("newAccess", "newRefresh", 604800L)

            whenever(httpRequest.cookies).thenReturn(cookies)
            whenever(refreshTokenUseCase.execute("validRefreshToken")).thenReturn(tokens)

            val result = controller.refresh(httpRequest, httpResponse)

            assertThat(result.success).isTrue()
            assertThat(result.data?.accessToken).isEqualTo("newAccess")
            verify(httpResponse).addHeader(eq("Authorization"), eq("newAccess"))
        }

        @Test
        fun `쿠키에 리프레시 토큰이 없으면 BaseException이 발생한다`() {
            val httpRequest: HttpServletRequest = mock()
            val httpResponse: HttpServletResponse = mock()

            whenever(httpRequest.cookies).thenReturn(null)

            assertThatThrownBy { controller.refresh(httpRequest, httpResponse) }
                .isInstanceOf(BaseException::class.java)
        }

        @Test
        fun `다른 이름의 쿠키만 있으면 BaseException이 발생한다`() {
            val httpRequest: HttpServletRequest = mock()
            val httpResponse: HttpServletResponse = mock()
            val cookies = arrayOf(Cookie("otherCookie", "value"))

            whenever(httpRequest.cookies).thenReturn(cookies)

            assertThatThrownBy { controller.refresh(httpRequest, httpResponse) }
                .isInstanceOf(BaseException::class.java)
        }
    }

    @Nested
    @DisplayName("POST /api/auth/logout")
    inner class Logout {

        @Test
        fun `로그아웃 성공 시 쿠키를 삭제한다`() {
            val httpResponse: HttpServletResponse = mock()

            val result = controller.logout(1L, httpResponse)

            assertThat(result.success).isTrue()
            verify(logoutUseCase).execute(1L)
            verify(httpResponse).addHeader(eq("Set-Cookie"), any())
        }
    }
}
