package com.msa.banking.account.infrastructure.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing(modular = true)
public class BatchConfig {

    @Qualifier("batchDataSource")
    private final DataSource batchDataSource;

    // PlatformTransactionManager: Spring 프레임워크에서 트랜잭션 관리를 담당하는 인터페이스
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(batchDataSource);
    }

    // JobRepository: 배치 작업의 메타데이터를 관리하는 핵심 컴포넌트
    // 필드를 직접 주입할 경우, 생성 순서나 의존성 주입의 명확성이 떨어질 수 있다.
    // 빈 주입 방식을 사용하면 Spring이 자동으로 주입 순서를 관리하고, 코드의 가독성과 유지보수성이 향상된다
    @Bean(name = "customJobRepository")
    public JobRepository jobRepository(PlatformTransactionManager transactionManager) throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(batchDataSource);
        factory.setTransactionManager(transactionManager);// 빈에서 주입된 transactionManager 사용
        factory.setIsolationLevelForCreate("ISOLATION_SERIALIZABLE");
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    // JobLauncher: 실제로 배치 작업을 시작하는 역할
    @Bean(name = "customJobLauncher")
    public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher(); // JobLauncher의 구현체
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor()); // 비동기 처리
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }
}