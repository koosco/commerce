package com.koosco.inventoryservice.inventory.domain.enums;

/**
 * fileName       : StockReservationFailReason
 * author         : koo
 * date           : 2025. 12. 24. 오전 3:56
 * description    :
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/koosco/inventoryservice/inventory/domain/enums/StockReservationFailReason;", "", "(Ljava/lang/String;I)V", "NOT_ENOUGH_STOCK", "SKU_NOT_FOUND", "RESERVATION_CONFLICT", "INVENTORY_LOCKED", "inventory-service"})
public enum StockReservationFailReason {
    /*public static final*/ NOT_ENOUGH_STOCK /* = new NOT_ENOUGH_STOCK() */,
    /*public static final*/ SKU_NOT_FOUND /* = new SKU_NOT_FOUND() */,
    /*public static final*/ RESERVATION_CONFLICT /* = new RESERVATION_CONFLICT() */,
    /*public static final*/ INVENTORY_LOCKED /* = new INVENTORY_LOCKED() */;
    
    StockReservationFailReason() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.koosco.inventoryservice.inventory.domain.enums.StockReservationFailReason> getEntries() {
        return null;
    }
}