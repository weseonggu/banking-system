package com.msa.banking.account.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.batch.BatchDataSourceScriptDatabaseInitializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

// 별도의 batchDataSource를 구성해 배치 작업이 메인 데이터베이스와 독립적으로 동작하도록 함
@Slf4j
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.msa.banking.account.infrastructure.repository", // 배치 리포지토리가 위치한 패키지
        entityManagerFactoryRef = "batchEntityManager", // 배치용 EntityManager
        transactionManagerRef = "batchTransactionManager" // 배치용 트랜잭션 매니저
)
public class BatchDataSourceConfig {

    // HikariCP는 성능 최적화에 중점을 둔 커넥션 풀로, 속도와 효율성이 우수
    // 대규모 트래픽을 처리해야 하는 서비스에서 뛰어난 성능을 발휘
    @Bean(name = "batchDataSource")
    @ConfigurationProperties(prefix = "spring.batch.datasource")
    public DataSource batchDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "batchEntityManager")
    public LocalContainerEntityManagerFactoryBean batchEntityManager(
            @Qualifier("batchDataSource") DataSource batchDataSource,
            JpaVendorAdapter jpaVendorAdapter) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(batchDataSource);
        em.setPackagesToScan("com.msa.banking.account.domain.model"); // 배치 엔티티가 위치한 패키지
        em.setJpaVendorAdapter(jpaVendorAdapter);
        return em;
    }


    @Bean(name = "batchTransactionManager")
    public PlatformTransactionManager batchTransactionManager(
            @Qualifier("batchDataSource") DataSource batchDataSource) {
        log.info("batch 트랜잭션 매니저 확인");
        return new DataSourceTransactionManager(batchDataSource);
    }

    @Bean(name = "batchJobRepository")
    @DependsOn({"batchDataSource", "batchTransactionManager"})
    public JobRepository jobRepository(
            @Qualifier("batchDataSource") DataSource batchDataSource,
            @Qualifier("batchTransactionManager") PlatformTransactionManager transactionManager) throws Exception {

        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(batchDataSource);
        factory.setTransactionManager(transactionManager);

        // 자동으로 데이터베이스 타입 감지
        factory.setIsolationLevelForCreate("ISOLATION_DEFAULT");

        factory.afterPropertiesSet();
        return factory.getObject();
    }
}