package com.koosco.catalogservice.domain.entity

import com.koosco.catalogservice.domain.vo.CategoryTreeSpec
import com.koosco.common.core.exception.ConflictException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Category 도메인 테스트")
class CategoryTest {

    @Nested
    @DisplayName("of 메서드는")
    inner class OfTest {

        @Test
        fun `루트 카테고리를 생성한다`() {
            val category = Category.of(name = "전자제품")

            assertThat(category.name).isEqualTo("전자제품")
            assertThat(category.depth).isEqualTo(0)
            assertThat(category.parent).isNull()
            assertThat(category.code).isNotBlank()
        }

        @Test
        fun `부모가 있는 카테고리를 생성한다`() {
            val parent = Category.of(name = "전자제품")
            val child = Category.of(name = "스마트폰", parent = parent)

            assertThat(child.depth).isEqualTo(1)
            assertThat(child.parent).isEqualTo(parent)
        }

        @Test
        fun `ordering을 지정할 수 있다`() {
            val category = Category.of(name = "패션", ordering = 5)

            assertThat(category.ordering).isEqualTo(5)
        }
    }

    @Nested
    @DisplayName("hasNoDuplicateChild 메서드는")
    inner class HasNoDuplicateChildTest {

        @Test
        fun `중복 자식이 없으면 정상 통과한다`() {
            val parent = Category.of(name = "전자제품")

            parent.hasNoDuplicateChild("스마트폰")
            // 예외가 발생하지 않으면 성공
        }

        @Test
        fun `중복 자식이 있으면 ConflictException을 던진다`() {
            val parent = Category.of(name = "전자제품")
            val child = Category.of(name = "스마트폰", parent = parent)
            parent.children.add(child)

            assertThatThrownBy { parent.hasNoDuplicateChild("스마트폰") }
                .isInstanceOf(ConflictException::class.java)
        }
    }

    @Nested
    @DisplayName("addChild 메서드는")
    inner class AddChildTest {

        @Test
        fun `자식 카테고리를 추가한다`() {
            val parent = Category.of(name = "전자제품")
            val child = Category.of(name = "스마트폰", parent = parent)

            parent.addChild(child)

            assertThat(parent.children).hasSize(1)
            assertThat(parent.children.first().name).isEqualTo("스마트폰")
        }

        @Test
        fun `중복 이름의 자식을 추가하면 예외를 던진다`() {
            val parent = Category.of(name = "전자제품")
            val child1 = Category.of(name = "스마트폰", parent = parent)
            parent.addChild(child1)

            val child2 = Category.of(name = "스마트폰", parent = parent)

            assertThatThrownBy { parent.addChild(child2) }
                .isInstanceOf(ConflictException::class.java)
        }
    }

    @Nested
    @DisplayName("createTree 메서드는")
    inner class CreateTreeTest {

        @Test
        fun `단일 노드 트리를 생성한다`() {
            val spec = CategoryTreeSpec(name = "패션")

            val root = Category.createTree(spec)

            assertThat(root.name).isEqualTo("패션")
            assertThat(root.depth).isEqualTo(0)
            assertThat(root.children).isEmpty()
        }

        @Test
        fun `계층형 트리를 생성한다`() {
            val spec = CategoryTreeSpec(
                name = "패션",
                children = listOf(
                    CategoryTreeSpec(name = "남성"),
                    CategoryTreeSpec(name = "여성"),
                ),
            )

            val root = Category.createTree(spec)

            assertThat(root.name).isEqualTo("패션")
            assertThat(root.children).hasSize(2)
            assertThat(root.children[0].name).isEqualTo("남성")
            assertThat(root.children[0].depth).isEqualTo(1)
            assertThat(root.children[1].name).isEqualTo("여성")
        }

        @Test
        fun `3단계 트리를 생성한다`() {
            val spec = CategoryTreeSpec(
                name = "패션",
                children = listOf(
                    CategoryTreeSpec(
                        name = "남성",
                        children = listOf(
                            CategoryTreeSpec(name = "상의"),
                            CategoryTreeSpec(name = "하의"),
                        ),
                    ),
                ),
            )

            val root = Category.createTree(spec)

            assertThat(root.children).hasSize(1)
            assertThat(root.children[0].children).hasSize(2)
            assertThat(root.children[0].children[0].depth).isEqualTo(2)
        }

        @Test
        fun `같은 부모 아래 중복 이름의 자식을 생성하면 예외를 던진다`() {
            val spec = CategoryTreeSpec(
                name = "패션",
                children = listOf(
                    CategoryTreeSpec(name = "남성"),
                    CategoryTreeSpec(name = "남성"),
                ),
            )

            assertThatThrownBy { Category.createTree(spec) }
                .isInstanceOf(ConflictException::class.java)
        }
    }
}
