package com.koosco.catalogservice.category.application.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0007\bf\u0018\u00002\u00020\u0001J\u001a\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H&J\u000e\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\tH&J\u0016\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00070\t2\u0006\u0010\u000b\u001a\u00020\fH&J\u0012\u0010\r\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u000e\u001a\u00020\u000fH&J\u001d\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00070\t2\b\u0010\u0011\u001a\u0004\u0018\u00010\u000fH&\u00a2\u0006\u0002\u0010\u0012J\u000e\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00070\tH&J\u0010\u0010\u0014\u001a\u00020\u00072\u0006\u0010\u0015\u001a\u00020\u0007H&\u00a8\u0006\u0016"}, d2 = {"Lcom/koosco/catalogservice/category/application/repository/CategoryRepository;", "", "existsByNameAndParent", "", "name", "", "parent", "Lcom/koosco/catalogservice/category/domain/Category;", "findAllByOrderByDepthAscOrderingAsc", "", "findByDepthOrderByOrderingAsc", "depth", "", "findByIdOrNull", "id", "", "findByParentIdOrderByOrderingAsc", "parentId", "(Ljava/lang/Long;)Ljava/util/List;", "findByParentIsNull", "save", "category", "catalog-service"})
public abstract interface CategoryRepository {
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.koosco.catalogservice.category.domain.Category save(@org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.category.domain.Category category);
    
    @org.jetbrains.annotations.Nullable()
    public abstract com.koosco.catalogservice.category.domain.Category findByIdOrNull(long id);
    
    @org.jetbrains.annotations.NotNull()
    public abstract java.util.List<com.koosco.catalogservice.category.domain.Category> findByParentIdOrderByOrderingAsc(@org.jetbrains.annotations.Nullable()
    java.lang.Long parentId);
    
    @org.jetbrains.annotations.NotNull()
    public abstract java.util.List<com.koosco.catalogservice.category.domain.Category> findByParentIsNull();
    
    @org.jetbrains.annotations.NotNull()
    public abstract java.util.List<com.koosco.catalogservice.category.domain.Category> findByDepthOrderByOrderingAsc(int depth);
    
    @org.jetbrains.annotations.NotNull()
    public abstract java.util.List<com.koosco.catalogservice.category.domain.Category> findAllByOrderByDepthAscOrderingAsc();
    
    public abstract boolean existsByNameAndParent(@org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.Nullable()
    com.koosco.catalogservice.category.domain.Category parent);
}