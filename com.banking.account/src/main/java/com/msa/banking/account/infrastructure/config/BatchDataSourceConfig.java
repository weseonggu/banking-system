package com.msa.banking.account.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

// 별도의 batchDataSource를 구성해 배치 작업이 메인 데이터베이스와 독립적으로 동작하도록 함
@Configuration
public class BatchDataSourceConfig {

    // HikariCP는 성능 최적화에 중점을 둔 커넥션 풀로, 속도와 효율성이 우수
    // 대규모 트래픽을 처리해야 하는 서비스에서 뛰어난 성능을 발휘
    @Bean
    @ConfigurationProperties(prefix = "spring.batch.datasource")
    public DataSource batchDataSource() {

        return DataSourceBuilder.create().build();
    }

    @Bean(name="batchTransactionManager")
    public PlatformTransactionManager batchTransactionManager() {
        return new DataSourceTransactionManager(batchDataSource());
    }
}
