package com.koosco.userservice.api.controller;

@io.swagger.v3.oas.annotations.tags.Tag(name = "User", description = "User management operations")
@org.springframework.web.bind.annotation.RestController()
@org.springframework.web.bind.annotation.RequestMapping(value = {"/api/users"})
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\u0018\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\f2\b\b\u0001\u0010\r\u001a\u00020\u000eH\u0017J\u0018\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00010\f2\b\b\u0001\u0010\r\u001a\u00020\u000eH\u0017J\u0018\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00010\f2\b\b\u0001\u0010\u0011\u001a\u00020\u0012H\u0017J\"\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00010\f2\b\b\u0001\u0010\r\u001a\u00020\u000e2\b\b\u0001\u0010\u0011\u001a\u00020\u0014H\u0017R\u000e\u0010\u0006\u001a\u00020\u0007X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lcom/koosco/userservice/api/controller/UserController;", "", "registerUseCase", "Lcom/koosco/userservice/application/usecase/RegisterUseCase;", "getUserDetailUseCase", "Lcom/koosco/userservice/application/usecase/GetUserDetailUseCase;", "deleteMeUseCase", "Lcom/koosco/userservice/application/usecase/DeleteMeUseCase;", "updateMeUseCase", "Lcom/koosco/userservice/application/usecase/UpdateMeUseCase;", "(Lcom/koosco/userservice/application/usecase/RegisterUseCase;Lcom/koosco/userservice/application/usecase/GetUserDetailUseCase;Lcom/koosco/userservice/application/usecase/DeleteMeUseCase;Lcom/koosco/userservice/application/usecase/UpdateMeUseCase;)V", "deleteMe", "Lcom/koosco/common/core/response/ApiResponse;", "userId", "", "getUser", "registerUser", "request", "Lcom/koosco/userservice/api/RegisterRequest;", "updateMe", "Lcom/koosco/userservice/api/UpdateRequest;", "user-service"})
public class UserController {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.userservice.application.usecase.RegisterUseCase registerUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.userservice.application.usecase.GetUserDetailUseCase getUserDetailUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.userservice.application.usecase.DeleteMeUseCase deleteMeUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.userservice.application.usecase.UpdateMeUseCase updateMeUseCase = null;
    
    public UserController(@org.jetbrains.annotations.NotNull()
    com.koosco.userservice.application.usecase.RegisterUseCase registerUseCase, @org.jetbrains.annotations.NotNull()
    com.koosco.userservice.application.usecase.GetUserDetailUseCase getUserDetailUseCase, @org.jetbrains.annotations.NotNull()
    com.koosco.userservice.application.usecase.DeleteMeUseCase deleteMeUseCase, @org.jetbrains.annotations.NotNull()
    com.koosco.userservice.application.usecase.UpdateMeUseCase updateMeUseCase) {
        super();
    }
    
    @io.swagger.v3.oas.annotations.Operation(summary = "\ub85c\uceec \ud68c\uc6d0\uac00\uc785", description = "\ub85c\uceec \ud68c\uc6d0\uac00\uc785\uc744 \uc9c4\ud589\ud569\ub2c8\ub2e4. \uc774\uba54\uc77c, \ube44\ubc00\ubc88\ud638, \uc774\ub984, \uc804\ud654\ubc88\ud638\ub97c \uc785\ub825\ubc1b\uc544 \uc2e0\uaddc \uc0ac\uc6a9\uc790\ub97c \ub4f1\ub85d\ud569\ub2c8\ub2e4.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {@io.swagger.v3.oas.annotations.responses.ApiResponse(description = "\ud68c\uc6d0\uac00\uc785 \uc131\uacf5", responseCode = "200"), @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "\uc798\ubabb\ub41c \uc694\uccad (\uc720\ud6a8\uc131 \uac80\uc0ac \uc2e4\ud328)", responseCode = "400"), @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "\uc774\ubbf8 \uc874\uc7ac\ud558\ub294 \uc774\uba54\uc77c", responseCode = "409")})
    @org.springframework.web.bind.annotation.PostMapping()
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<java.lang.Object> registerUser(@jakarta.validation.Valid()
    @org.springframework.web.bind.annotation.RequestBody()
    @io.swagger.v3.oas.annotations.Parameter(description = "\ud68c\uc6d0\uac00\uc785 \uc694\uccad \uc815\ubcf4", required = true)
    @org.jetbrains.annotations.NotNull()
    com.koosco.userservice.api.RegisterRequest request) {
        return null;
    }
    
    @io.swagger.v3.oas.annotations.Operation(summary = "\uc0ac\uc6a9\uc790 \uc870\ud68c", description = "\uc0ac\uc6a9\uc790 \uc815\ubcf4\ub97c \uc870\ud68c\ud569\ub2c8\ub2e4.")
    @org.springframework.web.bind.annotation.GetMapping(value = {"/{userId}"})
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<java.lang.Object> getUser(@org.springframework.web.bind.annotation.PathVariable()
    long userId) {
        return null;
    }
    
    @io.swagger.v3.oas.annotations.Operation(summary = "\ubcf8\uc778 \uc0ad\uc81c", description = "\ubcf8\uc778 \uacc4\uc815\uc744 \uc0ad\uc81c\ud569\ub2c8\ub2e4.")
    @org.springframework.web.bind.annotation.DeleteMapping(value = {"/me"})
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<java.lang.Object> deleteMe(@com.koosco.commonsecurity.resolver.AuthId()
    long userId) {
        return null;
    }
    
    @io.swagger.v3.oas.annotations.Operation(summary = "\uc0ac\uc6a9\uc790 \uc218\uc815", description = "\uc0ac\uc6a9\uc790 \uc815\ubcf4\ub97c \uc218\uc815\ud569\ub2c8\ub2e4.")
    @org.springframework.web.bind.annotation.PatchMapping(value = {"/me"})
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<java.lang.Object> updateMe(@com.koosco.commonsecurity.resolver.AuthId()
    long userId, @org.springframework.web.bind.annotation.RequestBody()
    @org.jetbrains.annotations.NotNull()
    com.koosco.userservice.api.UpdateRequest request) {
        return null;
    }
}