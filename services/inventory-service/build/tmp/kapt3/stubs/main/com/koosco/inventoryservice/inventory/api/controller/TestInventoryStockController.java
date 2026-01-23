package com.koosco.inventoryservice.inventory.api.controller;

/**
 * fileName       : TestInventoryStockController
 * author         : koo
 * date           : 2025. 12. 26. 오전 4:58
 * description    : 테스트용 재고 초기화 컨트롤러 (local 환경에서만 사용)
 */
@org.springframework.context.annotation.Profile(value = {"local"})
@org.springframework.web.bind.annotation.RestController()
@org.springframework.web.bind.annotation.RequestMapping(value = {"/api/inventories/test"})
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0017\u0018\u00002\u00020\u0001:\u0001\u0010B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\b\u0010\u0007\u001a\u00020\bH\u0017J\"\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\n2\b\b\u0001\u0010\u000b\u001a\u00020\f2\b\b\u0001\u0010\r\u001a\u00020\u000eH\u0017J\b\u0010\u000f\u001a\u00020\bH\u0017R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lcom/koosco/inventoryservice/inventory/api/controller/TestInventoryStockController;", "", "inventorySeedUseCase", "Lcom/koosco/inventoryservice/inventory/application/usecase/InventorySeedUseCase;", "reduceStockUseCase", "Lcom/koosco/inventoryservice/inventory/application/usecase/ReduceStockUseCase;", "(Lcom/koosco/inventoryservice/inventory/application/usecase/InventorySeedUseCase;Lcom/koosco/inventoryservice/inventory/application/usecase/ReduceStockUseCase;)V", "clearStock", "", "decreaseStock", "Lcom/koosco/common/core/response/ApiResponse;", "skuId", "", "request", "Lcom/koosco/inventoryservice/inventory/api/controller/TestInventoryStockController$StockDto;", "initStock", "StockDto", "inventory-service"})
public class TestInventoryStockController {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.inventoryservice.inventory.application.usecase.InventorySeedUseCase inventorySeedUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.inventoryservice.inventory.application.usecase.ReduceStockUseCase reduceStockUseCase = null;
    
    public TestInventoryStockController(@org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.application.usecase.InventorySeedUseCase inventorySeedUseCase, @org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.application.usecase.ReduceStockUseCase reduceStockUseCase) {
        super();
    }
    
    @org.springframework.web.bind.annotation.PostMapping(value = {"/init"})
    public void initStock() {
    }
    
    @org.springframework.web.bind.annotation.PostMapping(value = {"/clear"})
    public void clearStock() {
    }
    
    @org.springframework.web.bind.annotation.PostMapping(value = {"/decrease/{skuId}"})
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<java.lang.Object> decreaseStock(@org.springframework.web.bind.annotation.PathVariable(value = "skuId")
    @org.jetbrains.annotations.NotNull()
    java.lang.String skuId, @org.springframework.web.bind.annotation.RequestBody()
    @org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.api.controller.TestInventoryStockController.StockDto request) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\f\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u000f"}, d2 = {"Lcom/koosco/inventoryservice/inventory/api/controller/TestInventoryStockController$StockDto;", "", "quantity", "", "(I)V", "getQuantity", "()I", "component1", "copy", "equals", "", "other", "hashCode", "toString", "", "inventory-service"})
    public static final class StockDto {
        private final int quantity = 0;
        
        public StockDto(int quantity) {
            super();
        }
        
        public final int getQuantity() {
            return 0;
        }
        
        public final int component1() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.koosco.inventoryservice.inventory.api.controller.TestInventoryStockController.StockDto copy(int quantity) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
}