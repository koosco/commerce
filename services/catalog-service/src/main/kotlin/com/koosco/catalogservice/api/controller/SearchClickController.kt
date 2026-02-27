package com.koosco.catalogservice.api.controller

import com.koosco.catalogservice.api.request.SearchClickRequest
import com.koosco.catalogservice.application.usecase.RecordSearchClickUseCase
import com.koosco.common.core.response.ApiResponse
import com.koosco.commonsecurity.resolver.AuthId
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Search", description = "Search quality tracking APIs")
@RestController
@RequestMapping("/api/search")
class SearchClickController(private val recordSearchClickUseCase: RecordSearchClickUseCase) {

    @Operation(
        summary = "검색 결과 클릭 로그를 기록합니다.",
        description = "검색 품질 지표(mAP@k) 계산을 위해 사용자의 검색 클릭 이벤트를 기록합니다.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @PostMapping("/click")
    @ResponseStatus(HttpStatus.OK)
    fun recordSearchClick(
        @Valid @RequestBody request: SearchClickRequest,
        @Parameter(hidden = true) @AuthId userId: Long,
    ): ApiResponse<Any> {
        recordSearchClickUseCase.execute(request.toCommand(userId))
        return ApiResponse.success()
    }
}
