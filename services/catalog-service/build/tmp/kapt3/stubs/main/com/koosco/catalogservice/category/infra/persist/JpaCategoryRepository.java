package com.koosco.catalogservice.category.infra.persist;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\bf\u0018\u00002\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u0001J\u001a\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\b\u001a\u0004\u0018\u00010\u0002H&J\u000e\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00020\nH&J\u0016\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00020\n2\u0006\u0010\f\u001a\u00020\rH&J\u001d\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00020\n2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0003H&\u00a2\u0006\u0002\u0010\u0010J\u000e\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00020\nH&\u00a8\u0006\u0012"}, d2 = {"Lcom/koosco/catalogservice/category/infra/persist/JpaCategoryRepository;", "Lorg/springframework/data/jpa/repository/JpaRepository;", "Lcom/koosco/catalogservice/category/domain/Category;", "", "existsByNameAndParent", "", "name", "", "parent", "findAllByOrderByDepthAscOrderingAsc", "", "findByDepthOrderByOrderingAsc", "depth", "", "findByParentIdOrderByOrderingAsc", "parentId", "(Ljava/lang/Long;)Ljava/util/List;", "findByParentIsNull", "catalog-service"})
public abstract interface JpaCategoryRepository extends org.springframework.data.jpa.repository.JpaRepository<com.koosco.catalogservice.category.domain.Category, java.lang.Long> {
    
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