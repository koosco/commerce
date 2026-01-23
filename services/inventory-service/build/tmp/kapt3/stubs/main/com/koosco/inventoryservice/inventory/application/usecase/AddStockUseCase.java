package com.koosco.inventoryservice.inventory.application.usecase;

@com.koosco.common.core.annotation.UseCase()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016J\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\tH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"Lcom/koosco/inventoryservice/inventory/application/usecase/AddStockUseCase;", "", "inventoryStockStore", "Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockStorePort;", "(Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockStorePort;)V", "execute", "", "command", "Lcom/koosco/inventoryservice/inventory/application/command/AddStockCommand;", "Lcom/koosco/inventoryservice/inventory/application/command/BulkAddStockCommand;", "inventory-service"})
public class AddStockUseCase {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort inventoryStockStore = null;
    
    public AddStockUseCase(@org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort inventoryStockStore) {
        super();
    }
    
    public void execute(@org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.application.command.AddStockCommand command) {
    }
    
    public void execute(@org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.application.command.BulkAddStockCommand command) {
    }
}