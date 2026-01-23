package com.koosco.catalogservice.product.application.port;

/**
 * 도메인 이벤트 발행 인터페이스
 * - UoW 패턴과 함께 사용하여 트랜잭션 내에서 발생한 도메인 이벤트를 발행
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\u0016\u0010\u0006\u001a\u00020\u00032\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00050\bH&\u00a8\u0006\t"}, d2 = {"Lcom/koosco/catalogservice/product/application/port/DomainEventPublisher;", "", "publish", "", "event", "Lcom/koosco/common/core/event/DomainEvent;", "publishAll", "events", "", "catalog-service"})
public abstract interface DomainEventPublisher {
    
    public abstract void publish(@org.jetbrains.annotations.NotNull()
    com.koosco.common.core.event.DomainEvent event);
    
    public abstract void publishAll(@org.jetbrains.annotations.NotNull()
    java.util.List<? extends com.koosco.common.core.event.DomainEvent> events);
}