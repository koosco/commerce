package com.koosco.inventoryservice.inventory.api.controller;

@org.springframework.web.bind.annotation.RestController()
@org.springframework.web.bind.annotation.RequestMapping(value = {"/api/inventories"})
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0017\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0017\u00a8\u0006\u0005"}, d2 = {"Lcom/koosco/inventoryservice/inventory/api/controller/AdminController;", "", "()V", "getInventoryChangeLogs", "", "inventory-service"})
public class AdminController {
    
    public AdminController() {
        super();
    }
    
    @io.swagger.v3.oas.annotations.Operation(summary = "\uae30\uac04\ub0b4 \uc7ac\uace0 \ubcc0\uacbd \ub85c\uadf8 \uc870\ud68c", description = "\uad00\ub9ac\uc790\uc6a9\uc73c\ub85c \uae30\uac04\ub0b4 \uc7ac\uace0 \ubcc0\uacbd \ub85c\uadf8\ub97c \uc870\ud68c\ud569\ub2c8\ub2e4.")
    @org.springframework.security.access.prepost.PreAuthorize(value = "hasRole(\'ADMIN\')")
    @org.jetbrains.annotations.NotNull()
    public java.lang.String getInventoryChangeLogs() {
        return null;
    }
}