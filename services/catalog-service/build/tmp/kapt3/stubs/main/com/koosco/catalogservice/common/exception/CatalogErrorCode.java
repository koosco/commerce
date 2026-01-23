package com.koosco.catalogservice.common.exception;

/**
 * Catalog service specific error codes.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0018\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u00012\u00020\u0002B\u001f\b\u0002\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bR\u0014\u0010\u0003\u001a\u00020\u0004X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0014\u0010\u0005\u001a\u00020\u0004X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0014\u0010\u0006\u001a\u00020\u0007X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011j\u0002\b\u0012j\u0002\b\u0013j\u0002\b\u0014j\u0002\b\u0015j\u0002\b\u0016j\u0002\b\u0017j\u0002\b\u0018j\u0002\b\u0019j\u0002\b\u001aj\u0002\b\u001bj\u0002\b\u001cj\u0002\b\u001dj\u0002\b\u001e\u00a8\u0006\u001f"}, d2 = {"Lcom/koosco/catalogservice/common/exception/CatalogErrorCode;", "", "Lcom/koosco/common/core/error/ErrorCode;", "code", "", "message", "status", "Lorg/springframework/http/HttpStatus;", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Lorg/springframework/http/HttpStatus;)V", "getCode", "()Ljava/lang/String;", "getMessage", "getStatus", "()Lorg/springframework/http/HttpStatus;", "INVALID_CATEGORY_ID", "INVALID_PRODUCT_ID", "INVALID_SORT_OPTION", "INVALID_PRICE_RANGE", "INVALID_SEARCH_QUERY", "UNAUTHORIZED", "FORBIDDEN", "PRODUCT_NOT_FOUND", "CATEGORY_NOT_FOUND", "PRODUCT_IMAGE_NOT_FOUND", "OPTION_NOT_FOUND", "PRODUCT_NAME_CONFLICT", "CATEGORY_NAME_CONFLICT", "PRODUCT_CREATION_FAILED", "CATEGORY_CREATION_FAILED", "IMAGE_UPLOAD_FAILED", "EXTERNAL_SERVICE_COMMUNICATION_FAILED", "catalog-service"})
public enum CatalogErrorCode implements com.koosco.common.core.error.ErrorCode {
    /*public static final*/ INVALID_CATEGORY_ID /* = new INVALID_CATEGORY_ID(null, null, null) */,
    /*public static final*/ INVALID_PRODUCT_ID /* = new INVALID_PRODUCT_ID(null, null, null) */,
    /*public static final*/ INVALID_SORT_OPTION /* = new INVALID_SORT_OPTION(null, null, null) */,
    /*public static final*/ INVALID_PRICE_RANGE /* = new INVALID_PRICE_RANGE(null, null, null) */,
    /*public static final*/ INVALID_SEARCH_QUERY /* = new INVALID_SEARCH_QUERY(null, null, null) */,
    /*public static final*/ UNAUTHORIZED /* = new UNAUTHORIZED(null, null, null) */,
    /*public static final*/ FORBIDDEN /* = new FORBIDDEN(null, null, null) */,
    /*public static final*/ PRODUCT_NOT_FOUND /* = new PRODUCT_NOT_FOUND(null, null, null) */,
    /*public static final*/ CATEGORY_NOT_FOUND /* = new CATEGORY_NOT_FOUND(null, null, null) */,
    /*public static final*/ PRODUCT_IMAGE_NOT_FOUND /* = new PRODUCT_IMAGE_NOT_FOUND(null, null, null) */,
    /*public static final*/ OPTION_NOT_FOUND /* = new OPTION_NOT_FOUND(null, null, null) */,
    /*public static final*/ PRODUCT_NAME_CONFLICT /* = new PRODUCT_NAME_CONFLICT(null, null, null) */,
    /*public static final*/ CATEGORY_NAME_CONFLICT /* = new CATEGORY_NAME_CONFLICT(null, null, null) */,
    /*public static final*/ PRODUCT_CREATION_FAILED /* = new PRODUCT_CREATION_FAILED(null, null, null) */,
    /*public static final*/ CATEGORY_CREATION_FAILED /* = new CATEGORY_CREATION_FAILED(null, null, null) */,
    /*public static final*/ IMAGE_UPLOAD_FAILED /* = new IMAGE_UPLOAD_FAILED(null, null, null) */,
    /*public static final*/ EXTERNAL_SERVICE_COMMUNICATION_FAILED /* = new EXTERNAL_SERVICE_COMMUNICATION_FAILED(null, null, null) */;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String code = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String message = null;
    @org.jetbrains.annotations.NotNull()
    private final org.springframework.http.HttpStatus status = null;
    
    CatalogErrorCode(java.lang.String code, java.lang.String message, org.springframework.http.HttpStatus status) {
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
    public static kotlin.enums.EnumEntries<com.koosco.catalogservice.common.exception.CatalogErrorCode> getEntries() {
        return null;
    }
}