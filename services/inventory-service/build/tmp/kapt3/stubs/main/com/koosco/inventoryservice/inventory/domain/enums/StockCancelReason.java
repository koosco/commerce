package com.koosco.inventoryservice.inventory.domain.enums;

/**
 * fileName       : CancelStockReason
 * author         : koo
 * date           : 2025. 12. 24. 오전 4:27
 * description    :
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0086\u0081\u0002\u0018\u0000 \u00062\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\u0006B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005\u00a8\u0006\u0007"}, d2 = {"Lcom/koosco/inventoryservice/inventory/domain/enums/StockCancelReason;", "", "(Ljava/lang/String;I)V", "PAYMENT_FAILED", "USER_CANCELLED", "PAYMENT_TIMEOUT", "Companion", "inventory-service"})
public enum StockCancelReason {
    /*public static final*/ PAYMENT_FAILED /* = new PAYMENT_FAILED() */,
    /*public static final*/ USER_CANCELLED /* = new USER_CANCELLED() */,
    /*public static final*/ PAYMENT_TIMEOUT /* = new PAYMENT_TIMEOUT() */;
    @org.jetbrains.annotations.NotNull()
    public static final com.koosco.inventoryservice.inventory.domain.enums.StockCancelReason.Companion Companion = null;
    
    StockCancelReason() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.koosco.inventoryservice.inventory.domain.enums.StockCancelReason> getEntries() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/koosco/inventoryservice/inventory/domain/enums/StockCancelReason$Companion;", "", "()V", "mapCancelReason", "Lcom/koosco/inventoryservice/inventory/domain/enums/StockCancelReason;", "reason", "", "inventory-service"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.koosco.inventoryservice.inventory.domain.enums.StockCancelReason mapCancelReason(@org.jetbrains.annotations.Nullable()
        java.lang.String reason) {
            return null;
        }
    }
}