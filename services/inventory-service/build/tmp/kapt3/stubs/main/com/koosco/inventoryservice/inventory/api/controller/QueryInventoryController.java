package com.koosco.inventoryservice.inventory.api.controller;

@org.springframework.web.bind.annotation.RestController()
@org.springframework.web.bind.annotation.RequestMapping(value = {"/api/inventories"})
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0018\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\b\b\u0001\u0010\b\u001a\u00020\tH\u0017J\u0018\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00062\b\b\u0001\u0010\f\u001a\u00020\rH\u0017R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/koosco/inventoryservice/inventory/api/controller/QueryInventoryController;", "", "getInventoryUseCase", "Lcom/koosco/inventoryservice/inventory/application/usecase/GetInventoryUseCase;", "(Lcom/koosco/inventoryservice/inventory/application/usecase/GetInventoryUseCase;)V", "getInventoryBySkuId", "Lcom/koosco/common/core/response/ApiResponse;", "Lcom/koosco/inventoryservice/inventory/api/response/GetInventoryResponse;", "skuId", "", "getInventoryBySkuIds", "Lcom/koosco/inventoryservice/inventory/api/response/GetInventoriesResponse;", "body", "Lcom/koosco/inventoryservice/inventory/api/request/GetInventoriesRequest;", "inventory-service"})
public class QueryInventoryController {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.inventoryservice.inventory.application.usecase.GetInventoryUseCase getInventoryUseCase = null;
    
    public QueryInventoryController(@org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.application.usecase.GetInventoryUseCase getInventoryUseCase) {
        super();
    }
    
    @io.swagger.v3.oas.annotations.Operation(summary = "\uc7ac\uace0 \uc870\ud68c", description = "SKU ID\ub85c \uc7ac\uace0 \uc815\ubcf4\ub97c \uc870\ud68c\ud569\ub2c8\ub2e4.")
    @org.springframework.web.bind.annotation.GetMapping(value = {"/{skuId}"})
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<com.koosco.inventoryservice.inventory.api.response.GetInventoryResponse> getInventoryBySkuId(@org.springframework.web.bind.annotation.PathVariable()
    @org.jetbrains.annotations.NotNull()
    java.lang.String skuId) {
        return null;
    }
    
    @io.swagger.v3.oas.annotations.Operation(summary = "\ub300\ub7c9 \uc7ac\uace0 \uc870\ud68c", description = "\uc5ec\ub7ec SKU ID\ub85c \uc7ac\uace0 \uc815\ubcf4\ub97c \ub300\ub7c9 \uc870\ud68c\ud569\ub2c8\ub2e4.")
    @org.springframework.web.bind.annotation.PostMapping(value = {"/bulk"})
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<com.koosco.inventoryservice.inventory.api.response.GetInventoriesResponse> getInventoryBySkuIds(@org.springframework.web.bind.annotation.RequestBody()
    @org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.api.request.GetInventoriesRequest body) {
        return null;
    }
}