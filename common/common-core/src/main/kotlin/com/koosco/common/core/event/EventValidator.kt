package com.koosco.common.core.event

/**
 * Validator for CloudEvents compliance.
 * Validates events against CloudEvents v1.0 specification.
 */
object EventValidator {
    /**
     * Validate a CloudEvent for compliance with CloudEvents specification.
     *
     * @param event The CloudEvent to validate
     * @return ValidationResult indicating success or failure with reasons
     */
    fun <T> validate(event: CloudEvent<T>): ValidationResult {
        val errors = mutableListOf<String>()

        // Required fields validation
        if (event.id.isBlank()) {
            errors.add("CloudEvent 'id' must not be blank")
        }

        if (event.source.isBlank()) {
            errors.add("CloudEvent 'source' must not be blank")
        } else if (!isValidUri(event.source)) {
            errors.add("CloudEvent 'source' must be a valid URI-reference: ${event.source}")
        }

        if (event.type.isBlank()) {
            errors.add("CloudEvent 'type' must not be blank")
        }

        if (event.specVersion != "1.0") {
            errors.add("CloudEvent 'specversion' must be '1.0', got: ${event.specVersion}")
        }

        // Optional fields validation
        event.dataSchema?.let {
            if (it.isNotBlank() && !isValidUri(it)) {
                errors.add("CloudEvent 'dataschema' must be a valid URI: $it")
            }
        }

        event.dataContentType?.let {
            if (it.isNotBlank() && !isValidContentType(it)) {
                errors.add("CloudEvent 'datacontenttype' must be a valid MIME type: $it")
            }
        }

        return if (errors.isEmpty()) {
            ValidationResult.success()
        } else {
            ValidationResult.failure(errors)
        }
    }

    /**
     * Validate a DomainEvent.
     *
     * @param event The DomainEvent to validate
     * @return ValidationResult indicating success or failure with reasons
     */
    fun validate(event: DomainEvent): ValidationResult {
        val errors = mutableListOf<String>()

        if (event.eventId.isBlank()) {
            errors.add("DomainEvent 'eventId' must not be blank")
        }

        if (event.getEventType().isBlank()) {
            errors.add("DomainEvent 'eventType' must not be blank")
        }

        if (event.getAggregateId().isBlank()) {
            errors.add("DomainEvent 'aggregateId' must not be blank")
        }

        return if (errors.isEmpty()) {
            ValidationResult.success()
        } else {
            ValidationResult.failure(errors)
        }
    }

    /**
     * Check if a string is a valid URI-reference (RFC 3986).
     * Basic validation for common URI formats.
     */
    private fun isValidUri(uri: String): Boolean {
        if (uri.isBlank()) return false

        // Allow URN format: urn:namespace:specific-string
        if (uri.startsWith("urn:")) {
            return uri.split(":").size >= 3
        }

        // Allow HTTP/HTTPS URLs
        if (uri.startsWith("http://") || uri.startsWith("https://")) {
            return try {
                java.net.URI(uri)
                true
            } catch (e: Exception) {
                false
            }
        }

        // Allow relative paths and other URI formats
        return uri.matches(Regex("^[a-zA-Z][a-zA-Z0-9+.-]*:.*"))
    }

    /**
     * Check if a string is a valid MIME type (RFC 2046).
     */
    private fun isValidContentType(contentType: String): Boolean = contentType.matches(Regex("^[a-zA-Z0-9][a-zA-Z0-9!#$&^_.+-]{0,126}/[a-zA-Z0-9][a-zA-Z0-9!#$&^_.+-]{0,126}$"))
}

/**
 * Result of event validation.
 */
data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String> = emptyList(),
) {
    /**
     * Throw an exception if validation failed.
     *
     * @throws ValidationException if validation failed
     */
    fun throwIfInvalid() {
        if (!isValid) {
            throw ValidationException(errors.joinToString("; "))
        }
    }

    companion object {
        fun success(): ValidationResult = ValidationResult(true)

        fun failure(errors: List<String>): ValidationResult = ValidationResult(false, errors)

        fun failure(error: String): ValidationResult = ValidationResult(false, listOf(error))
    }
}

/**
 * Exception thrown when event validation fails.
 */
class ValidationException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
