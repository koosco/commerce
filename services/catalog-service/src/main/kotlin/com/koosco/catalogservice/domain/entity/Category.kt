package com.koosco.catalogservice.domain.entity

import com.koosco.catalogservice.application.command.CreateCategoryTreeCommand
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.catalogservice.domain.service.CategoryCodeGenerator
import com.koosco.common.core.exception.ConflictException
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "categories")
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 100)
    var name: String,

    @Column(name = "code", nullable = false, unique = true, length = 50)
    var code: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    var parent: Category? = null,

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL], orphanRemoval = true)
    val children: MutableList<Category> = mutableListOf(),

    @Column(nullable = false)
    var depth: Int = 0,

    @Column(nullable = false)
    var ordering: Int = 0,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }

    fun hasNoDuplicateChild(name: String) {
        if (children.any { it.name == name }) {
            throw ConflictException(
                errorCode = CatalogErrorCode.CATEGORY_NAME_CONFLICT,
                message = "${this.name} 하위에 이미 ${name}이 존재합니다.",
            )
        }
    }

    fun addChild(child: Category) {
        hasNoDuplicateChild(child.name)
        children.add(child)
    }

    companion object {

        fun of(name: String, parent: Category? = null, ordering: Int = 0): Category {
            val code = CategoryCodeGenerator.generate(name)
            val depth = parent?.depth?.plus(1) ?: 0

            return Category(
                name = name,
                code = code,
                parent = parent,
                depth = depth,
                ordering = ordering,
            )
        }

        fun createTree(command: CreateCategoryTreeCommand): Category = createNodeRecursively(command, null)
        private fun createNodeRecursively(command: CreateCategoryTreeCommand, parent: Category?): Category {
            parent?.hasNoDuplicateChild(command.name)

            // 현재 노드 생성
            val category = of(
                name = command.name,
                parent = parent,
                ordering = command.ordering,
            )

            // 부모-자식 관계 설정
            parent?.addChild(category)

            // 자식 생성
            command.children.forEach { childCommand ->
                createNodeRecursively(childCommand, category)
            }

            return category
        }
    }
}
