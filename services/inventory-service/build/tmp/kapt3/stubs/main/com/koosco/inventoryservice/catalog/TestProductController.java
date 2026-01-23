package com.koosco.inventoryservice.catalog;

/**
 * fileName       : TestProductController
 * author         : koo
 * date           : 2025. 12. 27. 오후 6:52
 * description    : Integration Event 발행 테스트를 위한 Controller, local profile only
 */
@org.springframework.context.annotation.Profile(value = {"local"})
@org.springframework.web.bind.annotation.RestController()
@org.springframework.web.bind.annotation.RequestMapping(value = {"/api/products/test"})
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0017J \u0010\n\u001a\u001a\u0012\u0016\u0012\u0014\u0012\u0004\u0012\u00020\f\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00010\r0\u000b0\bH\u0017J\u0014\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\r0\bH\u0017R\u000e\u0010\u0004\u001a\u00020\u0005X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0010"}, d2 = {"Lcom/koosco/inventoryservice/catalog/TestProductController;", "", "productService", "Lcom/koosco/inventoryservice/catalog/TestProductService;", "productConsumer", "Lcom/koosco/inventoryservice/catalog/TestProductConsumer;", "(Lcom/koosco/inventoryservice/catalog/TestProductService;Lcom/koosco/inventoryservice/catalog/TestProductConsumer;)V", "clearReceivedEvents", "Lcom/koosco/common/core/response/ApiResponse;", "", "getReceivedEvents", "", "", "", "productSkuCreated", "Lcom/koosco/inventoryservice/inventory/application/contract/inbound/catalog/ProductSkuCreatedEvent;", "inventory-service"})
public class TestProductController {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.inventoryservice.catalog.TestProductService productService = null;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.inventoryservice.catalog.TestProductConsumer productConsumer = null;
    
    public TestProductController(@org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.catalog.TestProductService productService, @org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.catalog.TestProductConsumer productConsumer) {
        super();
    }
    
    @org.springframework.web.bind.annotation.PostMapping(value = {"/sku/created"})
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<java.util.List<com.koosco.inventoryservice.inventory.application.contract.inbound.catalog.ProductSkuCreatedEvent>> productSkuCreated() {
        return null;
    }
    
    @org.springframework.web.bind.annotation.GetMapping()
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<java.util.Map<java.lang.String, java.util.List<java.lang.Object>>> getReceivedEvents() {
        return null;
    }
    
    @org.springframework.web.bind.annotation.DeleteMapping(value = {"/clear"})
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<kotlin.Unit> clearReceivedEvents() {
        return null;
    }
}