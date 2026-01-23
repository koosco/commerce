package com.koosco.inventoryservice.inventory.api.controller;

@io.swagger.v3.oas.annotations.tags.Tag(name = "Inventory Stock Controller", description = "\uc7ac\uace0 \uad00\ub9ac API")
@org.springframework.web.bind.annotation.RestController()
@org.springframework.web.bind.annotation.RequestMapping(value = {"/api/inventories"})
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0018\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\b2\b\b\u0001\u0010\t\u001a\u00020\nH\u0017J\"\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\b2\b\b\u0001\u0010\f\u001a\u00020\r2\b\b\u0001\u0010\t\u001a\u00020\u000eH\u0017J\u0018\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00010\b2\b\b\u0001\u0010\t\u001a\u00020\u0010H\u0017J\"\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00010\b2\b\b\u0001\u0010\f\u001a\u00020\r2\b\b\u0001\u0010\t\u001a\u00020\u0012H\u0017R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0013"}, d2 = {"Lcom/koosco/inventoryservice/inventory/api/controller/InventoryStockController;", "", "addStockUseCase", "Lcom/koosco/inventoryservice/inventory/application/usecase/AddStockUseCase;", "reduceStockUseCase", "Lcom/koosco/inventoryservice/inventory/application/usecase/ReduceStockUseCase;", "(Lcom/koosco/inventoryservice/inventory/application/usecase/AddStockUseCase;Lcom/koosco/inventoryservice/inventory/application/usecase/ReduceStockUseCase;)V", "addBulkInventories", "Lcom/koosco/common/core/response/ApiResponse;", "body", "Lcom/koosco/inventoryservice/inventory/api/request/BulkAddStockRequest;", "addInventory", "skuId", "", "Lcom/koosco/inventoryservice/inventory/api/request/AddStockRequest;", "reduceBulkInventories", "Lcom/koosco/inventoryservice/inventory/api/request/BulkReduceStockRequest;", "reduceInventory", "Lcom/koosco/inventoryservice/inventory/api/request/ReduceStockRequest;", "inventory-service"})
public class InventoryStockController {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.inventoryservice.inventory.application.usecase.AddStockUseCase addStockUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.inventoryservice.inventory.application.usecase.ReduceStockUseCase reduceStockUseCase = null;
    
    public InventoryStockController(@org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.application.usecase.AddStockUseCase addStockUseCase, @org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.application.usecase.ReduceStockUseCase reduceStockUseCase) {
        super();
    }
    
    @io.swagger.v3.oas.annotations.Operation(summary = "\uc7ac\uace0 \ucd94\uac00", description = "SKU ID\ub85c \uc7ac\uace0\ub97c \ucd94\uac00\ud569\ub2c8\ub2e4.")
    @org.springframework.web.bind.annotation.PostMapping(value = {"/{skuId}/increase"})
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<java.lang.Object> addInventory(@org.springframework.web.bind.annotation.PathVariable()
    @org.jetbrains.annotations.NotNull()
    java.lang.String skuId, @org.springframework.web.bind.annotation.RequestBody()
    @org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.api.request.AddStockRequest body) {
        return null;
    }
    
    @io.swagger.v3.oas.annotations.Operation(summary = "\ub300\ub7c9 \uc7ac\uace0 \ucd94\uac00", description = "\uc5ec\ub7ec SKU ID\ub85c \uc7ac\uace0\ub97c \ub300\ub7c9 \ucd94\uac00\ud569\ub2c8\ub2e4.")
    @org.springframework.web.bind.annotation.PostMapping(value = {"/increase"})
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<java.lang.Object> addBulkInventories(@org.springframework.web.bind.annotation.RequestBody()
    @org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.api.request.BulkAddStockRequest body) {
        return null;
    }
    
    @io.swagger.v3.oas.annotations.Operation(summary = "\uc7ac\uace0 \uac10\uc18c", description = "SKU ID\ub85c \uc7ac\uace0\ub97c \uac10\uc18c\ud569\ub2c8\ub2e4.")
    @org.springframework.web.bind.annotation.PostMapping(value = {"/{skuId}/decrease"})
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<java.lang.Object> reduceInventory(@org.springframework.web.bind.annotation.PathVariable()
    @org.jetbrains.annotations.NotNull()
    java.lang.String skuId, @org.springframework.web.bind.annotation.RequestBody()
    @org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.api.request.ReduceStockRequest body) {
        return null;
    }
    
    @io.swagger.v3.oas.annotations.Operation(summary = "\ub300\ub7c9 \uc7ac\uace0 \uac10\uc18c", description = "SKU ID\ub85c \uc7ac\uace0\ub97c \ub300\ub7c9\uc73c\ub85c \uac10\uc18c\ud569\ub2c8\ub2e4.")
    @org.springframework.web.bind.annotation.PostMapping(value = {"/decrease"})
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<java.lang.Object> reduceBulkInventories(@org.springframework.web.bind.annotation.RequestBody()
    @org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.api.request.BulkReduceStockRequest body) {
        return null;
    }
}