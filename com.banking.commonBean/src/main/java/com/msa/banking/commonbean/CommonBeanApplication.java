package com.msa.banking.commonbean;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CommonBeanApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommonBeanApplication.class, args);
    }

}
