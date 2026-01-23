package com.koosco.userservice.domain.entity;

@jakarta.persistence.Entity()
@jakarta.persistence.Table(name = "users")
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u001d\n\u0002\u0010\u0002\n\u0002\b\u0006\b\u0017\u0018\u0000 42\u00020\u0001:\u00014B[\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u000f\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0011\u0012\b\b\u0002\u0010\u0012\u001a\u00020\u0011\u00a2\u0006\u0002\u0010\u0013J\b\u0010.\u001a\u00020/H\u0016J\b\u00100\u001a\u00020/H\u0016J\b\u00101\u001a\u00020/H\u0016J$\u00102\u001a\u00020/2\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\b\u0010\b\u001a\u0004\u0018\u00010\tH\u0016\u00f8\u0001\u0000\u00a2\u0006\u0002\b3R\u0014\u0010\u0010\u001a\u00020\u0011X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u001e\u0010\u0004\u001a\u00020\u00058\u0016X\u0097\u0004\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u0018\u001a\u0004\b\u0016\u0010\u0017R\"\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0016@\u0016X\u0097\u000e\u00a2\u0006\u0010\n\u0002\u0010\u001d\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001cR\u001e\u0010\u0006\u001a\u00020\u00078\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001e\u0010\u0017\"\u0004\b\u001f\u0010 R&\u0010\b\u001a\u00020\t8\u0016@\u0016X\u0097\u000e\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0010\n\u0002\u0010\u0018\u001a\u0004\b!\u0010\u0017\"\u0004\b\"\u0010 R\u0016\u0010\u000e\u001a\u00020\u000f8\u0016X\u0097\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010$R\u0016\u0010\f\u001a\u00020\r8\u0016X\u0097\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010&R\u001e\u0010\n\u001a\u00020\u000b8\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\'\u0010(\"\u0004\b)\u0010*R\u001a\u0010\u0012\u001a\u00020\u0011X\u0096\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b+\u0010\u0015\"\u0004\b,\u0010-\u0082\u0002\u000b\n\u0005\b\u00a1\u001e0\u0001\n\u0002\b!\u00a8\u00065"}, d2 = {"Lcom/koosco/userservice/domain/entity/User;", "", "id", "", "email", "Lcom/koosco/userservice/domain/vo/Email;", "name", "", "phone", "Lcom/koosco/userservice/domain/vo/Phone;", "status", "Lcom/koosco/userservice/domain/enums/UserStatus;", "role", "Lcom/koosco/userservice/domain/enums/UserRole;", "provider", "Lcom/koosco/userservice/domain/enums/AuthProvider;", "createdAt", "Ljava/time/LocalDateTime;", "updatedAt", "(Ljava/lang/Long;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/koosco/userservice/domain/enums/UserStatus;Lcom/koosco/userservice/domain/enums/UserRole;Lcom/koosco/userservice/domain/enums/AuthProvider;Ljava/time/LocalDateTime;Ljava/time/LocalDateTime;Lkotlin/jvm/internal/DefaultConstructorMarker;)V", "getCreatedAt", "()Ljava/time/LocalDateTime;", "getEmail-PhAlQ_A", "()Ljava/lang/String;", "Ljava/lang/String;", "getId", "()Ljava/lang/Long;", "setId", "(Ljava/lang/Long;)V", "Ljava/lang/Long;", "getName", "setName", "(Ljava/lang/String;)V", "getPhone-eOJzX64", "setPhone-CrESmYk", "getProvider", "()Lcom/koosco/userservice/domain/enums/AuthProvider;", "getRole", "()Lcom/koosco/userservice/domain/enums/UserRole;", "getStatus", "()Lcom/koosco/userservice/domain/enums/UserStatus;", "setStatus", "(Lcom/koosco/userservice/domain/enums/UserStatus;)V", "getUpdatedAt", "setUpdatedAt", "(Ljava/time/LocalDateTime;)V", "activate", "", "forceDelete", "quit", "update", "update-I8Ojym0", "Companion", "user-service"})
public class User {
    @jakarta.persistence.Id()
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @org.jetbrains.annotations.Nullable()
    private java.lang.Long id;
    @jakarta.persistence.Column(nullable = false)
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String email = null;
    @jakarta.persistence.Column(nullable = false, unique = true)
    @org.jetbrains.annotations.NotNull()
    private java.lang.String name;
    @jakarta.persistence.Column(nullable = true)
    @org.jetbrains.annotations.NotNull()
    private java.lang.String phone;
    @jakarta.persistence.Column(nullable = false)
    @jakarta.persistence.Enumerated(value = jakarta.persistence.EnumType.STRING)
    @org.jetbrains.annotations.NotNull()
    private com.koosco.userservice.domain.enums.UserStatus status;
    @jakarta.persistence.Column(nullable = false)
    @jakarta.persistence.Enumerated(value = jakarta.persistence.EnumType.STRING)
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.userservice.domain.enums.UserRole role = null;
    @jakarta.persistence.Column(nullable = false)
    @jakarta.persistence.Enumerated(value = jakarta.persistence.EnumType.STRING)
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.userservice.domain.enums.AuthProvider provider = null;
    @org.jetbrains.annotations.NotNull()
    private final java.time.LocalDateTime createdAt = null;
    @org.jetbrains.annotations.NotNull()
    private java.time.LocalDateTime updatedAt;
    @org.jetbrains.annotations.NotNull()
    public static final com.koosco.userservice.domain.entity.User.Companion Companion = null;
    
    private User(java.lang.Long id, java.lang.String email, java.lang.String name, java.lang.String phone, com.koosco.userservice.domain.enums.UserStatus status, com.koosco.userservice.domain.enums.UserRole role, com.koosco.userservice.domain.enums.AuthProvider provider, java.time.LocalDateTime createdAt, java.time.LocalDateTime updatedAt) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public java.lang.Long getId() {
        return null;
    }
    
    public void setId(@org.jetbrains.annotations.Nullable()
    java.lang.Long p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.lang.String getName() {
        return null;
    }
    
    public void setName(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public com.koosco.userservice.domain.enums.UserStatus getStatus() {
        return null;
    }
    
    public void setStatus(@org.jetbrains.annotations.NotNull()
    com.koosco.userservice.domain.enums.UserStatus p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public com.koosco.userservice.domain.enums.UserRole getRole() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public com.koosco.userservice.domain.enums.AuthProvider getProvider() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.time.LocalDateTime getCreatedAt() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.time.LocalDateTime getUpdatedAt() {
        return null;
    }
    
    public void setUpdatedAt(@org.jetbrains.annotations.NotNull()
    java.time.LocalDateTime p0) {
    }
    
    public void quit() {
    }
    
    public void activate() {
    }
    
    public void forceDelete() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J0\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f\u00f8\u0001\u0000\u00a2\u0006\u0004\b\r\u0010\u000e\u0082\u0002\u0007\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u000f"}, d2 = {"Lcom/koosco/userservice/domain/entity/User$Companion;", "", "()V", "createUser", "Lcom/koosco/userservice/domain/entity/User;", "email", "Lcom/koosco/userservice/domain/vo/Email;", "name", "", "phone", "Lcom/koosco/userservice/domain/vo/Phone;", "provider", "Lcom/koosco/userservice/domain/enums/AuthProvider;", "createUser-8zInIIA", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/koosco/userservice/domain/enums/AuthProvider;)Lcom/koosco/userservice/domain/entity/User;", "user-service"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}