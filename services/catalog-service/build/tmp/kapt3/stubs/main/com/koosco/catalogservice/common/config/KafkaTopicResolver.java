package com.koosco.catalogservice.common.config;

/**
 * fileName       : KafkaTopicResolver
 * author         : koo
 * date           : 2025. 12. 22. 오전 4:42
 * description    :
 */
@org.springframework.stereotype.Component()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/koosco/catalogservice/common/config/KafkaTopicResolver;", "Lcom/koosco/catalogservice/product/infra/messaging/TopicResolver;", "props", "Lcom/koosco/catalogservice/common/config/KafkaTopicProperties;", "(Lcom/koosco/catalogservice/common/config/KafkaTopicProperties;)V", "resolve", "", "event", "Lcom/koosco/catalogservice/product/application/contract/ProductIntegrationEvent;", "catalog-service"})
public class KafkaTopicResolver implements com.koosco.catalogservice.product.infra.messaging.TopicResolver {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.catalogservice.common.config.KafkaTopicProperties props = null;
    
    public KafkaTopicResolver(@org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.common.config.KafkaTopicProperties props) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String resolve(@org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.application.contract.ProductIntegrationEvent event) {
        return null;
    }
}