package com.koosco.userservice.api.controller

import com.koosco.common.core.response.ApiResponse
import com.koosco.userservice.api.UpdateRequest
import com.koosco.userservice.application.command.ForceDeleteCommand
import com.koosco.userservice.application.command.ForceUpdateCommand
import com.koosco.userservice.application.usecase.ForceDeleteUseCase
import com.koosco.userservice.application.usecase.ForceUpdateUseCase
import io.swagger.v3.oas.annotations.Operation
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class AdminController(
    private val forceDeleteUseCase: ForceDeleteUseCase,
    private val forceUpdateUseCase: ForceUpdateUseCase,
) {

    @Operation(
        summary = "사용자 삭제",
        description = "사용자를 삭제하고 BLOCK 처리합니다.",
    )
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteUser(@PathVariable userId: Long): ApiResponse<Any> {
        forceDeleteUseCase.execute(ForceDeleteCommand(userId))

        return ApiResponse.success()
    }

    @Operation(
        summary = "사용자 정보 수정",
        description = "사용자 정보를 수정합니다.",
    )
    @PatchMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateUser(@PathVariable userId: Long, @RequestBody request: UpdateRequest): ApiResponse<Any> {
        forceUpdateUseCase.execute(ForceUpdateCommand.of(userId, request.name, request.phone))

        return ApiResponse.success()
    }
}
