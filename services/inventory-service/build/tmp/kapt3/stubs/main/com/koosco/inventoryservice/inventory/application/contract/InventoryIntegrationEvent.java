package com.koosco.inventoryservice.inventory.application.contract;

/**
 * fileName       : InventoryIntegrationEvent
 * author         : koo
 * date           : 2025. 12. 24. 오전 2:30
 * description    :
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\b\u0010\u0006\u001a\u00020\u0007H&J\b\u0010\b\u001a\u00020\u0007H\u0016J\b\u0010\t\u001a\u00020\u0007H\u0016J\u0016\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\u000b2\u0006\u0010\f\u001a\u00020\u0007H\u0016R\u0012\u0010\u0002\u001a\u00020\u0003X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005\u00a8\u0006\r"}, d2 = {"Lcom/koosco/inventoryservice/inventory/application/contract/InventoryIntegrationEvent;", "", "orderId", "", "getOrderId", "()J", "getEventType", "", "getPartitionKey", "getSubject", "toCloudEvent", "Lcom/koosco/common/core/event/CloudEvent;", "source", "inventory-service"})
public abstract interface InventoryIntegrationEvent {
    
    public abstract long getOrderId();
    
    /**
     * CloudEvent type
     * 예: stock.reserve.failed
     */
    @org.jetbrains.annotations.NotNull()
    public abstract java.lang.String getEventType();
    
    /**
     * Kafka partition key
     */
    @org.jetbrains.annotations.NotNull()
    public abstract java.lang.String getPartitionKey();
    
    /**
     * CloudEvent subject (선택)
     */
    @org.jetbrains.annotations.NotNull()
    public abstract java.lang.String getSubject();
    
    /**
     * CloudEvent 변환 (공통)
     */
    @org.jetbrains.annotations.NotNull()
    public abstract com.koosco.common.core.event.CloudEvent<java.lang.Object> toCloudEvent(@org.jetbrains.annotations.NotNull()
    java.lang.String source);
    
    /**
     * fileName       : InventoryIntegrationEvent
     * author         : koo
     * date           : 2025. 12. 24. 오전 2:30
     * description    :
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
        
        /**
         * Kafka partition key
         */
        @org.jetbrains.annotations.NotNull()
        public static java.lang.String getPartitionKey(@org.jetbrains.annotations.NotNull()
        com.koosco.inventoryservice.inventory.application.contract.InventoryIntegrationEvent $this) {
            return null;
        }
        
        /**
         * CloudEvent subject (선택)
         */
        @org.jetbrains.annotations.NotNull()
        public static java.lang.String getSubject(@org.jetbrains.annotations.NotNull()
        com.koosco.inventoryservice.inventory.application.contract.InventoryIntegrationEvent $this) {
            return null;
        }
        
        /**
         * CloudEvent 변환 (공통)
         */
        @org.jetbrains.annotations.NotNull()
        public static com.koosco.common.core.event.CloudEvent<java.lang.Object> toCloudEvent(@org.jetbrains.annotations.NotNull()
        com.koosco.inventoryservice.inventory.application.contract.InventoryIntegrationEvent $this, @org.jetbrains.annotations.NotNull()
        java.lang.String source) {
            return null;
        }
    }
}