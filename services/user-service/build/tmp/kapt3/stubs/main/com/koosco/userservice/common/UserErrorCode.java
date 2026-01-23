package com.koosco.userservice.common;

/**
 * User service specific error codes.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000f\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u00012\u00020\u0002B\u001f\b\u0002\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bR\u0014\u0010\u0003\u001a\u00020\u0004X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0014\u0010\u0005\u001a\u00020\u0004X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0014\u0010\u0006\u001a\u00020\u0007X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011j\u0002\b\u0012j\u0002\b\u0013j\u0002\b\u0014j\u0002\b\u0015\u00a8\u0006\u0016"}, d2 = {"Lcom/koosco/userservice/common/UserErrorCode;", "", "Lcom/koosco/common/core/error/ErrorCode;", "code", "", "message", "status", "Lorg/springframework/http/HttpStatus;", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Lorg/springframework/http/HttpStatus;)V", "getCode", "()Ljava/lang/String;", "getMessage", "getStatus", "()Lorg/springframework/http/HttpStatus;", "INVALID_PASSWORD_FORMAT", "INVALID_EMAIL_FORMAT", "PASSWORD_MISMATCH", "INVALID_CREDENTIALS", "USER_NOT_FOUND", "EMAIL_ALREADY_EXISTS", "USERNAME_ALREADY_EXISTS", "USER_ALREADY_DELETED", "user-service"})
public enum UserErrorCode implements com.koosco.common.core.error.ErrorCode {
    /*public static final*/ INVALID_PASSWORD_FORMAT /* = new INVALID_PASSWORD_FORMAT(null, null, null) */,
    /*public static final*/ INVALID_EMAIL_FORMAT /* = new INVALID_EMAIL_FORMAT(null, null, null) */,
    /*public static final*/ PASSWORD_MISMATCH /* = new PASSWORD_MISMATCH(null, null, null) */,
    /*public static final*/ INVALID_CREDENTIALS /* = new INVALID_CREDENTIALS(null, null, null) */,
    /*public static final*/ USER_NOT_FOUND /* = new USER_NOT_FOUND(null, null, null) */,
    /*public static final*/ EMAIL_ALREADY_EXISTS /* = new EMAIL_ALREADY_EXISTS(null, null, null) */,
    /*public static final*/ USERNAME_ALREADY_EXISTS /* = new USERNAME_ALREADY_EXISTS(null, null, null) */,
    /*public static final*/ USER_ALREADY_DELETED /* = new USER_ALREADY_DELETED(null, null, null) */;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String code = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String message = null;
    @org.jetbrains.annotations.NotNull()
    private final org.springframework.http.HttpStatus status = null;
    
    UserErrorCode(java.lang.String code, java.lang.String message, org.springframework.http.HttpStatus status) {
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String getCode() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String getMessage() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public org.springframework.http.HttpStatus getStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.koosco.userservice.common.UserErrorCode> getEntries() {
        return null;
    }
}