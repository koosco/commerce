package com.koosco.catalogservice.domain.entity

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.koosco.catalogservice.domain.enums.SkuStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "product_skus")
class ProductSku(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    /** 문자열 기반 SKU ID */
    @Column(name = "sku_id", nullable = false, unique = true, length = 100)
    val skuId: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product,

    /**
     * 장바구니, 주문 생성, 재고 감소 등 "실거래에 사용되는 가격"
     */
    @Column(nullable = false)
    val price: Long,

    /** 옵션 조합 — JSON 형태 */
    @Column(name = "option_values", columnDefinition = "JSON")
    val optionValues: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: SkuStatus = SkuStatus.ACTIVE,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {

    fun deactivate() {
        this.status = SkuStatus.DEACTIVATED
    }

    fun isActive(): Boolean = status == SkuStatus.ACTIVE

    companion object {
        private val objectMapper: ObjectMapper = jacksonObjectMapper()

        fun create(product: Product, options: Map<String, String>, price: Long): ProductSku {
            val skuId = generate(product.productCode, options)
            // 옵션을 JSON 문자열로 변환 (정렬된 순서 유지)
            val sortedOptions = options.entries
                .sortedBy { it.key }
                .associateTo(linkedMapOf()) { it.key to it.value } // LinkedHashMap 사용
            val optionValues = objectMapper.writeValueAsString(sortedOptions)

            return ProductSku(
                skuId = skuId,
                product = product,
                price = price,
                optionValues = optionValues,
            )
        }

        fun generate(productCode: String, options: Map<String, String>): String {
            val optionString = options.entries
                .sortedBy { it.key }
                .joinToString("-") { it.value }

            val hash = optionString.hashCode().toString(16).uppercase()

            return "$productCode-$optionString-$hash"
        }
    }
}
