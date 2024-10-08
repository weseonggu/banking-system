package com.msa.banking.account.infrastructure.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasyptConfig {

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor(@Value("${ENCRYPTOR_PASSWORD}") String encryptorPassword) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(encryptorPassword); // 환경 변수에서 주입받은 비밀번호 사용
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        return encryptor;
    }
}