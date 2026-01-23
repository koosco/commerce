package com.koosco.inventoryservice.inventory.application.usecase;

/**
 * fileName       : ReserveStockUseCase
 * author         : koo
 * date           : 2025. 12. 22. 오전 6:33
 * description    : 재고 예약 Usecase
 */
@com.koosco.common.core.annotation.UseCase()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0018\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J \u0010\u0010\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u0012H\u0012R\u000e\u0010\u0004\u001a\u00020\u0005X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0007\u001a\n \t*\u0004\u0018\u00010\b0\bX\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0013"}, d2 = {"Lcom/koosco/inventoryservice/inventory/application/usecase/ReserveStockUseCase;", "", "inventoryStockStore", "Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockStorePort;", "integrationEventPublisher", "Lcom/koosco/inventoryservice/inventory/application/port/IntegrationEventPublisher;", "(Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockStorePort;Lcom/koosco/inventoryservice/inventory/application/port/IntegrationEventPublisher;)V", "logger", "Lorg/slf4j/Logger;", "kotlin.jvm.PlatformType", "execute", "", "command", "Lcom/koosco/inventoryservice/inventory/application/command/ReserveStockCommand;", "context", "Lcom/koosco/inventoryservice/common/MessageContext;", "publishFailed", "reason", "Lcom/koosco/inventoryservice/inventory/domain/enums/StockReservationFailReason;", "inventory-service"})
public class ReserveStockUseCase {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort inventoryStockStore = null;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.inventoryservice.inventory.application.port.IntegrationEventPublisher integrationEventPublisher = null;
    private final org.slf4j.Logger logger = null;
    
    public ReserveStockUseCase(@org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort inventoryStockStore, @org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.application.port.IntegrationEventPublisher integrationEventPublisher) {
        super();
    }
    
    public void execute(@org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.application.command.ReserveStockCommand command, @org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.common.MessageContext context) {
    }
    
    private void publishFailed(com.koosco.inventoryservice.inventory.application.command.ReserveStockCommand command, com.koosco.inventoryservice.common.MessageContext context, com.koosco.inventoryservice.inventory.domain.enums.StockReservationFailReason reason) {
    }
}