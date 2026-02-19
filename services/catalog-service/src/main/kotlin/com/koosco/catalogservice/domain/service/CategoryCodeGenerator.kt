package com.koosco.catalogservice.domain.service

import java.util.UUID

object CategoryCodeGenerator {
    fun generate(name: String): String {
        // 예: "MEN TOPS" → "MEN_TOPS_5F1A"
        val prefix = name.uppercase().replace(" ", "_")
        val random = UUID.randomUUID().toString().substring(0, 4).uppercase()
        return "${prefix}_$random"
    }
}
