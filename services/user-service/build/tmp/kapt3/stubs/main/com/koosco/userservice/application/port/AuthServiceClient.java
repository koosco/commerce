package com.koosco.userservice.application.port;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J2\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00072\b\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u000b\u001a\u00020\fH&\u00a8\u0006\r"}, d2 = {"Lcom/koosco/userservice/application/port/AuthServiceClient;", "", "notifyUserCreated", "", "userId", "", "password", "", "email", "provider", "Lcom/koosco/userservice/domain/enums/AuthProvider;", "role", "Lcom/koosco/userservice/domain/enums/UserRole;", "user-service"})
public abstract interface AuthServiceClient {
    
    public abstract void notifyUserCreated(long userId, @org.jetbrains.annotations.NotNull()
    java.lang.String password, @org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.Nullable()
    com.koosco.userservice.domain.enums.AuthProvider provider, @org.jetbrains.annotations.NotNull()
    com.koosco.userservice.domain.enums.UserRole role);
}