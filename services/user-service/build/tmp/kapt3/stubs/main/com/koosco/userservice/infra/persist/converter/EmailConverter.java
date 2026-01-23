package com.koosco.userservice.infra.persist.converter;

@jakarta.persistence.Converter(autoApply = true)
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\t\b\u0007\u0018\u00002\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u0001B\u0005\u00a2\u0006\u0002\u0010\u0004J\u001e\u0010\u0005\u001a\u0004\u0018\u00010\u00032\b\u0010\u0006\u001a\u0004\u0018\u00010\u0002H\u0016\u00f8\u0001\u0000\u00a2\u0006\u0004\b\u0007\u0010\bJ!\u0010\t\u001a\u0004\u0018\u00010\u00022\b\u0010\n\u001a\u0004\u0018\u00010\u0003H\u0016\u00f8\u0001\u0001\u00f8\u0001\u0000\u00a2\u0006\u0004\b\u000b\u0010\b\u0082\u0002\u000b\n\u0005\b\u00a1\u001e0\u0001\n\u0002\b!\u00a8\u0006\f"}, d2 = {"Lcom/koosco/userservice/infra/persist/converter/EmailConverter;", "Ljakarta/persistence/AttributeConverter;", "Lcom/koosco/userservice/domain/vo/Email;", "", "()V", "convertToDatabaseColumn", "attribute", "convertToDatabaseColumn-ENSCLKQ", "(Ljava/lang/String;)Ljava/lang/String;", "convertToEntityAttribute", "dbData", "convertToEntityAttribute-VE8xAMI", "user-service"})
public final class EmailConverter implements jakarta.persistence.AttributeConverter<com.koosco.userservice.domain.vo.Email, java.lang.String> {
    
    public EmailConverter() {
        super();
    }
}