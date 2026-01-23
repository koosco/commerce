package com.koosco.inventoryservice.inventory.application.usecase;

@com.koosco.common.core.annotation.UseCase()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\b\u001a\u00020\tH\u0016J\u0010\u0010\u0005\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\nH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/koosco/inventoryservice/inventory/application/usecase/GetInventoryUseCase;", "", "inventoryStockQuery", "Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockQueryPort;", "(Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockQueryPort;)V", "execute", "", "Lcom/koosco/inventoryservice/inventory/application/result/GetInventoryResult;", "command", "Lcom/koosco/inventoryservice/inventory/application/command/GetInventoriesCommand;", "Lcom/koosco/inventoryservice/inventory/application/command/GetInventoryCommand;", "inventory-service"})
public class GetInventoryUseCase {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.inventoryservice.inventory.application.port.InventoryStockQueryPort inventoryStockQuery = null;
    
    public GetInventoryUseCase(@org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.application.port.InventoryStockQueryPort inventoryStockQuery) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public com.koosco.inventoryservice.inventory.application.result.GetInventoryResult execute(@org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.application.command.GetInventoryCommand command) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.util.List<com.koosco.inventoryservice.inventory.application.result.GetInventoryResult> execute(@org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.application.command.GetInventoriesCommand command) {
        return null;
    }
}