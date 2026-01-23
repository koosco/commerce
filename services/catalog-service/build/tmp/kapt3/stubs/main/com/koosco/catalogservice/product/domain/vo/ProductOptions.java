package com.koosco.catalogservice.product.domain.vo;

/**
 * 상품 옵션 조합을 나타내는 Value Object
 * - 옵션의 순서와 무관하게 동일한 키-값 쌍을 가지면 동일한 것으로 간주
 * - 내부적으로 정규화된 형태로 저장하여 일관된 비교 보장
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\b\u0086\b\u0018\u0000 \u00122\u00020\u0001:\u0001\u0012B\u0019\u0012\u0012\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\u0010\u0005J\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u0003J\u0015\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u0003H\u00c2\u0003J\u001f\u0010\t\u001a\u00020\u00002\u0014\b\u0002\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\b\u0010\r\u001a\u00020\u000eH\u0016J\u0006\u0010\u000f\u001a\u00020\u000bJ\u0006\u0010\u0010\u001a\u00020\u0004J\b\u0010\u0011\u001a\u00020\u0004H\u0016R\u001a\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0013"}, d2 = {"Lcom/koosco/catalogservice/product/domain/vo/ProductOptions;", "", "options", "", "", "(Ljava/util/Map;)V", "normalized", "asMap", "component1", "copy", "equals", "", "other", "hashCode", "", "isEmpty", "toJson", "toString", "Companion", "catalog-service"})
public final class ProductOptions {
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, java.lang.String> options = null;
    @org.jetbrains.annotations.NotNull()
    private static final com.fasterxml.jackson.databind.ObjectMapper objectMapper = null;
    
    /**
     * 정규화된 옵션 (알파벳 순 정렬)
     * - 비교 시 순서와 무관하게 일관된 결과 보장
     */
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, java.lang.String> normalized = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.koosco.catalogservice.product.domain.vo.ProductOptions.Companion Companion = null;
    
    public ProductOptions(@org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.String> options) {
        super();
    }
    
    /**
     * JSON 문자열로 변환 (정렬된 순서 유지)
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String toJson() {
        return null;
    }
    
    /**
     * 원본 Map 반환
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, java.lang.String> asMap() {
        return null;
    }
    
    /**
     * 옵션이 비어있는지 확인
     */
    public final boolean isEmpty() {
        return false;
    }
    
    /**
     * 동일성 비교 - 정규화된 옵션 기준
     */
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
    
    private final java.util.Map<java.lang.String, java.lang.String> component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.koosco.catalogservice.product.domain.vo.ProductOptions copy(@org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.String> options) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001a\u0010\u0005\u001a\u00020\u00062\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\t0\bJ\u000e\u0010\n\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\tR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/koosco/catalogservice/product/domain/vo/ProductOptions$Companion;", "", "()V", "objectMapper", "Lcom/fasterxml/jackson/databind/ObjectMapper;", "from", "Lcom/koosco/catalogservice/product/domain/vo/ProductOptions;", "options", "", "", "fromJson", "json", "catalog-service"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        /**
         * JSON 문자열로부터 ProductOptions 생성
         */
        @org.jetbrains.annotations.NotNull()
        public final com.koosco.catalogservice.product.domain.vo.ProductOptions fromJson(@org.jetbrains.annotations.NotNull()
        java.lang.String json) {
            return null;
        }
        
        /**
         * Map으로부터 ProductOptions 생성
         */
        @org.jetbrains.annotations.NotNull()
        public final com.koosco.catalogservice.product.domain.vo.ProductOptions from(@org.jetbrains.annotations.NotNull()
        java.util.Map<java.lang.String, java.lang.String> options) {
            return null;
        }
    }
}