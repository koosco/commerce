package com.koosco.catalogservice.product.application.contract;

/**
 * fileName       : CatalogIntegrationEvent
 * author         : koo
 * date           : 2025. 12. 22. 오전 9:30
 * description    :
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\b\u0010\u0006\u001a\u00020\u0003H&J\b\u0010\u0007\u001a\u00020\u0003H\u0016J\b\u0010\b\u001a\u00020\u0003H\u0016J\u0016\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00000\n2\u0006\u0010\u000b\u001a\u00020\u0003H\u0016R\u0012\u0010\u0002\u001a\u00020\u0003X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005\u00a8\u0006\f"}, d2 = {"Lcom/koosco/catalogservice/product/application/contract/ProductIntegrationEvent;", "", "skuId", "", "getSkuId", "()Ljava/lang/String;", "getEventType", "getPartitionKey", "getSubject", "toCloudEvent", "Lcom/koosco/common/core/event/CloudEvent;", "source", "catalog-service"})
public abstract interface ProductIntegrationEvent {
    
    @org.jetbrains.annotations.NotNull()
    public abstract java.lang.String getSkuId();
    
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
    public abstract com.koosco.common.core.event.CloudEvent<com.koosco.catalogservice.product.application.contract.ProductIntegrationEvent> toCloudEvent(@org.jetbrains.annotations.NotNull()
    java.lang.String source);
    
    /**
     * fileName       : CatalogIntegrationEvent
     * author         : koo
     * date           : 2025. 12. 22. 오전 9:30
     * description    :
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
        
        /**
         * Kafka partition key
         */
        @org.jetbrains.annotations.NotNull()
        public static java.lang.String getPartitionKey(@org.jetbrains.annotations.NotNull()
        com.koosco.catalogservice.product.application.contract.ProductIntegrationEvent $this) {
            return null;
        }
        
        /**
         * CloudEvent subject (선택)
         */
        @org.jetbrains.annotations.NotNull()
        public static java.lang.String getSubject(@org.jetbrains.annotations.NotNull()
        com.koosco.catalogservice.product.application.contract.ProductIntegrationEvent $this) {
            return null;
        }
        
        /**
         * CloudEvent 변환 (공통)
         */
        @org.jetbrains.annotations.NotNull()
        public static com.koosco.common.core.event.CloudEvent<com.koosco.catalogservice.product.application.contract.ProductIntegrationEvent> toCloudEvent(@org.jetbrains.annotations.NotNull()
        com.koosco.catalogservice.product.application.contract.ProductIntegrationEvent $this, @org.jetbrains.annotations.NotNull()
        java.lang.String source) {
            return null;
        }
    }
}