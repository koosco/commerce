package com.koosco.userservice.infra.persist.converter

import com.koosco.userservice.domain.vo.Email
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class EmailConverter : AttributeConverter<Email, String> {
    override fun convertToDatabaseColumn(attribute: Email?): String? = attribute?.value

    override fun convertToEntityAttribute(dbData: String?): Email? = dbData?.let { Email.of(it) }
}
