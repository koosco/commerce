package com.koosco.userservice.infra.persist.converter

import com.koosco.userservice.domain.vo.EncryptedPassword
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class EncryptedPasswordConverter : AttributeConverter<EncryptedPassword, String> {
    override fun convertToDatabaseColumn(attribute: EncryptedPassword?): String? = attribute?.value

    override fun convertToEntityAttribute(dbData: String?): EncryptedPassword? = dbData?.let { EncryptedPassword(it) }
}
