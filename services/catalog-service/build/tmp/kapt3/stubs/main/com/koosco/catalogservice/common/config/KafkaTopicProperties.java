package com.koosco.catalogservice.common.config;

/**
 * fileName       : KafkaTopicProperties
 * author         : koo
 * date           : 2025. 12. 22. 오전 4:43
 * description    :
 */
@org.springframework.stereotype.Component()
@org.springframework.boot.context.properties.ConfigurationProperties(prefix = "catalog.topic")
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010$\n\u0002\b\u0005\b\u0017\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002R\u001a\u0010\u0003\u001a\u00020\u0004X\u0096.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR&\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\nX\u0096.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000e\u00a8\u0006\u000f"}, d2 = {"Lcom/koosco/catalogservice/common/config/KafkaTopicProperties;", "", "()V", "default", "", "getDefault", "()Ljava/lang/String;", "setDefault", "(Ljava/lang/String;)V", "mappings", "", "getMappings", "()Ljava/util/Map;", "setMappings", "(Ljava/util/Map;)V", "catalog-service"})
public class KafkaTopicProperties {
    public java.util.Map<java.lang.String, java.lang.String> mappings;
    
    public KafkaTopicProperties() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.util.Map<java.lang.String, java.lang.String> getMappings() {
        return null;
    }
    
    public void setMappings(@org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.String> p0) {
    }
    
    /**
     * fallback topic
     */
    @org.jetbrains.annotations.NotNull()
    public java.lang.String getDefault() {
        return null;
    }
    
    /**
     * fallback topic
     */
    public void setDefault(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
}