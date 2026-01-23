package com.koosco.authservice.infra.persist.converter

import com.koosco.authservice.domain.vo.EncryptedPassword
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class EncryptPasswordConverter : AttributeConverter<EncryptedPassword, String> {
    override fun convertToDatabaseColumn(attribute: EncryptedPassword?): String? = attribute?.value

    override fun convertToEntityAttribute(dbData: String?): EncryptedPassword? = dbData?.let { EncryptedPassword(it) }
}
