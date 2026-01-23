package com.koosco.inventoryservice.inventory.application.usecase;

/**
 * fileName       : InventorySeedUseCase
 * author         : koo
 * date           : 2025. 12. 26. 오전 4:59
 * description    : 더미 데이터 초기화 usecase
 */
@org.springframework.context.annotation.Profile(value = {"local"})
@com.koosco.common.core.annotation.UseCase()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0017\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u0005\u001a\u00020\u0006H\u0016J\b\u0010\u0007\u001a\u00020\u0006H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/koosco/inventoryservice/inventory/application/usecase/InventorySeedUseCase;", "", "inventorySeedPort", "Lcom/koosco/inventoryservice/inventory/application/port/InventorySeedPort;", "(Lcom/koosco/inventoryservice/inventory/application/port/InventorySeedPort;)V", "clear", "", "init", "inventory-service"})
public class InventorySeedUseCase {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.inventoryservice.inventory.application.port.InventorySeedPort inventorySeedPort = null;
    
    public InventorySeedUseCase(@org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.application.port.InventorySeedPort inventorySeedPort) {
        super();
    }
    
    public void init() {
    }
    
    public void clear() {
    }
}