package com.koosco.common.core.annotation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [EnumIfPresentValidator::class])
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnumIfPresent(
    val enumClass: KClass<out Enum<*>>,
    val message: String = "invalid enum value",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class EnumIfPresentValidator : ConstraintValidator<EnumIfPresent, String?> {

    private lateinit var enumValues: Set<String>

    override fun initialize(annotation: EnumIfPresent) {
        enumValues = annotation.enumClass.java.enumConstants
            .map { it.name }
            .toSet()
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return true
        return enumValues.contains(value)
    }
}
