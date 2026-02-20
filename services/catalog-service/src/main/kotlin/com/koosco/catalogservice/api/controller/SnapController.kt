package com.koosco.catalogservice.api.controller

import com.koosco.catalogservice.api.request.CreateSnapRequest
import com.koosco.catalogservice.api.request.UpdateSnapRequest
import com.koosco.catalogservice.api.response.LikeToggleResponse
import com.koosco.catalogservice.api.response.SnapResponse
import com.koosco.catalogservice.application.command.DeleteSnapCommand
import com.koosco.catalogservice.application.usecase.CreateSnapUseCase
import com.koosco.catalogservice.application.usecase.DeleteSnapUseCase
import com.koosco.catalogservice.application.usecase.GetSnapFeedUseCase
import com.koosco.catalogservice.application.usecase.ToggleSnapLikeUseCase
import com.koosco.catalogservice.application.usecase.UpdateSnapUseCase
import com.koosco.common.core.response.ApiResponse
import com.koosco.commonsecurity.resolver.AuthId
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Snaps", description = "Snap management API")
@RestController
@RequestMapping("/api/snaps")
class SnapController(
    private val createSnapUseCase: CreateSnapUseCase,
    private val getSnapFeedUseCase: GetSnapFeedUseCase,
    private val updateSnapUseCase: UpdateSnapUseCase,
    private val deleteSnapUseCase: DeleteSnapUseCase,
    private val toggleSnapLikeUseCase: ToggleSnapLikeUseCase,
) {

    @Operation(summary = "스냅 작성")
    @PostMapping
    fun createSnap(@AuthId userId: Long, @Valid @RequestBody request: CreateSnapRequest): ApiResponse<SnapResponse> {
        val result = createSnapUseCase.execute(request.toCommand(userId))
        return ApiResponse.Companion.success(SnapResponse.from(result))
    }

    @Operation(summary = "스냅 피드 조회")
    @GetMapping
    fun getSnapFeed(
        @PageableDefault(size = 20, sort = ["createdAt"]) pageable: Pageable,
    ): ApiResponse<Page<SnapResponse>> {
        val result = getSnapFeedUseCase.execute(pageable)
        return ApiResponse.Companion.success(result.map { SnapResponse.from(it) })
    }

    @Operation(summary = "스냅 수정")
    @PutMapping("/{snapId}")
    fun updateSnap(
        @AuthId userId: Long,
        @PathVariable snapId: Long,
        @Valid @RequestBody request: UpdateSnapRequest,
    ): ApiResponse<SnapResponse> {
        val result = updateSnapUseCase.execute(request.toCommand(snapId, userId))
        return ApiResponse.Companion.success(SnapResponse.from(result))
    }

    @Operation(summary = "스냅 삭제")
    @DeleteMapping("/{snapId}")
    fun deleteSnap(@AuthId userId: Long, @PathVariable snapId: Long): ApiResponse<Unit> {
        deleteSnapUseCase.execute(DeleteSnapCommand(snapId, userId))
        return ApiResponse.Companion.success(Unit)
    }

    @Operation(summary = "스냅 좋아요 토글")
    @PostMapping("/{snapId}/like")
    fun toggleSnapLike(@AuthId userId: Long, @PathVariable snapId: Long): ApiResponse<LikeToggleResponse> {
        val liked = toggleSnapLikeUseCase.execute(snapId, userId)
        return ApiResponse.Companion.success(LikeToggleResponse(liked))
    }
}
