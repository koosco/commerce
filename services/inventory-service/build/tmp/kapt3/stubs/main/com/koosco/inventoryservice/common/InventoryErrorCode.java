package com.koosco.inventoryservice.common;

/**
 * Inventory service specific error codes.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000f\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u00012\u00020\u0002B\u001f\b\u0002\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bR\u0014\u0010\u0003\u001a\u00020\u0004X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0014\u0010\u0005\u001a\u00020\u0004X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0014\u0010\u0006\u001a\u00020\u0007X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011j\u0002\b\u0012j\u0002\b\u0013j\u0002\b\u0014j\u0002\b\u0015\u00a8\u0006\u0016"}, d2 = {"Lcom/koosco/inventoryservice/common/InventoryErrorCode;", "", "Lcom/koosco/common/core/error/ErrorCode;", "code", "", "message", "status", "Lorg/springframework/http/HttpStatus;", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Lorg/springframework/http/HttpStatus;)V", "getCode", "()Ljava/lang/String;", "getMessage", "getStatus", "()Lorg/springframework/http/HttpStatus;", "INVALID_QUANTITY", "INVALID_PRODUCT_ID", "STOCK_ADJUST_NOT_ALLOWED", "PRODUCT_NOT_FOUND", "INVENTORY_NOT_FOUND", "NOT_ENOUGH_STOCK", "OUT_OF_STOCK", "INVENTORY_ALREADY_EXISTS", "inventory-service"})
public enum InventoryErrorCode implements com.koosco.common.core.error.ErrorCode {
    /*public static final*/ INVALID_QUANTITY /* = new INVALID_QUANTITY(null, null, null) */,
    /*public static final*/ INVALID_PRODUCT_ID /* = new INVALID_PRODUCT_ID(null, null, null) */,
    /*public static final*/ STOCK_ADJUST_NOT_ALLOWED /* = new STOCK_ADJUST_NOT_ALLOWED(null, null, null) */,
    /*public static final*/ PRODUCT_NOT_FOUND /* = new PRODUCT_NOT_FOUND(null, null, null) */,
    /*public static final*/ INVENTORY_NOT_FOUND /* = new INVENTORY_NOT_FOUND(null, null, null) */,
    /*public static final*/ NOT_ENOUGH_STOCK /* = new NOT_ENOUGH_STOCK(null, null, null) */,
    /*public static final*/ OUT_OF_STOCK /* = new OUT_OF_STOCK(null, null, null) */,
    /*public static final*/ INVENTORY_ALREADY_EXISTS /* = new INVENTORY_ALREADY_EXISTS(null, null, null) */;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String code = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String message = null;
    @org.jetbrains.annotations.NotNull()
    private final org.springframework.http.HttpStatus status = null;
    
    InventoryErrorCode(java.lang.String code, java.lang.String message, org.springframework.http.HttpStatus status) {
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
    public static kotlin.enums.EnumEntries<com.koosco.inventoryservice.common.InventoryErrorCode> getEntries() {
        return null;
    }
}