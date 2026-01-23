package com.koosco.inventoryservice.inventory.infra.messaging;

/**
 * fileName       : IntegrationTopicResolver
 * author         : koo
 * date           : 2025. 12. 19. 오후 3:00
 * description    :
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&\u00a8\u0006\u0006"}, d2 = {"Lcom/koosco/inventoryservice/inventory/infra/messaging/IntegrationTopicResolver;", "", "resolve", "", "event", "Lcom/koosco/inventoryservice/inventory/application/contract/InventoryIntegrationEvent;", "inventory-service"})
public abstract interface IntegrationTopicResolver {
    
    @org.jetbrains.annotations.NotNull()
    public abstract java.lang.String resolve(@org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.application.contract.InventoryIntegrationEvent event);
}