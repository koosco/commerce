package com.koosco.userservice.api.controller

import com.koosco.common.core.response.ApiResponse
import com.koosco.commonsecurity.resolver.AuthId
import com.koosco.userservice.api.RegisterRequest
import com.koosco.userservice.api.UpdateRequest
import com.koosco.userservice.application.command.GetUserDetailCommand
import com.koosco.userservice.application.usecase.DeleteMeUseCase
import com.koosco.userservice.application.usecase.GetUserDetailUseCase
import com.koosco.userservice.application.usecase.RegisterUseCase
import com.koosco.userservice.application.usecase.UpdateMeUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse

@Tag(name = "User", description = "User management operations")
@RestController
@RequestMapping("/api/users")
class UserController(
    private val registerUseCase: RegisterUseCase,
    private val getUserDetailUseCase: GetUserDetailUseCase,
    private val deleteMeUseCase: DeleteMeUseCase,
    private val updateMeUseCase: UpdateMeUseCase,
) {

    @Operation(
        summary = "로컬 회원가입",
        description = "로컬 회원가입을 진행합니다. 이메일, 비밀번호, 이름, 전화번호를 입력받아 신규 사용자를 등록합니다.",
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(responseCode = "200", description = "회원가입 성공"),
            SwaggerApiResponse(responseCode = "400", description = "잘못된 요청 (유효성 검사 실패)"),
            SwaggerApiResponse(responseCode = "409", description = "이미 존재하는 이메일"),
        ],
    )
    @PostMapping
    fun registerUser(
        @Valid @RequestBody
        @Parameter(description = "회원가입 요청 정보", required = true)
        request: RegisterRequest,
    ): ApiResponse<Any> {
        registerUseCase.execute(request.toCommand())

        return ApiResponse.success()
    }

    @Operation(
        summary = "사용자 조회",
        description = "사용자 정보를 조회합니다.",
    )
    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: Long): ApiResponse<Any> {
        val response = getUserDetailUseCase.execute(GetUserDetailCommand(userId))

        return ApiResponse.success(response)
    }

    @Operation(
        summary = "본인 삭제",
        description = "본인 계정을 삭제합니다.",
    )
    @DeleteMapping("/me")
    fun deleteMe(@AuthId userId: Long): ApiResponse<Any> {
        deleteMeUseCase.execute(userId)

        return ApiResponse.success()
    }

    @Operation(
        summary = "사용자 수정",
        description = "사용자 정보를 수정합니다.",
    )
    @PatchMapping("/me")
    fun updateMe(@AuthId userId: Long, @RequestBody request: UpdateRequest): ApiResponse<Any> {
        updateMeUseCase.execute(request.toCommand(userId))

        return ApiResponse.success()
    }
}
