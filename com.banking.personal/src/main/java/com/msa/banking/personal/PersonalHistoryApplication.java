package com.msa.banking.personal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = {
        "com.msa.banking.personal",
        "com.msa.banking.commonbean"
})
@EnableFeignClients
@EnableJpaAuditing
public class PersonalHistoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonalHistoryApplication.class, args);
    }

}
