package com.koosco.userservice.api

import com.koosco.userservice.api.controller.UserController
import com.koosco.userservice.application.command.GetUserDetailCommand
import com.koosco.userservice.application.command.UpdateUserCommand
import com.koosco.userservice.application.dto.UserDto
import com.koosco.userservice.application.usecase.DeleteMeUseCase
import com.koosco.userservice.application.usecase.GetUserDetailUseCase
import com.koosco.userservice.application.usecase.RegisterUseCase
import com.koosco.userservice.application.usecase.UpdateMeUseCase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*

@ExtendWith(MockitoExtension::class)
@DisplayName("UserController")
class UserControllerTest {

    private val registerUseCase: RegisterUseCase = mock()
    private val getUserDetailUseCase: GetUserDetailUseCase = mock()
    private val deleteMeUseCase: DeleteMeUseCase = mock()
    private val updateMeUseCase: UpdateMeUseCase = mock()

    private val controller = UserController(
        registerUseCase,
        getUserDetailUseCase,
        deleteMeUseCase,
        updateMeUseCase,
    )

    @Nested
    @DisplayName("POST /api/users - 회원가입")
    inner class RegisterUser {

        @Test
        fun `회원가입 요청 시 성공 응답을 반환한다`() {
            val request = RegisterRequest("test@example.com", "password", "홍길동", "010-1234-5678")

            val result = controller.registerUser(request)

            assertThat(result.success).isTrue()
            verify(registerUseCase).execute(any())
        }

        @Test
        fun `멱등성 키가 포함된 회원가입 요청을 처리한다`() {
            val request = RegisterRequest("test@example.com", "password", "홍길동", null, "idem-key")

            val result = controller.registerUser(request)

            assertThat(result.success).isTrue()
            verify(registerUseCase).execute(argThat { idempotencyKey == "idem-key" })
        }
    }

    @Nested
    @DisplayName("GET /api/users/{userId} - 사용자 조회")
    inner class GetUser {

        @Test
        fun `사용자 조회 성공 시 UserDto를 반환한다`() {
            val userDto = UserDto(1L, "test@example.com", "홍길동", "010-1234-5678")
            whenever(getUserDetailUseCase.execute(GetUserDetailCommand(1L))).thenReturn(userDto)

            val result = controller.getUser(1L)

            assertThat(result.success).isTrue()
            assertThat(result.data).isEqualTo(userDto)
        }
    }

    @Nested
    @DisplayName("DELETE /api/users/me - 회원 탈퇴")
    inner class DeleteMe {

        @Test
        fun `회원 탈퇴 성공 시 성공 응답을 반환한다`() {
            val result = controller.deleteMe(1L)

            assertThat(result.success).isTrue()
            verify(deleteMeUseCase).execute(1L)
        }
    }

    @Nested
    @DisplayName("PATCH /api/users/me - 정보 수정")
    inner class UpdateMe {

        @Test
        fun `정보 수정 성공 시 성공 응답을 반환한다`() {
            val request = UpdateRequest("김철수", "010-9999-8888")

            val result = controller.updateMe(1L, request)

            assertThat(result.success).isTrue()
            verify(updateMeUseCase).execute(
                argThat<UpdateUserCommand> { userId == 1L && name == "김철수" && phone == "010-9999-8888" },
            )
        }
    }
}
