package com.msa.banking.account.infrastructure.batch;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

// 별도의 batchDataSource를 구성해 배치 작업이 메인 데이터베이스와 독립적으로 동작하도록 함
@Configuration
public class DataSourceConfig  {

    @Primary
    @Bean(name = "dataSource")  // Spring 기본 데이터 소스
    public HikariDataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl("jdbc:postgresql://localhost:5433/account");
        config.setUsername("banking");
        config.setPassword("banking20");
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        return new HikariDataSource(config);
    }

    @Bean(name = "batchDataSource")
    public HikariDataSource batchDataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl("jdbc:postgresql://localhost:5433/batch_db");
        config.setUsername("banking");
        config.setPassword("banking20");
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        return new HikariDataSource(config);
    }
}
