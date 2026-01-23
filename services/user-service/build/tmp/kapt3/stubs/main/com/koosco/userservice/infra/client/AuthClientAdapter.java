package com.koosco.userservice.infra.client;

@org.springframework.stereotype.Component()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\b\u0010\n\u001a\u00020\u000bH\u0017J2\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00052\u0006\u0010\u0010\u001a\u00020\u00052\b\u0010\u0011\u001a\u0004\u0018\u00010\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0007\u001a\n \t*\u0004\u0018\u00010\b0\bX\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lcom/koosco/userservice/infra/client/AuthClientAdapter;", "Lcom/koosco/userservice/application/port/AuthServiceClient;", "authClient", "Lcom/koosco/userservice/infra/client/AuthClient;", "authServiceUrl", "", "(Lcom/koosco/userservice/infra/client/AuthClient;Ljava/lang/String;)V", "logger", "Lorg/slf4j/Logger;", "kotlin.jvm.PlatformType", "init", "", "notifyUserCreated", "userId", "", "password", "email", "provider", "Lcom/koosco/userservice/domain/enums/AuthProvider;", "role", "Lcom/koosco/userservice/domain/enums/UserRole;", "user-service"})
public class AuthClientAdapter implements com.koosco.userservice.application.port.AuthServiceClient {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.userservice.infra.client.AuthClient authClient = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String authServiceUrl = null;
    private final org.slf4j.Logger logger = null;
    
    public AuthClientAdapter(@org.jetbrains.annotations.NotNull()
    com.koosco.userservice.infra.client.AuthClient authClient, @org.springframework.beans.factory.annotation.Value(value = "${auth-service.url}")
    @org.jetbrains.annotations.NotNull()
    java.lang.String authServiceUrl) {
        super();
    }
    
    @jakarta.annotation.PostConstruct()
    public void init() {
    }
    
    @java.lang.Override()
    public void notifyUserCreated(long userId, @org.jetbrains.annotations.NotNull()
    java.lang.String password, @org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.Nullable()
    com.koosco.userservice.domain.enums.AuthProvider provider, @org.jetbrains.annotations.NotNull()
    com.koosco.userservice.domain.enums.UserRole role) {
    }
}