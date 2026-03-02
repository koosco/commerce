package com.koosco.userservice.api

import com.koosco.userservice.api.controller.AdminController
import com.koosco.userservice.application.command.ForceDeleteCommand
import com.koosco.userservice.application.usecase.ForceDeleteUseCase
import com.koosco.userservice.application.usecase.ForceUpdateUseCase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*

@ExtendWith(MockitoExtension::class)
@DisplayName("AdminController")
class AdminControllerTest {

    private val forceDeleteUseCase: ForceDeleteUseCase = mock()
    private val forceUpdateUseCase: ForceUpdateUseCase = mock()

    private val controller = AdminController(forceDeleteUseCase, forceUpdateUseCase)

    @Nested
    @DisplayName("DELETE /api/users/{userId} - 관리자 사용자 잠금")
    inner class DeleteUser {

        @Test
        fun `사용자를 잠금 처리한다`() {
            val result = controller.deleteUser(1L)

            assertThat(result.success).isTrue()
            verify(forceDeleteUseCase).execute(ForceDeleteCommand(1L))
        }
    }

    @Nested
    @DisplayName("PATCH /api/users/{userId} - 관리자 사용자 수정")
    inner class UpdateUser {

        @Test
        fun `사용자 정보를 강제 수정한다`() {
            val request = UpdateRequest("새이름", "010-1111-2222")

            val result = controller.updateUser(1L, request)

            assertThat(result.success).isTrue()
            verify(forceUpdateUseCase).execute(
                argThat { userId == 1L && name == "새이름" && phone == "010-1111-2222" },
            )
        }

        @Test
        fun `이름만 수정한다`() {
            val request = UpdateRequest("새이름", null)

            val result = controller.updateUser(2L, request)

            assertThat(result.success).isTrue()
            verify(forceUpdateUseCase).execute(
                argThat { userId == 2L && name == "새이름" && phone == null },
            )
        }
    }
}
