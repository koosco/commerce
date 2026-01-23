package com.koosco.userservice.infra.client;

@org.springframework.cloud.openfeign.FeignClient(name = "auth-service", url = "${auth-service.url}")
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bg\u0018\u00002\u00020\u0001J\u0012\u0010\u0002\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\'\u00a8\u0006\u0006"}, d2 = {"Lcom/koosco/userservice/infra/client/AuthClient;", "", "createUser", "", "request", "Lcom/koosco/userservice/infra/client/dto/CreateUserRequest;", "user-service"})
public abstract interface AuthClient {
    
    @org.springframework.web.bind.annotation.PostMapping(value = {"/api/auth"})
    public abstract void createUser(@org.springframework.web.bind.annotation.RequestBody()
    @org.jetbrains.annotations.NotNull()
    com.koosco.userservice.infra.client.dto.CreateUserRequest request);
}