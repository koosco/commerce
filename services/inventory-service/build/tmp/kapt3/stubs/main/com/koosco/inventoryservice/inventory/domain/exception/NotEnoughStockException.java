package com.koosco.inventoryservice.inventory.domain.exception;

/**
 * fileName       : NotEnoughStockException
 * author         : koo
 * date           : 2025. 12. 19. 오후 1:54
 * description    :
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\t\u0018\u00002\u00020\u0001B3\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\u0002\u0010\bR\u0015\u0010\u0007\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\n\n\u0002\u0010\u000b\u001a\u0004\b\t\u0010\nR\u0015\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\n\n\u0002\u0010\u000b\u001a\u0004\b\f\u0010\nR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e\u00a8\u0006\u000f"}, d2 = {"Lcom/koosco/inventoryservice/inventory/domain/exception/NotEnoughStockException;", "Lcom/koosco/inventoryservice/inventory/domain/exception/BusinessException;", "message", "", "skuId", "requestedQuantity", "", "availableQuantity", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/Integer;)V", "getAvailableQuantity", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getRequestedQuantity", "getSkuId", "()Ljava/lang/String;", "inventory-service"})
public final class NotEnoughStockException extends com.koosco.inventoryservice.inventory.domain.exception.BusinessException {
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String skuId = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer requestedQuantity = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer availableQuantity = null;
    
    public NotEnoughStockException(@org.jetbrains.annotations.NotNull()
    java.lang.String message, @org.jetbrains.annotations.Nullable()
    java.lang.String skuId, @org.jetbrains.annotations.Nullable()
    java.lang.Integer requestedQuantity, @org.jetbrains.annotations.Nullable()
    java.lang.Integer availableQuantity) {
        super(null, null, null, null);
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getSkuId() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getRequestedQuantity() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getAvailableQuantity() {
        return null;
    }
    
    public NotEnoughStockException() {
        super(null, null, null, null);
    }
}