package com.msa.banking.account.infrastructure.config;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class HibernateConfig implements HibernatePropertiesCustomizer {

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        // 커스텀 ID 생성기를 Hibernate에 등록
        hibernateProperties.put("hibernate.id.custom-id-generator", "com.msa.banking.account.infrastructure.idgenerator.CustomIdGenerator");
    }
}