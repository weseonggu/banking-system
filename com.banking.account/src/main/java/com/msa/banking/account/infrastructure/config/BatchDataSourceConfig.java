package com.msa.banking.account.infrastructure.config;

import org.springframework.beans.factory.annotation.Qualifier;
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

    /**
     * 이 방식으로는 batchDataSource가 제대로 빈으로 등록되기 전에 호출될 수 있는 문제가 발생할 수 있습니다.
     * 따라서 @Qualifier를 사용하여 batchDataSource 빈을 명시적으로 주입
     */
    @Bean(name="batchTransactionManager")
    public PlatformTransactionManager batchTransactionManager(
            @Qualifier("batchDataSource") DataSource batchDataSource) {
        return new DataSourceTransactionManager(batchDataSource);
    }
}