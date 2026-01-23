package com.koosco.userservice.api.controller;

@org.springframework.web.bind.annotation.RestController()
@org.springframework.web.bind.annotation.RequestMapping(value = {"/api/users"})
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0018\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\b2\b\b\u0001\u0010\t\u001a\u00020\nH\u0017J\"\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\b2\b\b\u0001\u0010\t\u001a\u00020\n2\b\b\u0001\u0010\f\u001a\u00020\rH\u0017R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/koosco/userservice/api/controller/AdminController;", "", "forceDeleteUseCase", "Lcom/koosco/userservice/application/usecase/ForceDeleteUseCase;", "forceUpdateUseCase", "Lcom/koosco/userservice/application/usecase/ForceUpdateUseCase;", "(Lcom/koosco/userservice/application/usecase/ForceDeleteUseCase;Lcom/koosco/userservice/application/usecase/ForceUpdateUseCase;)V", "deleteUser", "Lcom/koosco/common/core/response/ApiResponse;", "userId", "", "updateUser", "request", "Lcom/koosco/userservice/api/UpdateRequest;", "user-service"})
public class AdminController {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.userservice.application.usecase.ForceDeleteUseCase forceDeleteUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.userservice.application.usecase.ForceUpdateUseCase forceUpdateUseCase = null;
    
    public AdminController(@org.jetbrains.annotations.NotNull()
    com.koosco.userservice.application.usecase.ForceDeleteUseCase forceDeleteUseCase, @org.jetbrains.annotations.NotNull()
    com.koosco.userservice.application.usecase.ForceUpdateUseCase forceUpdateUseCase) {
        super();
    }
    
    @io.swagger.v3.oas.annotations.Operation(summary = "\uc0ac\uc6a9\uc790 \uc0ad\uc81c", description = "\uc0ac\uc6a9\uc790\ub97c \uc0ad\uc81c\ud558\uace0 BLOCK \ucc98\ub9ac\ud569\ub2c8\ub2e4.")
    @org.springframework.web.bind.annotation.DeleteMapping(value = {"/{userId}"})
    @org.springframework.security.access.prepost.PreAuthorize(value = "hasRole(\'ADMIN\')")
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<java.lang.Object> deleteUser(@org.springframework.web.bind.annotation.PathVariable()
    long userId) {
        return null;
    }
    
    @io.swagger.v3.oas.annotations.Operation(summary = "\uc0ac\uc6a9\uc790 \uc815\ubcf4 \uc218\uc815", description = "\uc0ac\uc6a9\uc790 \uc815\ubcf4\ub97c \uc218\uc815\ud569\ub2c8\ub2e4.")
    @org.springframework.web.bind.annotation.PatchMapping(value = {"/{userId}"})
    @org.springframework.security.access.prepost.PreAuthorize(value = "hasRole(\'ADMIN\')")
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<java.lang.Object> updateUser(@org.springframework.web.bind.annotation.PathVariable()
    long userId, @org.springframework.web.bind.annotation.RequestBody()
    @org.jetbrains.annotations.NotNull()
    com.koosco.userservice.api.UpdateRequest request) {
        return null;
    }
}