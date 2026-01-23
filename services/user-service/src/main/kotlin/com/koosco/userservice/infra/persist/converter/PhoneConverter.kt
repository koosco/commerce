package com.koosco.userservice.infra.persist.converter

import com.koosco.userservice.domain.vo.Phone
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class PhoneConverter : AttributeConverter<Phone, String> {
    override fun convertToDatabaseColumn(attribute: Phone?): String? = attribute?.value

    override fun convertToEntityAttribute(dbData: String?): Phone? = dbData?.let { Phone.of(it) }
}
