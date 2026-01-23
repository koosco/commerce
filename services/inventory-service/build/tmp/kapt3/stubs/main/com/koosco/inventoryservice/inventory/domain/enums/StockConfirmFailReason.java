package com.koosco.inventoryservice.inventory.domain.enums;

/**
 * fileName       : StockConfirmFailReason
 * author         : koo
 * date           : 2025. 12. 29. 오전 5:01
 * description    :
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0004\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004\u00a8\u0006\u0005"}, d2 = {"Lcom/koosco/inventoryservice/inventory/domain/enums/StockConfirmFailReason;", "", "(Ljava/lang/String;I)V", "SKU_NOT_FOUND", "NOT_ENOUGH_RESERVED", "inventory-service"})
public enum StockConfirmFailReason {
    /*public static final*/ SKU_NOT_FOUND /* = new SKU_NOT_FOUND() */,
    /*public static final*/ NOT_ENOUGH_RESERVED /* = new NOT_ENOUGH_RESERVED() */;
    
    StockConfirmFailReason() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.koosco.inventoryservice.inventory.domain.enums.StockConfirmFailReason> getEntries() {
        return null;
    }
}