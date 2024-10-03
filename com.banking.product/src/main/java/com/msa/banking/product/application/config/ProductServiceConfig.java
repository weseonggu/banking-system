package com.msa.banking.product.application.config;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@Configuration
public class ProductServiceConfig {



//    @Bean
//    public MultipartConfigElement multipartConfigElement() {
//        MultipartConfigFactory factory = new MultipartConfigFactory();
//        factory.setLocation("G:\\uploadFolder");
//        factory.setMaxRequestSize(DataSize.ofMegabytes(50L));
//        factory.setMaxFileSize(DataSize.ofMegabytes(10L));
//
//        return factory.createMultipartConfig();
//    }

}
