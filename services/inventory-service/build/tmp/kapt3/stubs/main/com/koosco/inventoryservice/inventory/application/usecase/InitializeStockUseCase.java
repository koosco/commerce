package com.koosco.inventoryservice.inventory.application.usecase;

/**
 * 재고 초기화 유스케이스
 * 새로운 상품이 생성되었을 때 재고를 초기화합니다.
 */
@com.koosco.common.core.annotation.UseCase()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0005\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/koosco/inventoryservice/inventory/application/usecase/InitializeStockUseCase;", "", "inventoryStockStore", "Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockStorePort;", "(Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockStorePort;)V", "logger", "Lorg/slf4j/Logger;", "kotlin.jvm.PlatformType", "execute", "", "command", "Lcom/koosco/inventoryservice/inventory/application/command/InitStockCommand;", "inventory-service"})
public class InitializeStockUseCase {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort inventoryStockStore = null;
    private final org.slf4j.Logger logger = null;
    
    public InitializeStockUseCase(@org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort inventoryStockStore) {
        super();
    }
    
    /**
     * 재고 초기화 처리
     *
     * @param command 재고 초기화 정보 (skuId, initialQuantity)
     */
    public void execute(@org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.application.command.InitStockCommand command) {
    }
}