package com.koosco.catalogservice.category.infra.persist;

@org.springframework.stereotype.Repository()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0007\b\u0017\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001a\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\nH\u0016J\u000e\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\n0\fH\u0016J\u0016\u0010\r\u001a\b\u0012\u0004\u0012\u00020\n0\f2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J\u0012\u0010\u0010\u001a\u0004\u0018\u00010\n2\u0006\u0010\u0011\u001a\u00020\u0012H\u0016J\u001d\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\n0\f2\b\u0010\u0014\u001a\u0004\u0018\u00010\u0012H\u0016\u00a2\u0006\u0002\u0010\u0015J\u000e\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\n0\fH\u0016J\u0010\u0010\u0017\u001a\u00020\n2\u0006\u0010\u0018\u001a\u00020\nH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2 = {"Lcom/koosco/catalogservice/category/infra/persist/CategoryRepositoryImpl;", "Lcom/koosco/catalogservice/category/application/repository/CategoryRepository;", "jpaCategoryRepository", "Lcom/koosco/catalogservice/category/infra/persist/JpaCategoryRepository;", "(Lcom/koosco/catalogservice/category/infra/persist/JpaCategoryRepository;)V", "existsByNameAndParent", "", "name", "", "parent", "Lcom/koosco/catalogservice/category/domain/Category;", "findAllByOrderByDepthAscOrderingAsc", "", "findByDepthOrderByOrderingAsc", "depth", "", "findByIdOrNull", "id", "", "findByParentIdOrderByOrderingAsc", "parentId", "(Ljava/lang/Long;)Ljava/util/List;", "findByParentIsNull", "save", "category", "catalog-service"})
public class CategoryRepositoryImpl implements com.koosco.catalogservice.category.application.repository.CategoryRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.catalogservice.category.infra.persist.JpaCategoryRepository jpaCategoryRepository = null;
    
    public CategoryRepositoryImpl(@org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.category.infra.persist.JpaCategoryRepository jpaCategoryRepository) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.koosco.catalogservice.category.domain.Category save(@org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.category.domain.Category category) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public com.koosco.catalogservice.category.domain.Category findByIdOrNull(long id) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.util.List<com.koosco.catalogservice.category.domain.Category> findByParentIdOrderByOrderingAsc(@org.jetbrains.annotations.Nullable()
    java.lang.Long parentId) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.util.List<com.koosco.catalogservice.category.domain.Category> findByParentIsNull() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.util.List<com.koosco.catalogservice.category.domain.Category> findByDepthOrderByOrderingAsc(int depth) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.util.List<com.koosco.catalogservice.category.domain.Category> findAllByOrderByDepthAscOrderingAsc() {
        return null;
    }
    
    @java.lang.Override()
    public boolean existsByNameAndParent(@org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.Nullable()
    com.koosco.catalogservice.category.domain.Category parent) {
        return false;
    }
}