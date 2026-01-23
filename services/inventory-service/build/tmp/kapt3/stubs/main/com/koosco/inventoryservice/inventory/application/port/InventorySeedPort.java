package com.koosco.inventoryservice.inventory.application.port;

/**
 * fileName       : InventorySeedPort
 * author         : koo
 * date           : 2025. 12. 30. 오전 12:06
 * description    :
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\u0018\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH&J\u001e\u0010\t\u001a\u00020\u00032\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00060\u000b2\u0006\u0010\u0007\u001a\u00020\bH&\u00a8\u0006\f"}, d2 = {"Lcom/koosco/inventoryservice/inventory/application/port/InventorySeedPort;", "", "clear", "", "initStock", "skuId", "", "initialQuantity", "", "initStocks", "skuIds", "", "inventory-service"})
public abstract interface InventorySeedPort {
    
    public abstract void initStock(@org.jetbrains.annotations.NotNull()
    java.lang.String skuId, int initialQuantity);
    
    public abstract void initStocks(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> skuIds, int initialQuantity);
    
    public abstract void clear();
}