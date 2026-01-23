package com.koosco.userservice.api;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B3\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u0014\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\bH\u00c6\u0003J=\u0010\u0016\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0007\u001a\u00020\bH\u00c6\u0001J\u0013\u0010\u0017\u001a\u00020\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001a\u001a\u00020\u001bH\u00d6\u0001J\u0006\u0010\u001c\u001a\u00020\u001dJ\t\u0010\u001e\u001a\u00020\u0003H\u00d6\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0016\u0010\u0005\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000bR\u0018\u0010\u0006\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000bR\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\u001f"}, d2 = {"Lcom/koosco/userservice/api/RegisterRequest;", "", "email", "", "password", "name", "phone", "provider", "Lcom/koosco/userservice/domain/enums/AuthProvider;", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/koosco/userservice/domain/enums/AuthProvider;)V", "getEmail", "()Ljava/lang/String;", "getName", "getPassword", "getPhone", "getProvider", "()Lcom/koosco/userservice/domain/enums/AuthProvider;", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "", "toCommand", "Lcom/koosco/userservice/application/command/CreateUserCommand;", "toString", "user-service"})
public final class RegisterRequest {
    @jakarta.validation.constraints.NotBlank(message = "\uc774\uba54\uc77c\uc740 \uacf5\ubc31\uc77c \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String email = null;
    @jakarta.validation.constraints.NotBlank(message = "\ube44\ubc00\ubc88\ud638\ub294 \uacf5\ubc31\uc77c \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String password = null;
    @jakarta.validation.constraints.NotBlank(message = "\uc774\ub984\uc740 \uacf5\ubc31\uc77c \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String name = null;
    @com.koosco.common.core.annotation.NotBlankIfPresent(message = "\uc804\ud654\ubc88\ud638\ub294 \uacf5\ubc31\uc77c \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String phone = null;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.userservice.domain.enums.AuthProvider provider = null;
    
    public RegisterRequest(@org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    java.lang.String password, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.Nullable()
    java.lang.String phone, @org.jetbrains.annotations.NotNull()
    com.koosco.userservice.domain.enums.AuthProvider provider) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getEmail() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getPassword() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getName() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPhone() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.koosco.userservice.domain.enums.AuthProvider getProvider() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.koosco.userservice.application.command.CreateUserCommand toCommand() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.koosco.userservice.domain.enums.AuthProvider component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.koosco.userservice.api.RegisterRequest copy(@org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    java.lang.String password, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.Nullable()
    java.lang.String phone, @org.jetbrains.annotations.NotNull()
    com.koosco.userservice.domain.enums.AuthProvider provider) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}