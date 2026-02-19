package com.koosco.userservice.api.controller

import com.koosco.common.core.response.ApiResponse
import com.koosco.commonsecurity.resolver.AuthId
import com.koosco.userservice.api.CreateAddressRequest
import com.koosco.userservice.application.command.GetAddressesCommand
import com.koosco.userservice.application.usecase.CreateAddressUseCase
import com.koosco.userservice.application.usecase.GetAddressesUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Address", description = "배송지 관리")
@RestController
@RequestMapping("/api/users/me/addresses")
class AddressController(
    private val getAddressesUseCase: GetAddressesUseCase,
    private val createAddressUseCase: CreateAddressUseCase,
) {

    @Operation(summary = "배송지 목록 조회", description = "본인의 배송지 목록을 조회합니다.")
    @GetMapping
    fun getAddresses(@AuthId userId: Long): ApiResponse<Any> {
        val result = getAddressesUseCase.execute(GetAddressesCommand(userId))
        return ApiResponse.success(result)
    }

    @Operation(summary = "배송지 등록", description = "새 배송지를 등록합니다.")
    @PostMapping
    fun createAddress(@AuthId userId: Long, @Valid @RequestBody request: CreateAddressRequest): ApiResponse<Any> {
        val result = createAddressUseCase.execute(request.toCommand(userId))
        return ApiResponse.success(result)
    }
}
