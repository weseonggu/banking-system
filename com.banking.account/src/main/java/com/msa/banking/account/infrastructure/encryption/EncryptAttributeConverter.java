package com.msa.banking.account.infrastructure.encryption;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Component
@Converter
public class EncryptAttributeConverter implements AttributeConverter<String, String> {

    private final String secretKey = "1234567890123456"; // 16바이트 키

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        try {
            // EncryptionUtil의 암호화 메서드 호출
            return EncryptionUtil.encrypt(attribute, secretKey);
        } catch (Exception e) {
            throw new RuntimeException("Encryption error", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            // EncryptionUtil의 복호화 메서드 호출
            return EncryptionUtil.decrypt(dbData, secretKey);
        } catch (Exception e) {
            throw new RuntimeException("Decryption error", e);
        }
    }
}