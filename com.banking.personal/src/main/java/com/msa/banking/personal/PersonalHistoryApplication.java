package com.msa.banking.personal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication(scanBasePackages = {
        "com.msa.banking.personal",
        "com.msa.banking.commonbean"
})
@EnableFeignClients
@EnableJpaAuditing
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class PersonalHistoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonalHistoryApplication.class, args);
    }

}
