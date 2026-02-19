package com.koosco.catalogservice.domain.service

import com.koosco.catalogservice.domain.vo.OptionGroupCreateSpec
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ProductValidator {

    companion object {
        private const val MAX_OPTION_GROUPS = 5
        private const val MAX_OPTIONS_PER_GROUP = 20
        private const val MAX_SKU_COUNT = 500
        private const val RECOMMENDED_MAX_SKU_COUNT = 100
    }

    /**
     * 상품 생성 시 SKU 개수 검증
     *
     * @throws IllegalArgumentException SKU 개수가 제한을 초과하는 경우
     */
    fun validateSkuCount(optionGroupSpecs: List<OptionGroupCreateSpec>) {
        if (optionGroupSpecs.isEmpty()) {
            return
        }

        // 옵션 그룹 개수 검증
        require(optionGroupSpecs.size <= MAX_OPTION_GROUPS) {
            "옵션 그룹은 최대 ${MAX_OPTION_GROUPS}개까지만 생성할 수 있습니다. (현재: ${optionGroupSpecs.size}개)"
        }

        // 각 그룹의 옵션 개수 검증
        optionGroupSpecs.forEach { group ->
            require(group.options.size <= MAX_OPTIONS_PER_GROUP) {
                "옵션 그룹 '${group.name}'의 옵션은 최대 ${MAX_OPTIONS_PER_GROUP}개까지만 생성할 수 있습니다. " +
                    "(현재: ${group.options.size}개)"
            }
        }

        // 예상 SKU 개수 계산 (Cartesian Product)
        val expectedSkuCount = optionGroupSpecs
            .map { it.options.size }
            .reduce { acc, size -> acc * size }

        // SKU 개수 검증
        require(expectedSkuCount <= MAX_SKU_COUNT) {
            "생성 가능한 SKU 개수가 제한을 초과합니다. " +
                "(예상: ${expectedSkuCount}개, 최대: ${MAX_SKU_COUNT}개)\n" +
                "옵션 그룹 구조를 재검토해주세요."
        }

        // 권장 개수 초과 시 경고 로그
        if (expectedSkuCount > RECOMMENDED_MAX_SKU_COUNT) {
            LoggerFactory.getLogger(javaClass).warn(
                "생성되는 SKU 개수가 권장 개수를 초과합니다. " +
                    "(예상: ${expectedSkuCount}개, 권장: ${RECOMMENDED_MAX_SKU_COUNT}개)\n" +
                    "성능 문제가 발생할 수 있습니다.",
            )
        }
    }

    /**
     * 옵션 그룹 구조 검증 (일반적인 실수 방지)
     *
     * 예: "RED", "BLUE" 등이 옵션 그룹 이름으로 사용되는 경우
     */
    fun validateOptionGroupStructure(optionGroupSpecs: List<OptionGroupCreateSpec>) {
        // 옵션 그룹 이름이 색상, 사이즈 등 일반적인 값인지 확인
        val suspiciousNames = listOf(
            "RED", "BLUE", "BLACK", "WHITE", "GRAY", "GREEN", "YELLOW", "ORANGE",
            "XS", "S", "M", "L", "XL", "XXL", "2XL", "3XL",
        )

        optionGroupSpecs.forEach { group ->
            if (group.name.uppercase() in suspiciousNames) {
                LoggerFactory.getLogger(javaClass).warn(
                    "옵션 그룹 이름 '${group.name}'이 의심스럽습니다. " +
                        "옵션 그룹 이름은 '색상', '사이즈' 등의 카테고리명이어야 하며, " +
                        "개별 옵션 값('RED', 'XS' 등)이 아닙니다.",
                )
            }
        }
    }
}
