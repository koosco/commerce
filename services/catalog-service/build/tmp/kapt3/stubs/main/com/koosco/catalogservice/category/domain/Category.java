package com.koosco.catalogservice.category.domain;

@jakarta.persistence.Entity()
@jakarta.persistence.Table(name = "categories")
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010!\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u001d\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0006\b\u0017\u0018\u0000 42\u00020\u0001:\u00014Be\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0000\u0012\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00000\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\u000b\u0012\b\b\u0002\u0010\r\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u000e\u00a2\u0006\u0002\u0010\u0010J\u0010\u0010+\u001a\u00020,2\u0006\u0010-\u001a\u00020\u0000H\u0016J\u0013\u0010.\u001a\u00020/2\b\u00100\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\u0010\u00101\u001a\u00020,2\u0006\u0010\u0004\u001a\u00020\u0005H\u0016J\b\u00102\u001a\u00020\u000bH\u0016J\b\u00103\u001a\u00020,H\u0017R\u001c\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00000\t8\u0016X\u0097\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u001e\u0010\u0006\u001a\u00020\u00058\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016R\u0016\u0010\r\u001a\u00020\u000e8\u0016X\u0097\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u001e\u0010\n\u001a\u00020\u000b8\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001cR\u001a\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0016X\u0097\u0004\u00a2\u0006\n\n\u0002\u0010\u001f\u001a\u0004\b\u001d\u0010\u001eR\u001e\u0010\u0004\u001a\u00020\u00058\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b \u0010\u0014\"\u0004\b!\u0010\u0016R\u001e\u0010\f\u001a\u00020\u000b8\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\"\u0010\u001a\"\u0004\b#\u0010\u001cR \u0010\u0007\u001a\u0004\u0018\u00010\u00008\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b$\u0010%\"\u0004\b&\u0010\'R\u001e\u0010\u000f\u001a\u00020\u000e8\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b(\u0010\u0018\"\u0004\b)\u0010*\u00a8\u00065"}, d2 = {"Lcom/koosco/catalogservice/category/domain/Category;", "", "id", "", "name", "", "code", "parent", "children", "", "depth", "", "ordering", "createdAt", "Ljava/time/LocalDateTime;", "updatedAt", "(Ljava/lang/Long;Ljava/lang/String;Ljava/lang/String;Lcom/koosco/catalogservice/category/domain/Category;Ljava/util/List;IILjava/time/LocalDateTime;Ljava/time/LocalDateTime;)V", "getChildren", "()Ljava/util/List;", "getCode", "()Ljava/lang/String;", "setCode", "(Ljava/lang/String;)V", "getCreatedAt", "()Ljava/time/LocalDateTime;", "getDepth", "()I", "setDepth", "(I)V", "getId", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getName", "setName", "getOrdering", "setOrdering", "getParent", "()Lcom/koosco/catalogservice/category/domain/Category;", "setParent", "(Lcom/koosco/catalogservice/category/domain/Category;)V", "getUpdatedAt", "setUpdatedAt", "(Ljava/time/LocalDateTime;)V", "addChild", "", "child", "equals", "", "other", "hasNoDuplicateChild", "hashCode", "preUpdate", "Companion", "catalog-service"})
public class Category {
    @jakarta.persistence.Id()
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long id = null;
    @jakarta.persistence.Column(nullable = false, length = 100)
    @org.jetbrains.annotations.NotNull()
    private java.lang.String name;
    @jakarta.persistence.Column(name = "code", nullable = false, unique = true, length = 50)
    @org.jetbrains.annotations.NotNull()
    private java.lang.String code;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "parent_id")
    @org.jetbrains.annotations.Nullable()
    private com.koosco.catalogservice.category.domain.Category parent;
    @jakarta.persistence.OneToMany(mappedBy = "parent", cascade = {jakarta.persistence.CascadeType.ALL}, orphanRemoval = true)
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.koosco.catalogservice.category.domain.Category> children = null;
    @jakarta.persistence.Column(nullable = false)
    private int depth;
    @jakarta.persistence.Column(nullable = false)
    private int ordering;
    @jakarta.persistence.Column(name = "created_at", nullable = false, updatable = false)
    @org.jetbrains.annotations.NotNull()
    private final java.time.LocalDateTime createdAt = null;
    @jakarta.persistence.Column(name = "updated_at", nullable = false)
    @org.jetbrains.annotations.NotNull()
    private java.time.LocalDateTime updatedAt;
    @org.jetbrains.annotations.NotNull()
    public static final com.koosco.catalogservice.category.domain.Category.Companion Companion = null;
    
    public Category(@org.jetbrains.annotations.Nullable()
    java.lang.Long id, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String code, @org.jetbrains.annotations.Nullable()
    com.koosco.catalogservice.category.domain.Category parent, @org.jetbrains.annotations.NotNull()
    java.util.List<com.koosco.catalogservice.category.domain.Category> children, int depth, int ordering, @org.jetbrains.annotations.NotNull()
    java.time.LocalDateTime createdAt, @org.jetbrains.annotations.NotNull()
    java.time.LocalDateTime updatedAt) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public java.lang.Long getId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.lang.String getName() {
        return null;
    }
    
    public void setName(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.lang.String getCode() {
        return null;
    }
    
    public void setCode(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public com.koosco.catalogservice.category.domain.Category getParent() {
        return null;
    }
    
    public void setParent(@org.jetbrains.annotations.Nullable()
    com.koosco.catalogservice.category.domain.Category p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.util.List<com.koosco.catalogservice.category.domain.Category> getChildren() {
        return null;
    }
    
    public int getDepth() {
        return 0;
    }
    
    public void setDepth(int p0) {
    }
    
    public int getOrdering() {
        return 0;
    }
    
    public void setOrdering(int p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.time.LocalDateTime getCreatedAt() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.time.LocalDateTime getUpdatedAt() {
        return null;
    }
    
    public void setUpdatedAt(@org.jetbrains.annotations.NotNull()
    java.time.LocalDateTime p0) {
    }
    
    @jakarta.persistence.PreUpdate()
    public void preUpdate() {
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    public void hasNoDuplicateChild(@org.jetbrains.annotations.NotNull()
    java.lang.String name) {
    }
    
    public void addChild(@org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.category.domain.Category child) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001a\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\u0004H\u0002J\u000e\u0010\b\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006J$\u0010\t\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\u000b2\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00042\b\b\u0002\u0010\f\u001a\u00020\r\u00a8\u0006\u000e"}, d2 = {"Lcom/koosco/catalogservice/category/domain/Category$Companion;", "", "()V", "createNodeRecursively", "Lcom/koosco/catalogservice/category/domain/Category;", "command", "Lcom/koosco/catalogservice/category/application/dto/CreateCategoryTreeCommand;", "parent", "createTree", "of", "name", "", "ordering", "", "catalog-service"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.koosco.catalogservice.category.domain.Category of(@org.jetbrains.annotations.NotNull()
        java.lang.String name, @org.jetbrains.annotations.Nullable()
        com.koosco.catalogservice.category.domain.Category parent, int ordering) {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.koosco.catalogservice.category.domain.Category createTree(@org.jetbrains.annotations.NotNull()
        com.koosco.catalogservice.category.application.dto.CreateCategoryTreeCommand command) {
            return null;
        }
        
        private final com.koosco.catalogservice.category.domain.Category createNodeRecursively(com.koosco.catalogservice.category.application.dto.CreateCategoryTreeCommand command, com.koosco.catalogservice.category.domain.Category parent) {
            return null;
        }
    }
}